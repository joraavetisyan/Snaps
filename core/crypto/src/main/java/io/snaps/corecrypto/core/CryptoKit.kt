package io.snaps.corecrypto.core

//import com.walletconnect.android.Core
//import com.walletconnect.android.CoreClient
//import com.walletconnect.android.relay.ConnectionType
//import com.walletconnect.sign.client.Sign
//import com.walletconnect.sign.client.SignClient
//import io.snaps.corecrypto.walletconnect.version2.WC2Service
//import io.snaps.corecrypto.walletconnect.version2.WC2SessionManager
import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import io.horizontalsystems.ethereumkit.core.EthereumKit
import io.horizontalsystems.hdwalletkit.Mnemonic
import io.reactivex.plugins.RxJavaPlugins
import io.snaps.corecommon.BuildConfig
import io.snaps.corecrypto.core.factories.AccountFactory
import io.snaps.corecrypto.core.factories.AdapterFactory
import io.snaps.corecrypto.core.factories.AddressParserFactory
import io.snaps.corecrypto.core.factories.EvmAccountManagerFactory
import io.snaps.corecrypto.core.managers.AccountCleaner
import io.snaps.corecrypto.core.managers.AccountManager
import io.snaps.corecrypto.core.managers.AdapterManager
import io.snaps.corecrypto.core.managers.AppVersionManager
import io.snaps.corecrypto.core.managers.BackgroundStateChangeListener
import io.snaps.corecrypto.core.managers.BackupManager
import io.snaps.corecrypto.core.managers.BalanceHiddenManager
import io.snaps.corecrypto.core.managers.BaseTokenManager
import io.snaps.corecrypto.core.managers.BinanceKitManager
import io.snaps.corecrypto.core.managers.BtcBlockchainManager
import io.snaps.corecrypto.core.managers.CoinManager
import io.snaps.corecrypto.core.managers.ConnectivityManager
import io.snaps.corecrypto.core.managers.CurrencyManager
import io.snaps.corecrypto.core.managers.EvmBlockchainManager
import io.snaps.corecrypto.core.managers.EvmLabelManager
import io.snaps.corecrypto.core.managers.EvmSyncSourceManager
import io.snaps.corecrypto.core.managers.EvmTestnetManager
import io.snaps.corecrypto.core.managers.KeyStoreCleaner
import io.snaps.corecrypto.core.managers.LanguageManager
import io.snaps.corecrypto.core.managers.LocalStorageManager
import io.snaps.corecrypto.core.managers.MarketKitWrapper
import io.snaps.corecrypto.core.managers.NetworkManager
import io.snaps.corecrypto.core.managers.NumberFormatter
import io.snaps.corecrypto.core.managers.RestoreSettingsManager
import io.snaps.corecrypto.core.managers.SolanaKitManager
import io.snaps.corecrypto.core.managers.SolanaRpcSourceManager
import io.snaps.corecrypto.core.managers.SolanaWalletManager
import io.snaps.corecrypto.core.managers.SystemInfoManager
import io.snaps.corecrypto.core.managers.TransactionAdapterManager
import io.snaps.corecrypto.core.managers.WalletActivator
import io.snaps.corecrypto.core.managers.WalletManager
import io.snaps.corecrypto.core.managers.WalletStorage
import io.snaps.corecrypto.core.managers.WordsManager
import io.snaps.corecrypto.core.managers.ZcashBirthdayProvider
import io.snaps.corecrypto.core.providers.AppConfigProvider
import io.snaps.corecrypto.core.providers.EvmLabelProvider
import io.snaps.corecrypto.core.providers.FeeRateProvider
import io.snaps.corecrypto.core.providers.FeeTokenProvider
import io.snaps.corecrypto.core.security.EncryptionManager
import io.snaps.corecrypto.core.security.KeyStoreManager
import io.snaps.corecrypto.core.storage.AccountsStorage
import io.snaps.corecrypto.core.storage.AppDatabase
import io.snaps.corecrypto.core.storage.BlockchainSettingsStorage
import io.snaps.corecrypto.core.storage.EnabledWalletsStorage
import io.snaps.corecrypto.core.storage.EvmSyncSourceStorage
import io.snaps.corecrypto.core.storage.RestoreSettingsStorage
import io.snaps.corecrypto.other.BackgroundManager
import io.snaps.corecrypto.other.ICoreApp
import io.snaps.corecrypto.other.IEncryptionManager
import io.snaps.corecrypto.other.IKeyProvider
import io.snaps.corecrypto.other.IKeyStoreManager
import io.snaps.corecrypto.other.ISystemInfoManager
import io.snaps.corecrypto.other.IThirdKeyboard
import io.snaps.corecrypto.walletconnect.other.BalanceViewTypeManager
import java.util.logging.Level
import java.util.logging.Logger
import androidx.work.Configuration as WorkConfiguration

