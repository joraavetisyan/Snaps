package io.snaps.coredata.database

import androidx.core.content.edit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletStorage @Inject constructor(
    private val provider: PreferencesProvider,
) {

    // todo
    var wallet: List<String>?
        get() = provider.cryptoPrefs.getString("wallet", null)?.split(",")
        set(value) = provider.cryptoPrefs.edit {
            putString("wallet", value?.joinToString(separator = ","))
        }

    fun reset() {
        wallet = null
    }
}