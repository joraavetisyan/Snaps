package io.snaps.corecrypto.core

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
//import com.walletconnect.android.Core
//import com.walletconnect.android.CoreClient
//import com.walletconnect.android.relay.ConnectionType
//import com.walletconnect.sign.client.Sign
//import com.walletconnect.sign.client.SignClient
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
import io.snaps.corecrypto.core.managers.NftAdapterManager
import io.snaps.corecrypto.core.managers.NftMetadataManager
import io.snaps.corecrypto.core.managers.NftMetadataSyncer
import io.snaps.corecrypto.core.managers.NumberFormatter
import io.snaps.corecrypto.core.managers.ReleaseNotesManager
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
import io.snaps.corecrypto.core.storage.NftStorage
import io.snaps.corecrypto.core.storage.RestoreSettingsStorage
import io.snaps.corecrypto.other.BackgroundManager
import io.snaps.corecrypto.other.ICoreApp
import io.snaps.corecrypto.other.IEncryptionManager
import io.snaps.corecrypto.other.IKeyProvider
import io.snaps.corecrypto.other.IKeyStoreManager
import io.snaps.corecrypto.other.ISystemInfoManager
import io.snaps.corecrypto.other.IThirdKeyboard
import io.snaps.corecrypto.walletconnect.other.BalanceViewTypeManager
import io.snaps.corecrypto.walletconnect.storage.WC1SessionStorage
import io.snaps.corecrypto.walletconnect.version1.WC1Manager
import io.snaps.corecrypto.walletconnect.version1.WC1RequestManager
import io.snaps.corecrypto.walletconnect.version1.WC1SessionManager
import io.snaps.corecrypto.walletconnect.version2.WC2Manager
//import io.snaps.corecrypto.walletconnect.version2.WC2Service
//import io.snaps.corecrypto.walletconnect.version2.WC2SessionManager
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
        lateinit var wc1SessionStorage: WC1SessionStorage
        lateinit var wc1SessionManager: WC1SessionManager
        lateinit var wc1RequestManager: WC1RequestManager

        //        lateinit var wc2Service: WC2Service