abstract class CoreApp {

    companion object : ICoreApp {
        override lateinit var backgroundManager: BackgroundManager
        override lateinit var encryptionManager: IEncryptionManager
        override lateinit var systemInfoManager: ISystemInfoManager
        override lateinit var keyStoreManager: IKeyStoreManager
        override lateinit var keyProvider: IKeyProvider
        override lateinit var thirdKeyboardStorage: IThirdKeyboard

        override lateinit var instance: Application

        override val testMode = false
    }
}

class CryptoKit : CoreApp(), WorkConfiguration.Provider {

    companion object : ICoreApp by CoreApp {

        lateinit var preferences: SharedPreferences
        lateinit var feeRateProvider: FeeRateProvider
        lateinit var localStorage: ILocalStorage
        lateinit var marketStorage: IMarketStorage
        lateinit var restoreSettingsStorage: IRestoreSettingsStorage
        lateinit var currencyManager: CurrencyManager
        lateinit var languageManager: LanguageManager
        lateinit var blockchainSettingsStorage: BlockchainSettingsStorage
        lateinit var evmSyncSourceStorage: EvmSyncSourceStorage
        lateinit var btcBlockchainManager: BtcBlockchainManager
        lateinit var wordsManager: WordsManager
        lateinit var networkManager: INetworkManager
        lateinit var backgroundStateChangeListener: BackgroundStateChangeListener
        lateinit var appConfigProvider: AppConfigProvider
        lateinit var adapterManager: IAdapterManager
        lateinit var transactionAdapterManager: TransactionAdapterManager
        lateinit var walletManager: IWalletManager
        lateinit var walletActivator: WalletActivator
        lateinit var walletStorage: IWalletStorage
        lateinit var accountManager: IAccountManager
        lateinit var accountFactory: IAccountFactory
        lateinit var backupManager: IBackupManager
        lateinit var zcashBirthdayProvider: ZcashBirthdayProvider
        lateinit var connectivityManager: ConnectivityManager
        lateinit var appDatabase: AppDatabase
        lateinit var accountsStorage: IAccountsStorage
        lateinit var enabledWalletsStorage: IEnabledWalletStorage
        lateinit var binanceKitManager: BinanceKitManager
        lateinit var solanaKitManager: SolanaKitManager
        lateinit var numberFormatter: IAppNumberFormatter
        lateinit var addressParserFactory: AddressParserFactory
        lateinit var feeCoinProvider: FeeTokenProvider
        lateinit var accountCleaner: IAccountCleaner
        lateinit var coinManager: ICoinManager
        lateinit var marketKit: MarketKitWrapper
        lateinit var restoreSettingsManager: RestoreSettingsManager
        lateinit var evmSyncSourceManager: EvmSyncSourceManager
        lateinit var evmBlockchainManager: EvmBlockchainManager
        lateinit var evmTestnetManager: EvmTestnetManager
        lateinit var solanaRpcSourceManager: SolanaRpcSourceManager
        lateinit var evmLabelManager: EvmLabelManager
        lateinit var baseTokenManager: BaseTokenManager
        lateinit var balanceViewTypeManager: BalanceViewTypeManager
        lateinit var balanceHiddenManager: BalanceHiddenManager

        fun init(applicationContext: Application) {
            if (!BuildConfig.DEBUG) {
                //Disable logging for lower levels in Release build
                Logger.getLogger("").level = Level.SEVERE
            }

            RxJavaPlugins.setErrorHandler { e: Throwable? ->
                Log.w("RxJava ErrorHandler", e)
            }

            EthereumKit.init()

            instance = applicationContext
            preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)

            val appConfig = AppConfigProvider()
            appConfigProvider = appConfig

            marketKit = MarketKitWrapper(
                context = applicationContext,
                hsApiBaseUrl = appConfig.marketApiBaseUrl,
                hsApiKey = appConfig.marketApiKey,
                cryptoCompareApiKey = appConfig.cryptoCompareApiKey,
                defiYieldApiKey = appConfig.defiyieldProviderApiKey
            )
            marketKit.sync()

            feeRateProvider = FeeRateProvider(appConfigProvider)
            backgroundManager = BackgroundManager(applicationContext)

            appDatabase = AppDatabase.getInstance(applicationContext)

            blockchainSettingsStorage = BlockchainSettingsStorage(appDatabase)
            evmSyncSourceStorage = EvmSyncSourceStorage(appDatabase)
            evmSyncSourceManager = EvmSyncSourceManager(
                appConfigProvider = appConfigProvider,
                blockchainSettingsStorage = blockchainSettingsStorage,
                evmSyncSourceStorage = evmSyncSourceStorage,
            )

            btcBlockchainManager = BtcBlockchainManager(
                storage = blockchainSettingsStorage,
                marketKit = marketKit,
            )

            binanceKitManager = BinanceKitManager(testMode)

            accountsStorage = AccountsStorage(appDatabase)
            restoreSettingsStorage = RestoreSettingsStorage(appDatabase)

            accountCleaner = AccountCleaner(testMode)
            accountManager = AccountManager(
                storage = accountsStorage,
                accountCleaner = accountCleaner,
            )

            LocalStorageManager(preferences).apply {
                localStorage = this
                thirdKeyboardStorage = this
                marketStorage = this
            }

            evmTestnetManager = EvmTestnetManager(localStorage)
            enabledWalletsStorage = EnabledWalletsStorage(appDatabase)
            walletStorage = WalletStorage(
                marketKit = marketKit,
                storage = enabledWalletsStorage,
                evmTestnetManager = evmTestnetManager,
            )

            walletManager = WalletManager(
                accountManager = accountManager,
                storage = walletStorage,
                testnetManager = evmTestnetManager,
            )
            coinManager = CoinManager(
                marketKit = marketKit,
                walletManager = walletManager
            )

            solanaRpcSourceManager = SolanaRpcSourceManager(
                blockchainSettingsStorage = blockchainSettingsStorage,
                marketKitWrapper = marketKit,
            )
            val solanaWalletManager = SolanaWalletManager(
                walletManager = walletManager,
                accountManager = accountManager,
                marketKit = marketKit,
            )
            solanaKitManager = SolanaKitManager(
                rpcSourceManager = solanaRpcSourceManager,
                walletManager = solanaWalletManager,
                backgroundManager = backgroundManager,
            )

            blockchainSettingsStorage = BlockchainSettingsStorage(appDatabase)

            wordsManager = WordsManager(Mnemonic())
            networkManager = NetworkManager()
            accountFactory = AccountFactory(accountManager)
            backupManager = BackupManager(accountManager)

            KeyStoreManager(
                keyAlias = "MASTER_KEY",
                keyStoreCleaner = KeyStoreCleaner(
                    localStorage = localStorage,
                    accountManager = accountManager,
                    walletManager = walletManager,
                ),
            ).apply {
                keyStoreManager = this
                keyProvider = this
            }

            encryptionManager = EncryptionManager(keyProvider)

            walletActivator = WalletActivator(
                walletManager = walletManager,
                marketKit = marketKit
            )

            val evmAccountManagerFactory = EvmAccountManagerFactory(
                accountManager = accountManager,
                walletManager = walletManager,
                marketKit = marketKit,
                evmAccountStateDao = appDatabase.evmAccountStateDao()
            )
            evmBlockchainManager = EvmBlockchainManager(
                backgroundManager = backgroundManager,
                syncSourceManager = evmSyncSourceManager,
                marketKit = marketKit,
                accountManagerFactory = evmAccountManagerFactory,
                evmTestnetManager = evmTestnetManager
            )

            systemInfoManager = SystemInfoManager()

            languageManager = LanguageManager()
            currencyManager = CurrencyManager(
                localStorage = localStorage,
                appConfigProvider = appConfigProvider
            )
            numberFormatter = NumberFormatter(languageManager)

            connectivityManager = ConnectivityManager(backgroundManager)

            zcashBirthdayProvider = ZcashBirthdayProvider(applicationContext, testMode)
            restoreSettingsManager = RestoreSettingsManager(
                storage = restoreSettingsStorage,
                zcashBirthdayProvider = zcashBirthdayProvider
            )

            evmLabelManager = EvmLabelManager(
                provider = EvmLabelProvider(),
                addressLabelDao = appDatabase.evmAddressLabelDao(),
                methodLabelDao = appDatabase.evmMethodLabelDao(),
                syncerStateStorage = appDatabase.syncerStateDao()
            )

            val adapterFactory = AdapterFactory(
                context = applicationContext,
                testMode = testMode,
                btcBlockchainManager = btcBlockchainManager,
                evmBlockchainManager = evmBlockchainManager,
                evmSyncSourceManager = evmSyncSourceManager,
                binanceKitManager = binanceKitManager,
                solanaKitManager = solanaKitManager,
                backgroundManager = backgroundManager,
                restoreSettingsManager = restoreSettingsManager,
                coinManager = coinManager,
                evmLabelManager = evmLabelManager
            )
            adapterManager = AdapterManager(
                walletManager = walletManager,
                adapterFactory = adapterFactory,
                btcBlockchainManager = btcBlockchainManager,
                evmBlockchainManager = evmBlockchainManager,
                binanceKitManager = binanceKitManager,
                solanaKitManager = solanaKitManager
            )
            transactionAdapterManager = TransactionAdapterManager(
                adapterManager = adapterManager,
                adapterFactory = adapterFactory,
            )

            feeCoinProvider = FeeTokenProvider(marketKit)

            addressParserFactory = AddressParserFactory()

            backgroundStateChangeListener =
                BackgroundStateChangeListener(systemInfoManager, keyStoreManager).apply {
                    backgroundManager.registerListener(this)
                }

            baseTokenManager = BaseTokenManager(
                coinManager = coinManager,
                localStorage = localStorage,
            )
            balanceViewTypeManager = BalanceViewTypeManager(localStorage)
            balanceHiddenManager = BalanceHiddenManager(localStorage)

            startTasks()
        }

        private fun startTasks() {
            Thread {
                accountManager.loadAccounts()
                walletManager.loadWallets()
                adapterManager.preloadAdapters()
                // clear deleted accounts
                accountManager.clearAccounts()

                AppVersionManager(
                    systemInfoManager = systemInfoManager,
                    localStorage = localStorage,
                ).apply { storeAppVersion() }

                evmLabelManager.sync()

            }.start()
        }
    }

    override fun getWorkManagerConfiguration(): WorkConfiguration {
        return if (BuildConfig.DEBUG) {
            WorkConfiguration.Builder()
                .setMinimumLoggingLevel(Log.ERROR)
                .build()
        } else {
            WorkConfiguration.Builder()
                .setMinimumLoggingLevel(Log.ERROR)
                .build()
        }
    }
}
