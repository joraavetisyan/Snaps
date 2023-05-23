package io.snaps.basewallet.data

import io.horizontalsystems.ethereumkit.models.Address
import io.horizontalsystems.marketkit.models.Blockchain
import io.horizontalsystems.marketkit.models.BlockchainType
import io.horizontalsystems.marketkit.models.Coin
import io.horizontalsystems.marketkit.models.Token
import io.horizontalsystems.marketkit.models.TokenQuery
import io.horizontalsystems.marketkit.models.TokenType
import io.snaps.basewallet.data.model.ClaimRequestDto
import io.snaps.basewallet.data.model.PayoutOrderRequestDto
import io.snaps.basewallet.data.model.PayoutOrderResponseDto
import io.snaps.basewallet.data.model.RefillGasRequestDto
import io.snaps.basewallet.data.model.WalletSaveRequestDto
import io.snaps.basewallet.domain.SnpsAccountModel
import io.snaps.basewallet.domain.DeviceNotSecuredException
import io.snaps.basewallet.domain.TotalBalanceModel
import io.snaps.basewallet.domain.WalletModel
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.ext.logE
import io.snaps.corecommon.model.AppError
import io.snaps.corecommon.model.CardNumber
import io.snaps.corecommon.model.CoinType
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.CryptoAddress
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.FiatValue
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.State
import io.snaps.corecommon.model.Uuid
import io.snaps.corecrypto.core.AdapterState
import io.snaps.corecrypto.core.CryptoKit
import io.snaps.corecrypto.core.IAccountFactory
import io.snaps.corecrypto.core.IAccountManager
import io.snaps.corecrypto.core.IWalletManager
import io.snaps.corecrypto.core.IWordsManager
import io.snaps.corecrypto.core.managers.WalletActivator
import io.snaps.corecrypto.core.providers.BalanceItem
import io.snaps.corecrypto.core.providers.BalanceService
import io.snaps.corecrypto.core.providers.BalanceViewItemFactory
import io.snaps.corecrypto.core.providers.ITotalBalance
import io.snaps.corecrypto.core.providers.TotalService
import io.snaps.corecrypto.entities.Account
import io.snaps.corecrypto.entities.AccountOrigin
import io.snaps.corecrypto.entities.AccountType
import io.snaps.corecrypto.entities.Currency
import io.snaps.corecrypto.entities.Wallet
import io.snaps.corecrypto.entities.normalizeNFKD
import io.snaps.coredata.coroutine.ApplicationCoroutineScope
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.network.apiCall
import io.snaps.coreui.viewmodel.likeStateFlow
import io.snaps.coreui.viewmodel.tryPublish
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.security.InvalidAlgorithmParameterException
import javax.inject.Inject

private const val messageNotSecured =
    "java.lang.IllegalStateException: Secure lock screen must be enabled to create keys requiring user authentication"

interface WalletRepository {

    val snpsAccountState: StateFlow<State<SnpsAccountModel>>

    val totalBalance: StateFlow<TotalBalanceModel>

    val activeWallets: StateFlow<List<WalletModel>>

    val snps: StateFlow<WalletModel?>

    val bnb: StateFlow<WalletModel?>

    val payouts: StateFlow<State<List<PayoutOrderResponseDto>>>

    suspend fun updateSnpsAccount(): Effect<Completable>

    suspend fun updateTotalBalance(): Effect<Completable>

    suspend fun updatePayouts(isSilently: Boolean = false): Effect<Completable>

    suspend fun updateTotalBalanceAndPayouts(isSilently: Boolean = false): Effect<Completable>

    fun createAccount(userId: Uuid): List<String>

    suspend fun importAccount(userId: Uuid, words: List<String>): Effect<Completable>

    suspend fun saveLastConnectedAccount(): Effect<Completable>

    fun hasAccount(userId: Uuid): Boolean

    fun setAccountActive(userId: Uuid)

    fun setAccountInactive()

    fun deleteAccount(id: Uuid)

    fun getMnemonics(): List<String>

    fun requireActiveWalletReceiveAddress(): CryptoAddress

    fun isAddressValid(value: CryptoAddress): Boolean

    suspend fun claim(amount: Double): Effect<Completable>

    suspend fun claimMax(): Effect<Completable>

