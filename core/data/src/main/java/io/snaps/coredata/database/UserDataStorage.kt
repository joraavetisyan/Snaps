package io.snaps.coredata.database

import androidx.core.content.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val PREF_NAME = stringPreferencesKey("PREF_NAME")
private val PREF_THEME_MODE = stringPreferencesKey("PREF_THEME_MODE")

@Singleton
class UserDataStorage @Inject constructor(
    private val provider: PreferencesProvider,
) {

    val userNameFlow: Flow<String?> get() = provider.userDataStore.get(PREF_NAME)
    suspend fun setUserName(value: String) {
        provider.userDataStore.set(PREF_NAME, value)
    }

    val themeModeFlow: Flow<ThemeMode>
        get() = provider.userDataStore.get(PREF_THEME_MODE).map {
            it?.let(ThemeMode::valueOf) ?: ThemeMode.System
        }

    suspend fun setThemeMode(value: ThemeMode) {
        provider.userDataStore.set(PREF_THEME_MODE, value.name)
    }

    var isStartOnBoardingFinished: Boolean
        get() = provider.prefs.getBoolean("isStartOnBoardingFinished", false)
        set(value) = provider.prefs.edit {
            putBoolean("isStartOnBoardingFinished", value)
        }

    var hasNft: Boolean
        get() = provider.prefs.getBoolean("hasNft", false)
        set(value) = provider.prefs.edit {
            putBoolean("hasNft", value)
        }

    var lastCheckedAvailableVersionCode: Int
        get() = provider.prefs.getInt("lastCheckedAvailableVersionCode", 0)
        set(value) = provider.prefs.edit {
            putInt("lastCheckedAvailableVersionCode", value)
        }

    fun reset(reason: LogOutReason? = null) {
        // todo
    }
}

enum class ThemeMode { Light, Dark, System }

enum class LogOutReason {
    Example
}