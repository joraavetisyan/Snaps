package io.snaps.corecrypto.core.providers

import io.snaps.corecrypto.core.managers.RestoreSettings
import io.snaps.corecrypto.core.managers.RestoreSettingsManager
import io.snaps.corecrypto.core.managers.ZcashBirthdayProvider
import io.snaps.corecrypto.entities.Account
import io.horizontalsystems.marketkit.models.BlockchainType

class PredefinedBlockchainSettingsProvider(
    private val manager: RestoreSettingsManager,
    private val zcashBirthdayProvider: ZcashBirthdayProvider
) {

    fun prepareNew(account: Account, blockchainType: BlockchainType) {
        val settings = RestoreSettings()
        when (blockchainType) {
            BlockchainType.Zcash -> {
                settings.birthdayHeight = zcashBirthdayProvider.getLatestCheckpointBlockHeight()
            }
            else -> {}
        }
        if (settings.isNotEmpty()) {
            manager.save(settings, account, blockchainType)
        }
    }
}
