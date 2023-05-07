package io.snaps.basewallet.data

import android.annotation.SuppressLint
import io.horizontalsystems.ethereumkit.api.jsonrpc.JsonRpc
import io.horizontalsystems.ethereumkit.core.LegacyGasPriceProvider
import io.horizontalsystems.ethereumkit.core.eip1559.Eip1559GasPriceProvider
import io.horizontalsystems.ethereumkit.core.hexStringToByteArray
import io.horizontalsystems.ethereumkit.core.toHexString
import io.horizontalsystems.ethereumkit.models.Address
import io.horizontalsystems.ethereumkit.models.GasPrice
import io.horizontalsystems.ethereumkit.models.TransactionData
import io.horizontalsystems.marketkit.models.BlockchainType
import io.horizontalsystems.marketkit.models.TokenQuery
import io.horizontalsystems.marketkit.models.TokenType
import io.reactivex.disposables.CompositeDisposable
import io.snaps.basewallet.data.model.ClaimRequestDto
import io.snaps.basewallet.data.model.NftSignatureRequestDto
import io.snaps.basewallet.data.model.NftSignatureResponseDto
import io.snaps.basewallet.data.model.WalletSaveRequestDto
import io.snaps.basewallet.domain.DeviceNotSecuredException
import io.snaps.basewallet.domain.NftMintSummary
import io.snaps.basewallet.domain.NoEnoughBnbToMint
import io.snaps.basewallet.domain.NoEnoughSnpToRepair
import io.snaps.basewallet.domain.TotalBalanceModel
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.model.AppError
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.NftModel
import io.snaps.corecommon.model.NftType
import io.snaps.corecommon.model.Token
import io.snaps.corecommon.model.Uuid
import io.snaps.corecommon.model.WalletAddress
import io.snaps.corecommon.model.WalletModel
import io.snaps.corecrypto.core.AdapterState
import io.snaps.corecrypto.core.CryptoKit
import io.snaps.corecrypto.core.IAccountFactory
import io.snaps.corecrypto.core.IAccountManager
import io.snaps.corecrypto.core.ISendEthereumAdapter
import io.snaps.corecrypto.core.IWalletManager
import io.snaps.corecrypto.core.IWordsManager
import io.snaps.corecrypto.core.adapters.Eip20Adapter
import io.snaps.corecrypto.core.managers.SNAPS_NFT
import io.snaps.corecrypto.core.managers.WalletActivator
import io.snaps.corecrypto.core.managers.defaultTokens
import io.snaps.corecrypto.core.providers.BalanceService
import io.snaps.corecrypto.core.providers.BalanceViewItemFactory
import io.snaps.corecrypto.core.providers.Eip1559GasPriceService
import io.snaps.corecrypto.core.providers.EvmCommonGasDataService
import io.snaps.corecrypto.core.providers.EvmFeeService
import io.snaps.corecrypto.core.providers.IEvmGasPriceService
import io.snaps.corecrypto.core.providers.ITotalBalance
import io.snaps.corecrypto.core.providers.LegacyGasPriceService
import io.snaps.corecrypto.core.providers.PredefinedBlockchainSettingsProvider
import io.snaps.corecrypto.core.providers.SendEvmData
import io.snaps.corecrypto.core.providers.SendEvmTransactionService
import io.snaps.corecrypto.core.providers.TotalService
import io.snaps.corecrypto.core.providers.TotalUIState
import io.snaps.corecrypto.core.subscribeIO
import io.snaps.corecrypto.entities.Account
import io.snaps.corecrypto.entities.AccountOrigin
import io.snaps.corecrypto.entities.AccountType
import io.snaps.corecrypto.entities.Wallet
import io.snaps.corecrypto.entities.normalizeNFKD
import io.snaps.coredata.coroutine.ApplicationCoroutineScope
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.network.apiCall
import io.snaps.coreui.viewmodel.likeStateFlow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.math.BigInteger
import java.security.InvalidAlgorithmParameterException
import javax.inject.Inject

