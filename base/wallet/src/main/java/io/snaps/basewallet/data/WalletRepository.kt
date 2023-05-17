package io.snaps.basewallet.data

import io.horizontalsystems.ethereumkit.models.Address
import io.horizontalsystems.marketkit.models.BlockchainType
import io.horizontalsystems.marketkit.models.TokenQuery
import io.horizontalsystems.marketkit.models.TokenType
import io.snaps.basewallet.data.model.ClaimRequestDto
import io.snaps.basewallet.data.model.PayoutOrderRequestDto
import io.snaps.basewallet.data.model.PayoutOrderResponseDto
import io.snaps.basewallet.data.model.PayoutOrderStatus
import io.snaps.basewallet.data.model.WalletSaveRequestDto
import io.snaps.basewallet.domain.DeviceNotSecuredException
import io.snaps.basewallet.domain.TotalBalanceModel
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.model.AppError
import io.snaps.corecommon.model.CardNumber
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.State
import io.snaps.corecommon.model.Uuid
import io.snaps.corecommon.model.WalletAddress
import io.snaps.corecommon.model.WalletModel
import io.snaps.corecrypto.core.AdapterState
import io.snaps.corecrypto.core.CryptoKit
import io.snaps.corecrypto.core.IAccountFactory
import io.snaps.corecrypto.core.IAccountManager
import io.snaps.corecrypto.core.IWalletManager
import io.snaps.corecrypto.core.IWordsManager
import io.snaps.corecrypto.core.adapters.BaseEvmAdapter
import io.snaps.corecrypto.core.managers.WalletActivator
import io.snaps.corecrypto.core.managers.defaultTokens
import io.snaps.corecrypto.core.providers.BalanceService
import io.snaps.corecrypto.core.providers.BalanceViewItemFactory
import io.snaps.corecrypto.core.providers.ITotalBalance
import io.snaps.corecrypto.core.providers.PredefinedBlockchainSettingsProvider
import io.snaps.corecrypto.core.providers.TotalService
import io.snaps.corecrypto.core.providers.TotalUIState
import io.snaps.corecrypto.entities.Account
import io.snaps.corecrypto.entities.AccountOrigin
import io.snaps.corecrypto.entities.AccountType
import io.snaps.corecrypto.entities.Wallet
import io.snaps.corecrypto.entities.normalizeNFKD
import io.snaps.coredata.coroutine.ApplicationCoroutineScope
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.network.BaseResponse
import io.snaps.coredata.network.apiCall
import io.snaps.coreui.viewmodel.likeStateFlow
import io.snaps.coreui.viewmodel.tryPublish
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
import java.security.InvalidAlgorithmParameterException
import javax.inject.Inject

private const val messageNotSecured =
    "java.lang.IllegalStateException: Secure lock screen must be enabled to create keys requiring user authentication"

interface WalletRepository {

    val totalBalanceValue: StateFlow<TotalBalanceModel>

    val activeWallets: StateFlow<List<WalletModel>>

    val payouts: StateFlow<State<List<PayoutOrderResponseDto>>>

    fun createAccount(userId: Uuid): List<String>

    suspend fun importAccount(userId: Uuid, words: List<String>): Effect<Completable>

    suspend fun saveLastConnectedAccount(): Effect<Completable>

    fun hasAccount(userId: Uuid): Boolean

    fun setAccountActive(userId: Uuid)

    fun setAccountInactive()

    fun deleteAccount(id: Uuid)

    fun getMnemonics(): List<String>

    fun getWallets(): List<Wallet>

    fun requireActiveWalletReceiveAddress(): WalletAddress

    fun isAddressValid(value: WalletAddress): Boolean

    // region Balances todo flow
    fun getBnbWalletModel(): WalletModel?

    fun getSnpWalletModel(): WalletModel?
    // endregion

    suspend fun updateBalance(): Effect<Completable>

    suspend fun claim(amount: Double): Effect<Completable>

    suspend fun confirmPayout(amount: Double, cardNumber: CardNumber): Effect<Completable>

    suspend fun updatePayouts(isSilently: Boolean = false): Effect<Completable>
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

    private val _payouts = MutableStateFlow<State<List<PayoutOrderResponseDto>>>(Loading())
    override val payouts = _payouts.asStateFlow()

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

    private fun getActiveWalletReceiveAddress(): WalletAddress? {
        return getWallets().firstNotNullOfOrNull {
            CryptoKit.adapterManager.getReceiveAdapterForWallet(it)?.receiveAddress
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

    override fun getWallets(): List<Wallet> {
        val account = getActiveAccount() ?: return emptyList()
        return walletManager.getWallets(account)
    }

    override fun getBnbWalletModel(): WalletModel? {
        return activeWallets.value.firstOrNull { it.coinUid == "binancecoin" }
    }

    override fun getSnpWalletModel(): WalletModel? {
        return activeWallets.value.firstOrNull { it.symbol == "SNAPS" }
    }

    override fun deleteAccount(id: Uuid) {
        accountManager.delete(id)
    }

    override fun requireActiveWalletReceiveAddress(): WalletAddress {
        return requireNotNull(getActiveWalletReceiveAddress()) { "No active wallet receive address" }
    }

    override fun isAddressValid(value: WalletAddress): Boolean {
        return kotlin.runCatching { Address(value) }.getOrNull() != null
    }

    private fun activateDefaultWallets(account: Account) {
        val tokenQueries = defaultTokens.map {
            TokenQuery(BlockchainType.BinanceSmartChain, TokenType.Eip20(it))
        } + listOf(
            TokenQuery(BlockchainType.BinanceSmartChain, TokenType.Native)
        )
        walletActivator.activateWallets(account, tokenQueries)
    }

    override suspend fun updateBalance(): Effect<Completable> {
        balanceService.refresh()
        delay(500) // usually it's very fast
        return Effect.completable
    }

    override suspend fun claim(amount: Double): Effect<Completable> {
        return apiCall(ioDispatcher) {
            walletApi.claim(ClaimRequestDto(amount))
        }
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
}