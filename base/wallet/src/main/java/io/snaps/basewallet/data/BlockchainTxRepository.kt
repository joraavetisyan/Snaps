package io.snaps.basewallet.data

import io.horizontalsystems.ethereumkit.api.jsonrpc.models.RpcTransactionReceipt
import io.horizontalsystems.ethereumkit.core.LegacyGasPriceProvider
import io.horizontalsystems.ethereumkit.core.hexStringToByteArray
import io.horizontalsystems.ethereumkit.core.toHexString
import io.horizontalsystems.ethereumkit.models.Address
import io.horizontalsystems.ethereumkit.models.GasPrice
import io.horizontalsystems.ethereumkit.models.TransactionData
import io.snaps.basewallet.data.model.SignatureRequestDto
import io.snaps.basewallet.domain.NftMintSummary
import io.snaps.basewallet.domain.NoEnoughBnbToMint
import io.snaps.basewallet.domain.NoEnoughSnpToRepair
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.model.AppError
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.NftModel
import io.snaps.corecommon.model.NftType
import io.snaps.corecommon.model.TxHash
import io.snaps.corecommon.model.WalletAddress
import io.snaps.corecommon.model.WalletModel
import io.snaps.corecrypto.core.CryptoKit
import io.snaps.corecrypto.core.ISendEthereumAdapter
import io.snaps.corecrypto.core.adapters.Eip20Adapter
import io.snaps.corecrypto.core.managers.SNAPS_NFT
import io.snaps.corecrypto.entities.Wallet
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.network.apiCall
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import java.math.BigInteger
import javax.inject.Inject

interface BlockchainTxRepository {

    suspend fun getLegacyGasPrice(wallet: WalletModel): Effect<Long>

    suspend fun send(
        wallet: WalletModel,
        walletAddress: WalletAddress,
        amount: BigInteger,
        gasPrice: Long?,
        gasLimit: Long,
        data: ByteArray = byteArrayOf(),
    ): Effect<TxHash>

    suspend fun repairNft(nftModel: NftModel): Effect<TxHash>

    suspend fun getNftMintSummary(nftType: NftType): Effect<NftMintSummary>

    suspend fun mintNft(nftType: NftType, summary: NftMintSummary): Effect<TxHash>

    suspend fun calculateGasLimit(
        wallet: WalletModel,
        address: WalletAddress,
        value: BigInteger,
        gasPrice: Long?
    ): Effect<Long>

    suspend fun getProfitWalletAddress(): Effect<WalletAddress>
}

class BlockchainTxRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val walletApi: WalletApi,
    private val walletRepository: WalletRepository,
) : BlockchainTxRepository {

    private val gasPrice = GasPrice.Legacy(10_000_000_000L)

    override suspend fun send(
        wallet: WalletModel,
        walletAddress: String,
        amount: BigInteger,
        gasPrice: Long?,
        gasLimit: Long,
        data: ByteArray
    ): Effect<TxHash> {
        return blockchainCall(ioDispatcher) {
            val adapter = requireEthereumAdapter(wallet.coinUid)
            val address = Address(walletAddress)
            val txData = adapter.evmKitWrapper.evmKit.transferTransactionData(
                address = address, value = amount
            )
            adapter.evmKitWrapper.sendSingle(
                transactionData = txData,
                gasPrice = gasPrice.toLegacyGasPriceOrDefault(),
                gasLimit = gasLimit,
            ).blockingGet().transaction.hash.toHexString()
        }
    }

    override suspend fun getLegacyGasPrice(wallet: WalletModel): Effect<Long> {
        return blockchainCall(ioDispatcher) {
            LegacyGasPriceProvider(
                requireEthereumAdapter(wallet.coinUid).evmKitWrapper.evmKit
            ).gasPriceSingle().blockingGet()
        }
    }

    private fun ethereumAdapter(coinUid: String): ISendEthereumAdapter? {
        return walletRepository.getWallets().firstOrNull { it.coin.uid == coinUid }?.let {
            CryptoKit.adapterManager.getAdapterForWallet(it) as ISendEthereumAdapter
        }
    }

    private fun requireEthereumAdapter(coinUid: String) = requireNotNull(ethereumAdapter(coinUid)) {
        "ISendEthereumAdapter is null for $coinUid"
    }

    private fun eip20Adapter(wallet: Wallet) =
        CryptoKit.adapterManager.getAdapterForWallet(wallet) as Eip20Adapter?

    private fun requireEip20Adapter(wallet: Wallet) = requireNotNull(eip20Adapter(wallet)) {
        "Eip20Adapter is null for $wallet"
    }

    override suspend fun repairNft(nftModel: NftModel): Effect<TxHash> {
        if ((walletRepository.getSnpWalletModel()?.coinValueDouble ?: 0.0) < nftModel.repairCost) {
            return Effect.error(AppError.Custom(cause = NoEnoughSnpToRepair))
        }

        val nonceRaw = getNonceRaw()

        return apiCall(ioDispatcher) {
            walletApi.getRepairSignature(body = SignatureRequestDto(nonce = nonceRaw, amount = nftModel.repairCost))
        }.flatMap { data ->
            blockchainCall(ioDispatcher) {
                fun error(): Nothing = throw IllegalStateException("Signature data null! $data")
                val wallet = requireSnapsWallet()
                val adapter = requireEip20Adapter(wallet)

                val address = Address(SNAPS_NFT)

                val method = RepairContractMethod(
                    owner = Address(walletRepository.requireActiveWalletReceiveAddress()),
                    fromAccountAmounts = data.amountReceiver?.let(::BigInteger) ?: error(),
                    deadline = data.deadline?.toBigInteger() ?: error(),
                    nonce = nonceRaw.toBigInteger(),
                    contract = data.contract?.let(::Address) ?: error(),
                    signature = data.signature?.hexStringToByteArray() ?: error(),
                    profitWallet = data.profitWallet?.let(::Address) ?: error(),
                )
                val encodedAbi: ByteArray = method.encodedABI()

                val approveGasLimitTD: TransactionData = adapter.eip20Kit.buildTransferTransactionData(
                    to = address,
                    value = nftModel.repairCost.applyDecimal(wallet),
                )
                val approveGasLimit = adapter.evmKit.estimateGas(
                    transactionData = approveGasLimitTD,
                    gasPrice = gasPrice,
                ).blockingGet()

                val approveTD = adapter.eip20Kit.buildApproveTransactionData(
                    spenderAddress = address,
                    amount = nftModel.repairCost.applyDecimal(wallet),
                )
                adapter.evmKitWrapper.sendSingle(
                    transactionData = approveTD,
                    gasPrice = gasPrice,
                    gasLimit = approveGasLimit,
                ).blockingGet()

                val repairGasLimitTD = TransactionData(
                    to = address,
                    value = BigInteger.ZERO,
                    input = encodedAbi,
                )
                val repairGasLimit = adapter.evmKit.estimateGas(
                    transactionData = repairGasLimitTD,
                    gasPrice = gasPrice,
                ).blockingGet()

                val repairTD = TransactionData(
                    to = address,
                    value = BigInteger.ZERO,
                    input = encodedAbi,
                )
                adapter.evmKitWrapper.sendSingle(
                    transactionData = repairTD,
                    gasPrice = gasPrice,
                    gasLimit = repairGasLimit,
                ).blockingGet().transaction.hash.toHexString()
            }
        }
    }

    private fun Double.applyDecimal(wallet: Wallet) = toBigDecimal()
        .movePointRight(wallet.decimal)
        .toBigInteger()

    override suspend fun getNftMintSummary(nftType: NftType): Effect<NftMintSummary> {
        val amount = 0.005
        if ((walletRepository.getBnbWalletModel()?.coinValueDouble ?: 0.0) < amount) {
            return Effect.error(AppError.Custom(cause = NoEnoughBnbToMint))
        }

        val nonceRaw = getNonceRaw()

        return apiCall(ioDispatcher) {
            walletApi.getMintSignature(body = SignatureRequestDto(nonce = nonceRaw, amount = amount))
        }.flatMap { data ->
            blockchainCall(ioDispatcher) {
                val wallet = requireSnapsWallet()
                val adapter = requireEip20Adapter(wallet)

                fun error(): Nothing = throw IllegalStateException("Signature data null! $data")

                val address = Address(SNAPS_NFT)
                val fromAddress = walletRepository.requireActiveWalletReceiveAddress()

                val method = MintContractMethod(
                    owner = Address(fromAddress),
                    fromAccountAmounts = data.amountReceiver?.let(::BigInteger) ?: error(),
                    deadline = data.deadline?.toBigInteger() ?: error(),
                    nonce = nonceRaw.toBigInteger(),
                    signature = data.signature?.hexStringToByteArray() ?: error(),
                    profitWallet = data.profitWallet?.let(::Address) ?: error(),
                )
                val encodedAbi: ByteArray = method.encodedABI()

                val valueApplied = amount.applyDecimal(wallet)
                val transactionData = TransactionData(
                    to = address,
                    value = valueApplied,
                    input = encodedAbi,
                )
                val gasLimit = adapter.evmKit.estimateGas(
                    transactionData = transactionData,
                    gasPrice = gasPrice,
                ).blockingGet()
                val gasPriceDecimal = gasPrice.max.toBigDecimal()
                    .movePointLeft(wallet.decimal)
                    .times(gasLimit.toBigDecimal())

                NftMintSummary(
                    from = fromAddress,
                    to = address.hex,
                    summary = amount.toBigDecimal(),
                    gas = gasPriceDecimal,
                    total = amount.toBigDecimal() + gasPriceDecimal,
                    gasLimit = gasLimit,
                    transactionData = transactionData,
                )
            }
        }
    }

    private fun getNonceRaw() = System.currentTimeMillis()

    override suspend fun mintNft(nftType: NftType, summary: NftMintSummary): Effect<TxHash> {
        return blockchainCall(ioDispatcher) {
            val adapter = requireEip20Adapter(requireSnapsWallet())
            val hash = adapter.evmKitWrapper.sendSingle(
                transactionData = summary.transactionData as TransactionData,
                gasPrice = gasPrice,
                gasLimit = summary.gasLimit,
            ).blockingGet().transaction.hash
            var receipt: RpcTransactionReceipt? = null
            while (receipt == null) {
                delay(1000L)
                // todo catch only rpc errors
                receipt = kotlin.runCatching { adapter.evmKit.getTransactionReceipt(hash).blockingGet() }.getOrNull()
            }
            hash.toHexString()
        }
    }

    private fun getSnapsWallet() = walletRepository.getWallets().firstOrNull { it.coin.code == "SNAPS" }

    private fun requireSnapsWallet() = requireNotNull(getSnapsWallet()) { "Snaps wallet is null!!!" }

    override suspend fun calculateGasLimit(
        wallet: WalletModel,
        address: WalletAddress,
        value: BigInteger,
        gasPrice: Long?,
    ): Effect<Long> {
        log("gas limit calc: $wallet, $address, $value, $gasPrice")
        return blockchainCall(ioDispatcher) {
            requireEthereumAdapter(wallet.coinUid).evmKitWrapper.evmKit.estimateGas(
                to = Address(address),
                value = value,
                gasPrice = gasPrice.toLegacyGasPriceOrDefault(),
            ).blockingGet()
        }
    }

    private fun Long?.toLegacyGasPriceOrDefault(): GasPrice.Legacy = this?.let(GasPrice::Legacy) ?: gasPrice

    override suspend fun getProfitWalletAddress(): Effect<WalletAddress> {
        return apiCall(ioDispatcher) {
            walletApi.getRepairSignature(SignatureRequestDto(nonce = 1L, amount = 10.0))
        }.map {
            it.profitWallet.orEmpty()
        }
    }
}