private const val messageNotSecured =
    "java.lang.IllegalStateException: Secure lock screen must be enabled to create keys requiring user authentication"

interface WalletRepository {

    val totalBalanceValue: StateFlow<TotalBalanceModel>

    val activeWallets: StateFlow<List<WalletModel>>

    fun createAccount(userId: Uuid): List<String>

    suspend fun importAccount(userId: Uuid, words: List<String>): Effect<Completable>

    suspend fun saveLastConnectedAccount(): Effect<Completable>

    fun hasAccount(userId: Uuid): Boolean

    fun setAccountActive(userId: Uuid)

    fun setAccountInactive()

    fun getMnemonics(): List<String>

    fun getActiveWalletReceiveAddress(): WalletAddress?

    fun requireActiveWalletReceiveAddress(): WalletAddress

    fun getAvailableBalance(wallet: WalletModel): String?

    fun send(
        amount: BigInteger,
        address: WalletAddress,
        wallet: WalletModel,
        data: ByteArray = byteArrayOf(),
    ): Effect<SendHandler>

    suspend fun claim(amount: Double): Effect<Completable>

    fun getBnbWalletModel(): WalletModel?

    fun getSnpWalletModel(): WalletModel?

    fun deleteAccount(id: Uuid)

    /**
     * returns: Repair transaction hash
     */
    suspend fun repairNft(nftModel: NftModel): Effect<Token>

    suspend fun getNftMintSummary(nftType: NftType): Effect<NftMintSummary>

    /**
     * returns: Mint transaction hash
     */
    suspend fun mintNft(nftType: NftType, summary: NftMintSummary): Effect<Token>
}

