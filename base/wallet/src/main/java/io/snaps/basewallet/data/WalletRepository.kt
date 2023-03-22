package io.snaps.basewallet.data

import android.annotation.SuppressLint
import io.horizontalsystems.ethereumkit.core.LegacyGasPriceProvider
import io.horizontalsystems.ethereumkit.core.eip1559.Eip1559GasPriceProvider
import io.horizontalsystems.ethereumkit.models.Address
import io.horizontalsystems.marketkit.models.BlockchainType
import io.horizontalsystems.marketkit.models.TokenQuery
import io.horizontalsystems.marketkit.models.TokenType
import io.reactivex.disposables.CompositeDisposable
import io.snaps.basewallet.data.model.WalletSaveRequestDto
import io.snaps.basewallet.domain.TotalBalanceModel
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.model.AppError
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.WalletAddress
import io.snaps.corecommon.model.WalletModel
import io.snaps.corecrypto.core.AdapterState
import io.snaps.corecrypto.core.CryptoKit
import io.snaps.corecrypto.core.IAccountFactory
import io.snaps.corecrypto.core.IAccountManager
import io.snaps.corecrypto.core.ISendEthereumAdapter
import io.snaps.corecrypto.core.IWalletManager
import io.snaps.corecrypto.core.IWordsManager
import io.snaps.corecrypto.core.managers.WalletActivator
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
import javax.inject.Inject

object InvalidMnemonicsException : Exception()

interface WalletRepository {

    val totalBalanceValue: StateFlow<TotalBalanceModel>

    val activeWallets: StateFlow<List<WalletModel>>

    fun createAccount(): List<String>

    suspend fun importAccount(words: List<String>): Effect<Completable>

    suspend fun saveLastConnectedAccount(): Effect<Completable>

    fun getActiveAccount(): Account?

    fun getActiveWalletsReceiveAddresses(): List<WalletAddress>

    fun getAvailableBalance(wallet: WalletModel): String?

    fun send(amount: String, address: WalletAddress, wallet: WalletModel): Effect<SendHandler>
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
                    symbol = item.coinCode,
                    iconUrl = item.coinIconUrl,
                    coinValue = item.primaryValue.value,
                    fiatValue = item.secondaryValue.value,
                    receiveAddress = CryptoKit.adapterManager.getReceiveAdapterForWallet(
                        item.wallet
                    )?.receiveAddress.orEmpty(),
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

    override fun createAccount(): List<String> {
        val accountType = mnemonicAccountType(12)
        account = accountFactory.account(
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

    override suspend fun importAccount(words: List<String>): Effect<Completable> {
        return try {
            wordsManager.validateChecksumStrict(words)
            val accountType = AccountType.Mnemonic(words, "".normalizeNFKD())
            account = accountFactory.account(
                name = "Wallet",
                type = accountType,
                origin = AccountOrigin.Restored,
                backedUp = true,
            )
            saveLastConnectedAccount()
            Effect.completable
        } catch (checksumException: Exception) {
            Effect.error(AppError.Unknown(cause = InvalidMnemonicsException))
        }
    }

    override suspend fun saveLastConnectedAccount(): Effect<Completable> {
        val account = account ?: return Effect.error(AppError.Unknown("No account was created!"))
        accountManager.save(account)
        activateDefaultWallets(account)
        predefinedBlockchainSettingsProvider.prepareNew(account, BlockchainType.Zcash)
        while (getActiveWalletsReceiveAddresses().firstOrNull() == null) {
            delay(200)
        }
        return apiCall(ioDispatcher) {
            walletApi.save(WalletSaveRequestDto(getActiveWalletsReceiveAddresses().first()))
        }.toCompletable()
    }

    override fun getActiveAccount(): Account? {
        return accountManager.activeAccount ?: run {
            log("No active account")
            null
        }
    }

    private fun getWallets(): List<Wallet> {
        val account = getActiveAccount() ?: return emptyList()
        return walletManager.getWallets(account)
    }

    override fun getActiveWalletsReceiveAddresses(): List<WalletAddress> {
        return getWallets().mapNotNull {
            CryptoKit.adapterManager.getReceiveAdapterForWallet(it)?.receiveAddress
        }
    }

    private fun activateDefaultWallets(account: Account) {
        val tokenQueries = listOf(
            // boxer
            "0x192E9321b6244D204D4301AfA507EB29CA84D9ef",
            // laflix
            "0x3e3bfa35e81e85be5c65b0c759fe1b6ed6525ec0",
            // wrapped bnb
            "0xbb4CdB9CBd36B01bD1cBaEBF2De08d9173bc095c",
            // BUSD
            "0xe9e7cea3dedca5984780bafc599bd69add087d56",
        ).map {
            TokenQuery(BlockchainType.BinanceSmartChain, TokenType.Eip20(it))
        }
        walletActivator.activateWallets(account, tokenQueries)
    }

    override fun getAvailableBalance(wallet: WalletModel): String? {
        return adapter(wallet)?.balanceData?.available?.toString()
    }

    @SuppressLint("CheckResult")
    override fun send(
        amount: String,
        address: WalletAddress,
        wallet: WalletModel,
    ): Effect<SendHandler> {
        lastSendHandler?.stop()
        val adapter = adapter(wallet) ?: return Effect.error(AppError.Unknown())
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
                    address = Address(address),
                    amount = amount.toBigInteger(),
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
            Effect.error(AppError.Unknown())
        }
    }

    private fun adapter(wallet: WalletModel): ISendEthereumAdapter? {
        return getWallets().firstOrNull { it.coin.uid == wallet.coinUid }?.let {
            CryptoKit.adapterManager.getAdapterForWallet(it) as ISendEthereumAdapter
        }
    }
}

interface SendHandler {

    val state: StateFlow<State>

    fun send()

    fun stop()

    sealed class State {
        object Idle : State()
        object Sending : State()
        data class Ready(
            val fee: String,
            val total: String,
        ) : State()

        object Sent : State()
        object Failed : State()
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
        service.stateObservable.subscribeIO {
            log("Transaction service state: $it")
            when (it) {
                is SendEvmTransactionService.State.NotReady -> {
                    _state.update { SendHandler.State.Failed }
                }
                is SendEvmTransactionService.State.Ready -> {
                    _state.update {
                        SendHandler.State.Ready(
                            fee = service.txDataState.transaction?.gasData?.fee?.toString()
                                .orEmpty(),
                            total = service.txDataState.transaction?.totalAmount?.toString()
                                .orEmpty(),
                        )
                    }
                }
            }
        }.let(disposables::add)
        service.sendStateObservable.subscribeIO {
            log("Transaction service send state: $it")
            when (it) {
                is SendEvmTransactionService.SendState.Failed -> {
                    _state.update { SendHandler.State.Failed }
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

    override fun stop() {
        service.clear()
        disposables.dispose()
    }
}