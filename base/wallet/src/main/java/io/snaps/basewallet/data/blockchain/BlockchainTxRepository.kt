package io.snaps.basewallet.data.blockchain

import io.horizontalsystems.ethereumkit.api.jsonrpc.models.RpcTransactionReceipt
import io.horizontalsystems.ethereumkit.core.LegacyGasPriceProvider
import io.horizontalsystems.ethereumkit.core.hexStringToByteArray
import io.horizontalsystems.ethereumkit.core.toHexString
import io.horizontalsystems.ethereumkit.models.Address
import io.horizontalsystems.ethereumkit.models.GasPrice
import io.horizontalsystems.ethereumkit.models.TransactionData
import io.snaps.basewallet.data.WalletApi
import io.snaps.basewallet.data.blockchainCall
import io.snaps.basewallet.data.model.SignatureRequestDto
import io.snaps.basewallet.domain.NftMintSummary
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.NftType
import io.snaps.corecommon.model.TxHash
import io.snaps.corecommon.model.CryptoAddress
import io.snaps.corecommon.model.Nft
import io.snaps.basewallet.domain.WalletModel
import io.snaps.corecommon.ext.applyDecimal
import io.snaps.corecommon.ext.logE
import io.snaps.corecommon.ext.unapplyDecimal
import io.snaps.corecommon.model.CoinType
import io.snaps.corecrypto.core.CryptoKit
import io.snaps.corecrypto.core.IAccountManager
import io.snaps.corecrypto.core.ISendEthereumAdapter
import io.snaps.corecrypto.core.IWalletManager
import io.snaps.corecrypto.core.adapters.Eip20Adapter
import io.snaps.corecrypto.entities.Account
import io.snaps.corecrypto.entities.Wallet
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.network.apiCall
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import java.math.BigInteger
import javax.inject.Inject

interface BlockchainTxRepository {

    suspend fun getLegacyGasPrice(wallet: WalletModel): Effect<Long>

    suspend fun getProfitWalletAddress(): Effect<CryptoAddress>

    suspend fun calculateGasLimit(
        wallet: WalletModel,
        address: CryptoAddress,
        value: BigInteger,
        gasPrice: Long?,
    ): Effect<Long>

    suspend fun send(
        wallet: WalletModel,
        address: CryptoAddress,
        amount: BigInteger,
        gasPrice: Long?,
        gasLimit: Long,
        data: ByteArray = byteArrayOf(),
    ): Effect<TxHash>

    suspend fun repairNft(repairCost: Double): Effect<TxHash>

    suspend fun getNftMintSummary(nftType: NftType, amount: Double): Effect<NftMintSummary>

    suspend fun mintNft(nftType: NftType, summary: NftMintSummary): Effect<TxHash>
}

class BlockchainTxRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,

    private val walletManager: IWalletManager,
    private val accountManager: IAccountManager,

    private val walletApi: WalletApi,
) : BlockchainTxRepository {

    private val defaultGasPrice = GasPrice.Legacy(10_000_000_000L)

    override suspend fun getLegacyGasPrice(wallet: WalletModel): Effect<Long> {
        return blockchainCall(ioDispatcher) {
            LegacyGasPriceProvider(
                requireEthereumAdapter(wallet.coinUid).evmKitWrapper.evmKit
            ).gasPriceSingle().blockingGet()
        }
    }

    override suspend fun calculateGasLimit(
        wallet: WalletModel,
        address: CryptoAddress,
        value: BigInteger,
        gasPrice: Long?,
    ): Effect<Long> {
        return blockchainCall(ioDispatcher) {
            if (wallet.coinType == CoinType.BNB) {
                requireEthereumAdapter(wallet.coinUid).evmKitWrapper.evmKit.estimateGas(
                    to = Address(address),
                    value = value,
                    gasPrice = gasPrice.toLegacyGasPriceOrDefault(),
                ).blockingGet()
            } else {
                val adapter = requireEip20Adapter(requireWallet(wallet.coinUid))
                val gasLimitTD: TransactionData = adapter.eip20Kit.buildTransferTransactionData(
                    to = Address(address),
                    value = value,
                )
                adapter.evmKitWrapper.evmKit.estimateGas(
                    transactionData = gasLimitTD,
                    gasPrice = gasPrice.toLegacyGasPriceOrDefault(),
                ).blockingGet()
            }
        }
    }

    override suspend fun getProfitWalletAddress(): Effect<CryptoAddress> {
        return apiCall(ioDispatcher) {
            walletApi.getRepairSignature(SignatureRequestDto(nonce = 1L, amount = 10.0))
        }.map {
            it.profitWallet.orEmpty()
        }
    }

    override suspend fun send(
        wallet: WalletModel,
        address: String,
        amount: BigInteger,
        gasPrice: Long?,
        gasLimit: Long,
        data: ByteArray
    ): Effect<TxHash> {
        return blockchainCall(ioDispatcher) {
            val adapter = requireEthereumAdapter(wallet.coinUid)
            val txData = adapter.evmKitWrapper.evmKit.transferTransactionData(
                address = Address(address), value = amount
            )
            adapter.evmKitWrapper.sendSingle(
                transactionData = txData,
                gasPrice = gasPrice.toLegacyGasPriceOrDefault(),
                gasLimit = gasLimit,
            ).blockingGet().transaction.hash.toHexString()
        }
    }

    private fun ethereumAdapter(coinUid: String): ISendEthereumAdapter? {
        return getWallet(coinUid)?.let {
            CryptoKit.adapterManager.getAdapterForWallet(it) as ISendEthereumAdapter
        }
    }

    private fun getWallet(coinUid: String) = getWallets().firstOrNull { it.coin.uid == coinUid }

    private fun requireWallet(coinUid: String) = requireNotNull(getWallet(coinUid)) {
        "Wallet is null for $coinUid"
    }

    private fun requireEthereumAdapter(coinUid: String) = requireNotNull(ethereumAdapter(coinUid)) {
        "ISendEthereumAdapter is null for $coinUid"
    }

    private fun eip20Adapter(wallet: Wallet) =
        CryptoKit.adapterManager.getAdapterForWallet(wallet) as Eip20Adapter?

    private fun requireEip20Adapter(wallet: Wallet) = requireNotNull(eip20Adapter(wallet)) {
        "Eip20Adapter is null for $wallet"
    }

    override suspend fun repairNft(repairCost: Double): Effect<TxHash> {
        val nonceRaw = getNonceRaw()

        return apiCall(ioDispatcher) {
            walletApi.getRepairSignature(body = SignatureRequestDto(nonce = nonceRaw, amount = repairCost))
        }.flatMap { data ->
            blockchainCall(ioDispatcher) {
                fun error(): Nothing = throw IllegalStateException("Signature data null! $data")
                val wallet = requireSnapsWallet()
                val adapter = requireEip20Adapter(wallet)

                val address = Address(Nft.SNAPS.address)

                val method = RepairContractMethod(
                    owner = Address(requireActiveWalletReceiveAddress()),
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
                    value = repairCost.applyDecimal(wallet.decimal),
                )
                val approveGasLimit = adapter.evmKit.estimateGas(
                    transactionData = approveGasLimitTD,
                    gasPrice = defaultGasPrice,
                ).blockingGet()

                val approveTD = adapter.eip20Kit.buildApproveTransactionData(
                    spenderAddress = address,
                    amount = repairCost.applyDecimal(wallet.decimal),
                )
                adapter.evmKitWrapper.sendSingle(
                    transactionData = approveTD,
                    gasPrice = defaultGasPrice,
                    gasLimit = approveGasLimit,
                ).blockingGet()

                val repairGasLimitTD = TransactionData(
                    to = address,
                    value = BigInteger.ZERO,
                    input = encodedAbi,
                )
                val repairGasLimit = adapter.evmKit.estimateGas(
                    transactionData = repairGasLimitTD,
                    gasPrice = defaultGasPrice,
                ).blockingGet()

                val repairTD = TransactionData(
                    to = address,
                    value = BigInteger.ZERO,
                    input = encodedAbi,
                )
                adapter.evmKitWrapper.sendSingle(
                    transactionData = repairTD,
                    gasPrice = defaultGasPrice,
                    gasLimit = repairGasLimit,
                ).blockingGet().transaction.hash.toHexString()
            }
        }
    }

    private fun getActiveWalletReceiveAddress(): CryptoAddress? {
        return getWallets().firstNotNullOfOrNull { wallet ->
            CryptoKit.adapterManager.getReceiveAdapterForWallet(wallet)?.receiveAddress.also {
                if (it == null) logE("No receive adapter for wallet $wallet!")
            }
        }
    }

    private fun requireActiveWalletReceiveAddress(): CryptoAddress {
        return requireNotNull(getActiveWalletReceiveAddress())
    }

    override suspend fun getNftMintSummary(nftType: NftType, amount: Double): Effect<NftMintSummary> {
        val nonceRaw = getNonceRaw()

        return apiCall(ioDispatcher) {
            walletApi.getMintSignature(body = SignatureRequestDto(nonce = nonceRaw, amount = amount))
        }.flatMap { data ->
            blockchainCall(ioDispatcher) {
                val wallet = requireSnapsWallet()
                val adapter = requireEip20Adapter(wallet)

                fun error(): Nothing = throw IllegalStateException("Signature data null! $data")

                val address = Address(Nft.SNAPS.address)
                val fromAddress = requireActiveWalletReceiveAddress()

                val method = MintContractMethod(
                    owner = Address(fromAddress),
                    fromAccountAmounts = data.amountReceiver?.let(::BigInteger) ?: error(),
                    deadline = data.deadline?.toBigInteger() ?: error(),
                    nonce = nonceRaw.toBigInteger(),
                    signature = data.signature?.hexStringToByteArray() ?: error(),
                    profitWallet = data.profitWallet?.let(::Address) ?: error(),
                )
                val encodedAbi: ByteArray = method.encodedABI()

                val valueApplied = amount.applyDecimal(wallet.decimal)
                val transactionData = TransactionData(
                    to = address,
                    value = valueApplied,
                    input = encodedAbi,
                )
                val gasLimit = adapter.evmKit.estimateGas(
                    transactionData = transactionData,
                    gasPrice = defaultGasPrice,
                ).blockingGet()
                val gasPriceDecimal = defaultGasPrice.max.unapplyDecimal(wallet.decimal)
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
                gasPrice = defaultGasPrice,
                gasLimit = summary.gasLimit,
            ).blockingGet().transaction.hash
            var receipt: RpcTransactionReceipt? = null
            // todo possible inf loop
            while (receipt == null) {
                delay(1000L)
                // todo catch only rpc errors
                receipt = kotlin.runCatching { adapter.evmKit.getTransactionReceipt(hash).blockingGet() }.getOrNull()
            }
            hash.toHexString()
        }
    }

    private fun getSnapsWallet() = getWallets().firstOrNull { it.coin.code == CoinType.SNPS.code }

    private fun requireSnapsWallet() = requireNotNull(getSnapsWallet()) { "Snaps wallet is null!!!" }

    private fun getWallets(): List<Wallet> {
        val account = getActiveAccount() ?: return emptyList()
        return walletManager.getWallets(account).also { if (it.isEmpty()) logE("No wallets!") }
    }

    private fun getActiveAccount(): Account? {
        return accountManager.activeAccount.also { if (it == null) logE("No active account!") }
    }

    private fun Long?.toLegacyGasPriceOrDefault(): GasPrice.Legacy = this?.let(GasPrice::Legacy) ?: defaultGasPrice
}