    suspend fun confirmPayout(amount: Double, cardNumber: CardNumber): Effect<Completable>

    suspend fun refillGas(amount: Double): Effect<Completable>
}

class WalletRepositoryImpl @Inject constructor(
    @ApplicationCoroutineScope private val scope: CoroutineScope,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,

    private val accountFactory: IAccountFactory,
    private val walletManager: IWalletManager,
    private val wordsManager: IWordsManager,
    private val accountManager: IAccountManager,
    private val walletActivator: WalletActivator,
    private val balanceService: BalanceService,
    private val totalBalanceManager: ITotalBalance,
    private val balanceViewItemFactory: BalanceViewItemFactory,

    private val walletApi: WalletApi,
) : WalletRepository {

    private val _snpsAccountState = MutableStateFlow<State<SnpsAccountModel>>(Loading())
    override val snpsAccountState = _snpsAccountState.asStateFlow()

    private val _totalBalance = MutableStateFlow(TotalBalanceModel.empty)
    override val totalBalance = _totalBalance.asStateFlow()

    override val activeWallets: StateFlow<List<WalletModel>> = balanceService.balanceItemsFlow
        .onEach(::setTotalServiceItems)
        .mapNotNull { items -> items?.takeIf { it.isNotEmpty() } }
        .combine(snpsAccountState, ::walletModels)
        .likeStateFlow(scope, emptyList())

    override val bnb: StateFlow<WalletModel?> = activeWallets.mapNotNull { wallets ->
        wallets.find { it.coinType == CoinType.BNB }
    }.likeStateFlow(scope, null)

    override val snps: StateFlow<WalletModel?> = activeWallets.mapNotNull { wallets ->
        wallets.find { it.coinType == CoinType.SNPS }
    }.likeStateFlow(scope, null)

    private val _payouts = MutableStateFlow<State<List<PayoutOrderResponseDto>>>(Loading())
    override val payouts = _payouts.asStateFlow()

    private var account: Account? = null

    init {
        combine(totalBalanceManager.totalUiState, snps) { totalUIState, snps ->
            totalBalanceModel(
                totalUIState = totalUIState,
                snps = snps,
                balance = snpsAccountState.value,
            )
        }.onEach { balanceModel ->
            _totalBalance.update { balanceModel }
        }.launchIn(scope)
        balanceService.start()
        totalBalanceManager.start(scope)
    }

    private fun setTotalServiceItems(balanceItems: List<BalanceItem>?) {
        totalBalanceManager.setTotalServiceItems(
            map = balanceItems?.map {
                TotalService.BalanceItem(
                    value = it.balanceData.total,
                    isValuePending = it.state !is AdapterState.Synced,
                    coinPrice = it.coinPrice,
                )
            }
        )
    }

    private fun walletModels(
        balanceItems: List<BalanceItem>,
        snpsAccount: State<SnpsAccountModel>,
    ) = balanceItems.mapNotNull {
        balanceViewItemFactory.viewItem(
            item = it,
            currency = Currency(FiatValue.default.name, FiatValue.default.symbol, FiatValue.decimals, 0),
        ).toWalletModel(snpsAccount)
    }

    override suspend fun updateSnpsAccount(): Effect<Completable> {
        _snpsAccountState tryPublish Loading()
        return apiCall(ioDispatcher) {
            walletApi.getSnpsAccount()
        }.map {
            it.toModel()
        }.also {
            _snpsAccountState tryPublish it
        }.toCompletable()
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
            log("Saving account: $account")
            accountManager.save(account)
        } catch (e: InvalidAlgorithmParameterException) {
            if (e.message == messageNotSecured) {
                return Effect.error(AppError.Custom(cause = DeviceNotSecuredException))
            }
            return Effect.error(AppError.Unknown(cause = e))
        }
        activateDefaultTokens(account)
        // fixme better way
        while (getActiveWalletReceiveAddress() == null) {
            delay(200)
        }
        return apiCall(ioDispatcher) {
            walletApi.save(WalletSaveRequestDto(address = requireActiveWalletReceiveAddress()))
        }.toCompletable()
    }

    private fun activateDefaultTokens(account: Account) {
        val tokens = listOf(
            CoinType.SNPS.let {
                Token(
                    coin = Coin(uid = "snapsCoinUid", name = it.coinName, code = it.code),
                    // todo release mainnet
                    blockchain = Blockchain(
                        type = BlockchainType.BinanceSmartChain,
                        name = "Testnet",
                        explorerUrl = "https://testnet.bscscan.com",
                    ),
                    type = TokenType.Eip20(it.address),
                    decimals = it.decimal,
                )
            }
        )
        // todo construct tokens yourself
        val tokenQueries = CoinType.values().filterNot {
            it == CoinType.SNPS // constructed previously
                || it == CoinType.BNB // added as TokenType.Native
        }.map {
            TokenQuery(BlockchainType.BinanceSmartChain, TokenType.Eip20(it.address))
        } + listOf(
            TokenQuery(BlockchainType.BinanceSmartChain, TokenType.Native)
        )
        log("Activating tokens: ${tokens.joinToString()}")
        log("Activating tokenQueries: ${tokenQueries.joinToString()}")
        walletActivator.activateWallets(
            account = account,
            tokenQueries = tokenQueries,
            tokens = tokens,
        )
    }

    private fun getActiveWalletReceiveAddress(): CryptoAddress? {
        return getWallets().firstNotNullOfOrNull { wallet ->
            CryptoKit.adapterManager.getReceiveAdapterForWallet(wallet)?.receiveAddress.also {
                if (it == null) logE("No receive adapter for wallet $wallet!")
            }
        }
    }

    override fun requireActiveWalletReceiveAddress(): CryptoAddress {
        return requireNotNull(getActiveWalletReceiveAddress())
    }

    private fun getWallets(): List<Wallet> {
        val account = getActiveAccount() ?: return emptyList()
        return walletManager.getWallets(account).also {
            if (it.isEmpty()) logE("No wallets!")
        }
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
        return getActiveAccount()?.let { (it.type as AccountType.Mnemonic).words } ?: emptyList()
    }

    private fun getActiveAccount(): Account? {
        return accountManager.activeAccount.also {
            if (it == null) logE("No active account!")
        }
    }

    override fun deleteAccount(id: Uuid) {
        accountManager.delete(id)
    }

    override fun isAddressValid(value: CryptoAddress): Boolean {
        return kotlin.runCatching { Address(value) }.getOrNull() != null
    }

    override suspend fun updateTotalBalance(): Effect<Completable> {
        balanceService.refresh()
        delay(500) // usually it's very fast
        return Effect.completable
    }

    override suspend fun claim(amount: Double): Effect<Completable> {
        return apiCall(ioDispatcher) {
            walletApi.claim(ClaimRequestDto(amount))
        }.toCompletable()
    }

    override suspend fun claimMax(): Effect<Completable> {
        return apiCall(ioDispatcher) {
            walletApi.claimMax()
        }.toCompletable()
    }

    override suspend fun confirmPayout(amount: Double, cardNumber: CardNumber): Effect<Completable> {
        return apiCall(ioDispatcher) {
            walletApi.payoutOrder(PayoutOrderRequestDto(cardNumber = cardNumber, amount = amount))
        }
    }

    override suspend fun updatePayouts(isSilently: Boolean): Effect<Completable> {
        if (!isSilently) {
            _payouts tryPublish Loading()
        }
        return apiCall(ioDispatcher) {
            walletApi.payoutStatus()
        }.also {
            _payouts tryPublish it
        }.toCompletable()
    }

    override suspend fun refillGas(amount: Double): Effect<Completable> {
        return apiCall(ioDispatcher) {
            walletApi.refillGas(RefillGasRequestDto(amount = amount))
        }
    }

    override suspend fun updateTotalBalanceAndPayouts(isSilently: Boolean): Effect<Completable> {
        val updateTotalBalanceAsync = scope.async { updateTotalBalance() }
        val updatePayoutsAsync = scope.async { updatePayouts(isSilently = isSilently) }
        val updateTotalBalance = updateTotalBalanceAsync.await()
        val updatePayouts = updatePayoutsAsync.await()
        return if (updateTotalBalance.isSuccess && updatePayouts.isSuccess) {
            Effect.completable
        } else {
            Effect.error((updateTotalBalance.errorOrNull ?: updatePayouts.errorOrNull)!!)
        }
    }
}