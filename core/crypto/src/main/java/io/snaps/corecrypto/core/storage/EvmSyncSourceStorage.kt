package io.snaps.corecrypto.core.storage

import io.snaps.corecrypto.entities.EvmSyncSourceRecord
import io.horizontalsystems.marketkit.models.BlockchainType

class EvmSyncSourceStorage(appDatabase: AppDatabase) {

    private val dao = appDatabase.evmSyncSourceDao()

    fun evmSyncSources(blockchainType: BlockchainType): List<EvmSyncSourceRecord> {
        return dao.getEvmSyncSources(blockchainType.uid)
    }

    fun save(evmSyncSourceRecord: EvmSyncSourceRecord) {
        dao.insert(evmSyncSourceRecord)
    }

    fun delete(blockchainTypeUid: String, url: String) {
        dao.delete(blockchainTypeUid, url)
    }

}
