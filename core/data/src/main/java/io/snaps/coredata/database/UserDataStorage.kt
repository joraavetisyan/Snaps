package io.snaps.coredata.database

import androidx.core.content.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val PREF_THEME_MODE = stringPreferencesKey("PREF_THEME_MODE")

@Singleton
class UserDataStorage @Inject constructor(
    private val provider: PreferencesProvider,
) {

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

    var lastCheckedAvailableVersionCode: Int
        get() = provider.prefs.getInt("lastCheckedAvailableVersionCode", 0)
        set(value) = provider.prefs.edit {
            putInt("lastCheckedAvailableVersionCode", value)
        }

    var instagramUsername: String
        get() = provider.prefs.getString("instagramUsername", "").orEmpty()
        set(value) = provider.prefs.edit {
            putString("instagramUsername", value)
        }

    var instagramId: String?
        get() = provider.prefs.getString("instagramId", null)
        set(value) = provider.prefs.edit {
            putString("instagramId", value)
        }

    fun reset(reason: LogOutReason? = null) {
    }
}

enum class ThemeMode { Light, Dark, System }

enum class LogOutReason { Example }