class WalletRepositoryImpl @Inject constructor(
    @ApplicationCoroutineScope private val scope: CoroutineScope,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val accountFactory: IAccountFactory,
    private val walletManager: IWalletManager,
    private val wordsManager: IWordsManager,
    private val accountManager: IAccountManager,
    private val walletActivator: WalletActivator,
    private val predefinedBlockchainSettingsProvider: PredefinedBlockchainSettingsProvider,
    private val walletApi: WalletApi,
    private val balanceService: BalanceService,
    private val balanceViewItemFactory: BalanceViewItemFactory,
    private val totalBalance: ITotalBalance,
) : WalletRepository {

    private val gasPrice = GasPrice.Legacy(10_000_000_000L)

    private var account: Account? = null
    private var lastSendHandler: SendHandler? = null

    private val _totalBalanceValue = MutableStateFlow(TotalBalanceModel.empty)
    override val totalBalanceValue = _totalBalanceValue.asStateFlow()

    override val activeWallets: StateFlow<List<WalletModel>> = balanceService.balanceItemsFlow
        .map { items ->
            totalBalance.setTotalServiceItems(items?.map {
                TotalService.BalanceItem(
                    it.balanceData.total,
                    it.state !is AdapterState.Synced,
                    it.coinPrice
                )
            })

            items?.map { balanceItem ->
                val item = balanceViewItemFactory.viewItem(
                    item = balanceItem,
                    currency = balanceService.baseCurrency,
                )
                WalletModel(
                    coinUid = item.wallet.coin.uid,
                    decimal = item.wallet.decimal,
                    symbol = item.coinCode,
                    iconUrl = item.coinIconUrl,
                    coinValue = item.primaryValue.value,
                    fiatValue = item.secondaryValue.value,
                    receiveAddress = CryptoKit.adapterManager.getReceiveAdapterForWallet(
                        item.wallet
                    )?.receiveAddress.orEmpty(),
                    coinAddress = item.wallet.tokenAddress,
                )
            } ?: emptyList()
        }.likeStateFlow(scope, emptyList())

    init {
        totalBalance.totalUiState.onEach { total ->
            _totalBalanceValue.update {
                if (total !is TotalUIState.Visible) return@update TotalBalanceModel.empty
                TotalBalanceModel(coin = total.primaryAmountStr, fiat = total.secondaryAmountStr)
            }
        }.launchIn(scope)
        balanceService.start()
        totalBalance.start(scope)
    }

    override fun createAccount(userId: Uuid): List<String> {
        val accountType = mnemonicAccountType(12)
        account = accountFactory.account(
            id = userId,
            name = "Wallet",
            type = accountType,
            origin = AccountOrigin.Created,
            backedUp = false,
        )
        return accountType.words
    }

    private fun mnemonicAccountType(wordCount: Int): AccountType.Mnemonic {
        // A new account can be created only using an English wordlist and limited chars in the passphrase.
        // Despite it, we add text normalizing.
        // It is to avoid potential issues if we allow non-English wordlists on account creation.
        val words = wordsManager.generateWords(wordCount).map { it.normalizeNFKD() }
        return AccountType.Mnemonic(words, "".normalizeNFKD())
    }

    override suspend fun importAccount(userId: Uuid, words: List<String>): Effect<Completable> {
        return try {
            wordsManager.validateChecksumStrict(words)
            val accountType = AccountType.Mnemonic(words, "".normalizeNFKD())
            account = accountFactory.account(
                id = userId,
                name = "Wallet",
                type = accountType,
                origin = AccountOrigin.Restored,
                backedUp = true,
            )
            saveLastConnectedAccount()
        } catch (e: Exception /*todo specify exception for check sum error*/) {
            Effect.error(AppError.Unknown(cause = e))
        }
    }

    override suspend fun saveLastConnectedAccount(): Effect<Completable> {
        val account = account ?: return Effect.error(AppError.Unknown("No account was created!"))
        try {
            accountManager.save(account)
        } catch (e: InvalidAlgorithmParameterException) {
            if (e.message == messageNotSecured) {
                return Effect.error(AppError.Custom(cause = DeviceNotSecuredException))
            }
            return Effect.error(AppError.Unknown(cause = e))
        }
        activateDefaultWallets(account)
        predefinedBlockchainSettingsProvider.prepareNew(account, BlockchainType.Zcash)
        // fixme better way
        while (getActiveWalletReceiveAddress() == null) {
            delay(200)
        }
        return apiCall(ioDispatcher) {
            walletApi.save(WalletSaveRequestDto(requireActiveWalletReceiveAddress()))
        }.toCompletable()
    }

    override fun hasAccount(userId: Uuid): Boolean {
        return accountManager.account(userId) != null
    }

    override fun setAccountActive(userId: Uuid) {
        accountManager.setActiveAccountId(userId)
    }

    override fun setAccountInactive() {
        accountManager.setActiveAccountId(null)
    }

    override fun getMnemonics(): List<String> {
        return getActiveAccount()?.let {
            (it.type as AccountType.Mnemonic).words
        } ?: emptyList()
    }

    private fun getActiveAccount(): Account? {
        return accountManager.activeAccount ?: run {
            log("No active account")
            null
        }
    }

    private fun getWallets(): List<Wallet> {
        val account = getActiveAccount() ?: return emptyList()
        return walletManager.getWallets(account)
    }

    override fun getBnbWalletModel(): WalletModel? {
        return activeWallets.value.firstOrNull {
            it.coinUid == "binancecoin"
        }
    }

    override fun getSnpWalletModel(): WalletModel? {
        return activeWallets.value.firstOrNull {
            it.symbol == "SNAPS"
        }
    }

    override fun deleteAccount(id: Uuid) {
        accountManager.delete(id)
    }

    override fun getActiveWalletReceiveAddress(): WalletAddress? {
        return getWallets().firstNotNullOfOrNull {
            CryptoKit.adapterManager.getReceiveAdapterForWallet(it)?.receiveAddress
        }
    }

    override fun requireActiveWalletReceiveAddress(): WalletAddress {
        return requireNotNull(getActiveWalletReceiveAddress()) {
            "No active wallet receive address"
        }
    }

    private fun activateDefaultWallets(account: Account) {
        val tokenQueries = defaultTokens.map {
            TokenQuery(BlockchainType.BinanceSmartChain, TokenType.Eip20(it))
        } + listOf(
            TokenQuery(BlockchainType.BinanceSmartChain, TokenType.Native)
        )
        walletActivator.activateWallets(account, tokenQueries)
    }

    override fun getAvailableBalance(wallet: WalletModel): String? {
        return adapter(wallet.coinUid)?.balanceData?.available?.toString()
    }

    @SuppressLint("CheckResult")
    override fun send(
        amount: BigInteger,
        address: WalletAddress,
        wallet: WalletModel,
        data: ByteArray,
    ): Effect<SendHandler> {
        lastSendHandler?.stop()

        val adapter = adapter(wallet.coinUid) ?: return Effect.error(
            AppError.Unknown(
                cause = IllegalStateException("Adapter is null")
            )
        )

        return try {
            val evmKitWrapper = adapter.evmKitWrapper
            val gasPriceService: IEvmGasPriceService = run {
                val evmKit = evmKitWrapper.evmKit
                if (evmKit.chain.isEIP1559Supported) {
                    val gasPriceProvider = Eip1559GasPriceProvider(evmKit)
                    Eip1559GasPriceService(gasPriceProvider, evmKit)
                } else {
                    val gasPriceProvider = LegacyGasPriceProvider(evmKit)
                    LegacyGasPriceService(gasPriceProvider)
                }
            }
            val sendEvmData = SendEvmData(
                transactionData = adapter.getTransactionData(
                    amount = amount,
                    address = Address(address),
                    data = data,
                ),
            )
            val feeService = run {
                val gasLimitSurchargePercent =
                    if (sendEvmData.transactionData.input.isEmpty()) 0 else 20
                val gasDataService = EvmCommonGasDataService.instance(
                    evmKit = evmKitWrapper.evmKit,
                    blockchainType = evmKitWrapper.blockchainType,
                    gasLimitSurchargePercent = gasLimitSurchargePercent
                )
                EvmFeeService(
                    evmKit = evmKitWrapper.evmKit,
                    gasPriceService = gasPriceService,
                    gasDataService = gasDataService,
                    transactionData = sendEvmData.transactionData,
                )
            }
            val service = SendEvmTransactionService(
                sendEvmData = sendEvmData,
                evmKitWrapper = evmKitWrapper,
                feeService = feeService,
                evmLabelManager = CryptoKit.evmLabelManager
            )
            Effect.success(SendHandlerImpl(service).also { lastSendHandler = it })
        } catch (e: Exception) {
            Effect.error(AppError.Unknown(cause = e))
        }
    }

    private fun adapter(coinUid: String): ISendEthereumAdapter? {
        return getWallets().firstOrNull { it.coin.uid == coinUid }?.let {
            CryptoKit.adapterManager.getAdapterForWallet(it) as ISendEthereumAdapter
        }
    }

    override suspend fun claim(amount: Double): Effect<Completable> {
        return apiCall(ioDispatcher) {
            walletApi.claim(ClaimRequestDto(amount))
        }
    }

    override suspend fun repairNft(nftModel: NftModel): Effect<Token> {
        if ((getSnpWalletModel()?.coinValueDouble ?: 0.0) < nftModel.repairCost) {
            return Effect.error(AppError.Custom(cause = NoEnoughSnpToRepair))
        }

        val wallet = getSnapsWallet() ?: return Effect.error(
            AppError.Unknown(cause = IllegalStateException("Wallet is null"))
        )
        val adapter = CryptoKit.adapterManager.getAdapterForWallet(wallet) as Eip20Adapter?
            ?: return Effect.error(
                AppError.Unknown(cause = IllegalStateException("Adapter is null"))
            )

        val nonceRaw = System.currentTimeMillis()

        return apiCall(ioDispatcher) {
            walletApi.getRepairSignature(
                body = NftSignatureRequestDto(
                    nonce = nonceRaw,
                    amount = nftModel.repairCost,
                ),
            )
        }.flatMap {
            withContext(ioDispatcher) {
                repair(
                    data = it,
                    nonceRaw = nonceRaw,
                    wallet = wallet,
                    adapter = adapter,
                    nftModel = nftModel,
                )
            }
        }
    }

    private suspend fun repair(
        data: NftSignatureResponseDto,
        nonceRaw: Long,
        wallet: Wallet,
        adapter: Eip20Adapter,
        nftModel: NftModel,
    ): Effect<String> {
        return try {
            val error = Effect.error<String>(
                AppError.Unknown(cause = IllegalStateException("Signature data null! $data"))
            )

            val address = Address(SNAPS_NFT)

            val method = RepairContractMethod(
                owner = Address(requireActiveWalletReceiveAddress()),
                fromAccountAmounts = data.amountReceiver?.let(::BigInteger) ?: return error,
                deadline = data.deadline?.toBigInteger() ?: return error,
                nonce = nonceRaw.toBigInteger(),
                contract = data.contract?.let(::Address) ?: return error,
                signature = data.signature?.hexStringToByteArray() ?: return error,
                profitWallet = data.profitWallet?.let(::Address) ?: return error,
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
            val repairResult = adapter.evmKitWrapper.sendSingle(
                transactionData = repairTD,
                gasPrice = gasPrice,
                gasLimit = repairGasLimit,
            ).blockingGet().transaction.hash.toHexString()

            Effect.success(repairResult)
        } catch (e: Throwable) {
            handlePossibleRpcError(e)
        }
    }

    private fun Double.applyDecimal(wallet: Wallet) = toBigDecimal()
        .movePointRight(wallet.decimal)
        .toBigInteger()

    override suspend fun getNftMintSummary(nftType: NftType): Effect<NftMintSummary> {
        val value = 0.005
        if ((getBnbWalletModel()?.coinValueDouble ?: 0.0) < value) {
            return Effect.error(AppError.Custom(cause = NoEnoughBnbToMint))
        }

        val wallet = getSnapsWallet() ?: return Effect.error(
            AppError.Unknown(cause = IllegalStateException("Wallet is null"))
        )
        val adapter = CryptoKit.adapterManager.getAdapterForWallet(wallet) as Eip20Adapter?
            ?: return Effect.error(
                AppError.Unknown(cause = IllegalStateException("Adapter is null"))
            )
        val nonceRaw = System.currentTimeMillis()

        return apiCall(ioDispatcher) {
            walletApi.getMintSignature(
                body = NftSignatureRequestDto(
                    nonce = nonceRaw,
                    amount = value,
                ),
            )
        }.flatMap { data ->
            try {
                withContext(ioDispatcher) l@{
                    val error = Effect.error<NftMintSummary>(
                        AppError.Unknown(cause = IllegalStateException("Signature data null! $data"))
                    )

                    val address = Address(SNAPS_NFT)
                    val fromAddress = requireActiveWalletReceiveAddress()

                    val method = MintContractMethod(
                        owner = Address(requireActiveWalletReceiveAddress()),
                        fromAccountAmounts = data.amountReceiver?.let(::BigInteger) ?: return@l error,
                        deadline = data.deadline?.toBigInteger() ?: return@l error,
                        nonce = nonceRaw.toBigInteger(),
                        signature = data.signature?.hexStringToByteArray() ?: return@l error,
                        profitWallet = data.profitWallet?.let(::Address) ?: return@l error,
                    )
                    val encodedAbi: ByteArray = method.encodedABI()

                    val valueApplied = value.applyDecimal(wallet)
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

                    Effect.success(
                        NftMintSummary(
                            from = fromAddress,
                            to = address.hex,
                            summary = value.toBigDecimal(),
                            gas = gasPriceDecimal,
                            total = value.toBigDecimal() + gasPriceDecimal,
                            gasLimit = gasLimit,
                            transactionData = transactionData,
                        )
                    )
                }
            } catch (e: Throwable) {
                handlePossibleRpcError(e)
            }
        }
    }

    override suspend fun mintNft(nftType: NftType, summary: NftMintSummary): Effect<Token> {
        val wallet = getSnapsWallet() ?: return Effect.error(
            AppError.Unknown(cause = IllegalStateException("Wallet is null"))
        )
        val adapter = CryptoKit.adapterManager.getAdapterForWallet(wallet) as Eip20Adapter?
            ?: return Effect.error(
                AppError.Unknown(cause = IllegalStateException("Adapter is null"))
            )
        return withContext(ioDispatcher) {
            try {
                val result = adapter.evmKitWrapper.sendSingle(
                    transactionData = summary.transactionData as TransactionData,
                    gasPrice = gasPrice,
                    gasLimit = summary.gasLimit,
                ).blockingGet().transaction.hash.toHexString()
                Effect.success(result)
            } catch (e: Throwable) {
                handlePossibleRpcError(e)
            }
        }
    }

    private fun <T : Any> handlePossibleRpcError(e: Throwable): Effect<T> =
        (e.cause as? JsonRpc.ResponseError.RpcError)?.error?.let {
            Effect.error(AppError.Unknown(cause = Exception(it.code.toString() + " " + it.message)))
        } ?: (e.cause as? JsonRpc.ResponseError.InvalidResult)?.let {
            Effect.error(AppError.Unknown(cause = Exception(it.toString())))
        } ?: Effect.error(AppError.Unknown(cause = e as? Exception))

    private fun getSnapsWallet() = getWallets().firstOrNull { it.coin.code == "SNAPS" }
}

interface SendHandler {

    val state: StateFlow<State>

    fun send()

    fun send(
        gasPrice: BigInteger,
        gasLimit: BigInteger,
    )

    fun stop()

    sealed class State {
        object Idle : State()
        object Sending : State()
        data class Ready(
            val fee: BigInteger,
            val total: BigInteger,
        ) : State()

        object Sent : State()
        data class Failed(val errors: List<Throwable>) : State()
    }
}

internal class SendHandlerImpl(
    private val service: SendEvmTransactionService,
) : SendHandler {

    private val _state: MutableStateFlow<SendHandler.State> =
        MutableStateFlow(SendHandler.State.Idle)
    override val state = _state.asStateFlow()

    private val disposables = CompositeDisposable()

    init {
        service.stateObservable.subscribeIO { serviceState ->
            log("Transaction service state: $serviceState")
            when (serviceState) {
                is SendEvmTransactionService.State.NotReady -> {
                    _state.update { SendHandler.State.Failed(serviceState.errors) }
                }

                is SendEvmTransactionService.State.Ready -> {
                    _state.update {
                        SendHandler.State.Ready(
                            fee = service.txDataState.transaction?.gasData?.fee ?: BigInteger.ZERO,
                            total = service.txDataState.transaction?.totalAmount ?: BigInteger.ZERO,
                        )
                    }
                }
            }
        }.let(disposables::add)
        service.sendStateObservable.subscribeIO { serviceSendState ->
            log("Transaction service send state: $serviceSendState")
            when (serviceSendState) {
                is SendEvmTransactionService.SendState.Failed -> {
                    _state.update { SendHandler.State.Failed(listOf(serviceSendState.error)) }
                }

                SendEvmTransactionService.SendState.Idle -> {
                    _state.update { SendHandler.State.Idle }
                }

                SendEvmTransactionService.SendState.Sending -> {
                    _state.update { SendHandler.State.Sending }
                }

                is SendEvmTransactionService.SendState.Sent -> {
                    _state.update { SendHandler.State.Sent }
                }
            }
        }.let(disposables::add)
    }

    override fun send() {
        service.send()
    }

    override fun send(gasPrice: BigInteger, gasLimit: BigInteger) {
        service.send(gasPrice = gasPrice, gasLimit = gasLimit)
    }

    override fun stop() {
        service.clear()
        disposables.dispose()
    }
}