//        lateinit var wc2SessionManager: WC2SessionManager
        lateinit var wc1Manager: WC1Manager
        lateinit var wc2Manager: WC2Manager
        lateinit var marketKit: MarketKitWrapper
        lateinit var releaseNotesManager: ReleaseNotesManager
        lateinit var restoreSettingsManager: RestoreSettingsManager
        lateinit var evmSyncSourceManager: EvmSyncSourceManager
        lateinit var evmBlockchainManager: EvmBlockchainManager
        lateinit var evmTestnetManager: EvmTestnetManager
        lateinit var solanaRpcSourceManager: SolanaRpcSourceManager
        lateinit var nftMetadataManager: NftMetadataManager
        lateinit var nftAdapterManager: NftAdapterManager
        lateinit var nftMetadataSyncer: NftMetadataSyncer
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
            preferences =
                PreferenceManager.getDefaultSharedPreferences(applicationContext)

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

            feeRateProvider =
                FeeRateProvider(appConfigProvider)
            backgroundManager = BackgroundManager(applicationContext)

            appDatabase = AppDatabase.getInstance(applicationContext)

            blockchainSettingsStorage =
                BlockchainSettingsStorage(
                    appDatabase
                )
            evmSyncSourceStorage =
                EvmSyncSourceStorage(appDatabase)
            evmSyncSourceManager = EvmSyncSourceManager(
                appConfigProvider,
                blockchainSettingsStorage,
                evmSyncSourceStorage
            )

            btcBlockchainManager = BtcBlockchainManager(
                blockchainSettingsStorage,
                marketKit
            )

            binanceKitManager = BinanceKitManager(testMode)

            accountsStorage =
                AccountsStorage(appDatabase)
            restoreSettingsStorage =
                RestoreSettingsStorage(appDatabase)

            accountCleaner = AccountCleaner(testMode)
            accountManager = AccountManager(
                accountsStorage,
                accountCleaner
            )

            LocalStorageManager(preferences).apply {
                localStorage = this
                thirdKeyboardStorage = this
                marketStorage = this
            }

            evmTestnetManager =
                EvmTestnetManager(localStorage)
            enabledWalletsStorage =
                EnabledWalletsStorage(appDatabase)
            walletStorage = WalletStorage(
                marketKit,
                enabledWalletsStorage,
                evmTestnetManager
            )

            walletManager = WalletManager(
                accountManager,
                walletStorage,
                evmTestnetManager
            )
            coinManager = CoinManager(
                marketKit,
                walletManager
            )

            solanaRpcSourceManager = SolanaRpcSourceManager(
                blockchainSettingsStorage,
                marketKit
            )
            val solanaWalletManager = SolanaWalletManager(
                walletManager,
                accountManager,
                marketKit
            )
            solanaKitManager = SolanaKitManager(
                solanaRpcSourceManager,
                solanaWalletManager,
                backgroundManager
            )

            blockchainSettingsStorage =
                BlockchainSettingsStorage(
                    appDatabase
                )

            wordsManager = WordsManager(Mnemonic())
            networkManager = NetworkManager()
            accountFactory =
                AccountFactory(accountManager)
            backupManager =
                BackupManager(accountManager)


            KeyStoreManager(
                keyAlias = "MASTER_KEY",
                keyStoreCleaner = KeyStoreCleaner(
                    localStorage,
                    accountManager,
                    walletManager
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

            wc1SessionStorage =
                WC1SessionStorage(appDatabase)
            wc1SessionManager = WC1SessionManager(
                wc1SessionStorage,
                accountManager,
                evmSyncSourceManager
            )
            wc1RequestManager = WC1RequestManager()
            wc1Manager = WC1Manager(
                accountManager,
                evmBlockchainManager
            )
            wc2Manager = WC2Manager(
                accountManager,
                evmBlockchainManager
            )

            releaseNotesManager = ReleaseNotesManager(
                systemInfoManager,
                localStorage,
                appConfigProvider
            )

            val nftStorage = NftStorage(
                appDatabase.nftDao(),
                marketKit
            )
            nftMetadataManager = NftMetadataManager(
                marketKit,
                appConfigProvider, nftStorage
            )
            nftAdapterManager = NftAdapterManager(
                walletManager,
                evmBlockchainManager
            )
            nftMetadataSyncer = NftMetadataSyncer(
                nftAdapterManager,
                nftMetadataManager, nftStorage
            )

            initializeWalletConnectV2(appConfig, applicationContext)

//            wc2Service = WC2Service()
//            wc2SessionManager = WC2SessionManager(
//                accountManager, WC2SessionStorage(
//                    appDatabase
//                ),
//                wc2Service,
//                wc2Manager
//            )

            baseTokenManager = BaseTokenManager(
                coinManager,
                localStorage
            )
            balanceViewTypeManager =
                BalanceViewTypeManager(localStorage)
            balanceHiddenManager =
                BalanceHiddenManager(localStorage)

            startTasks()
        }

        private fun initializeWalletConnectV2(
            appConfig: AppConfigProvider,
            application: Application
        ) {
//            val projectId = appConfig.walletConnectProjectId
//            val serverUrl = "wss://${appConfig.walletConnectUrl}?projectId=$projectId"
//            val connectionType = ConnectionType.AUTOMATIC
//            val appMetaData = Core.Model.AppMetaData(
//                name = "Unstoppable",
//                description = "",
//                url = "unstoppable.money",
//                icons = listOf("https://raw.githubusercontent.com/horizontalsystems/HS-Design/master/PressKit/UW-AppIcon-on-light.png"),
//                redirect = null,
//            )
//
//            CoreClient.initialize(
//                metaData = appMetaData,
//                relayServerUrl = serverUrl,
//                connectionType = connectionType,
//                application = application,
//                onError = { error ->
//                    Log.w("AAA", "error", error.throwable)
//                },
//            )
//
//            val init = Sign.Params.Init(core = CoreClient)
//            SignClient.initialize(init) { error ->
//                Log.w("AAA", "error", error.throwable)
//            }
        }

        private fun startTasks() {
            Thread {
                nftMetadataSyncer.start()
                accountManager.loadAccounts()
                walletManager.loadWallets()
                adapterManager.preloadAdapters()
                accountManager.clearAccounts()

                AppVersionManager(
                    systemInfoManager,
                    localStorage
                ).apply { storeAppVersion() }

                evmLabelManager.sync()

            }.start()
        }

    }

    override fun getWorkManagerConfiguration(): WorkConfiguration {
        return if (BuildConfig.DEBUG) {
            WorkConfiguration.Builder()
                .setMinimumLoggingLevel(Log.DEBUG)
                .build()
        } else {
            WorkConfiguration.Builder()
                .setMinimumLoggingLevel(Log.ERROR)
                .build()
        }
    }
}
