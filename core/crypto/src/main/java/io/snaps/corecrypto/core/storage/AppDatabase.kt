package io.snaps.corecrypto.core.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.snaps.corecrypto.core.storage.migrations.*
import io.snaps.corecrypto.entities.*
import io.snaps.corecrypto.entities.nft.NftAssetBriefMetadataRecord
import io.snaps.corecrypto.entities.nft.NftAssetRecord
import io.snaps.corecrypto.entities.nft.NftCollectionRecord
import io.snaps.corecrypto.entities.nft.NftMetadataSyncRecord
import io.snaps.corecrypto.walletconnect.entity.WalletConnectSession
import io.snaps.corecrypto.walletconnect.entity.WalletConnectV2Session
import io.snaps.corecrypto.walletconnect.storage.WC1SessionDao
import io.snaps.corecrypto.walletconnect.storage.WC2SessionDao

@Database(
    version = 1, exportSchema = false, entities = [
        EnabledWallet::class,
        EnabledWalletCache::class,
        AccountRecord::class,
        BlockchainSettingRecord::class,
        EvmSyncSourceRecord::class,
        LogEntry::class,
        FavoriteCoin::class,
        WalletConnectSession::class,
        WalletConnectV2Session::class,
        RestoreSettingRecord::class,
        ActiveAccount::class,
        EvmAccountState::class,
        NftCollectionRecord::class,
        NftAssetRecord::class,
        NftMetadataSyncRecord::class,
        NftAssetBriefMetadataRecord::class,
        EvmAddressLabel::class,
        EvmMethodLabel::class,
        SyncerState::class
    ]
)

@TypeConverters(DatabaseConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun walletsDao(): EnabledWalletsDao
    abstract fun enabledWalletsCacheDao(): EnabledWalletsCacheDao
    abstract fun accountsDao(): AccountsDao
    abstract fun blockchainSettingDao(): BlockchainSettingDao
    abstract fun evmSyncSourceDao(): EvmSyncSourceDao
    abstract fun restoreSettingDao(): RestoreSettingDao
    abstract fun logsDao(): LogsDao
    abstract fun marketFavoritesDao(): MarketFavoritesDao
    abstract fun wc1SessionDao(): WC1SessionDao
    abstract fun wc2SessionDao(): WC2SessionDao
    abstract fun evmAccountStateDao(): EvmAccountStateDao
    abstract fun nftDao(): NftDao
    abstract fun evmAddressLabelDao(): EvmAddressLabelDao
    abstract fun evmMethodLabelDao(): EvmMethodLabelDao
    abstract fun syncerStateDao(): SyncerStateDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "dbSnapsWallet")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()
        }
    }
}
