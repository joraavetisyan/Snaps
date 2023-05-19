package io.snaps.coredata.database

import androidx.core.content.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import io.snaps.corecommon.model.NftType
import io.snaps.corecommon.model.OnboardingType
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

    fun isOnboardingShown(type: OnboardingType): Boolean {
        return provider.prefs.getBoolean("onboarding${type.name}", false)
    }

    fun setIsOnboardingShown(type: OnboardingType, value: Boolean) {
        provider.prefs.edit {
            putBoolean("onboarding${type.name}", value)
        }
    }

    var lastCheckedAvailableVersionCode: Int
        get() = provider.prefs.getInt("lastCheckedAvailableVersionCode", 0)
        set(value) = provider.prefs.edit {
            putInt("lastCheckedAvailableVersionCode", value)
        }

    fun getProcessingNftCount(type: NftType): Int = provider.prefs.getInt("ProcessingNft${type.name}", 0)

    // Saves processed + processing nfts count
    fun setProcessingNftCount(type: NftType, totalCount: Int) = provider.prefs.edit {
        putInt("ProcessingNft${type.name}", totalCount)
    }

    fun getNonMintedNfts(): List<String> = provider.prefs.getStringSet("NonMintedNft", null)?.toList() ?: emptyList()

    fun setNonMintedNft(data: String) = provider.prefs.edit {
        putStringSet("NonMintedNft", getNonMintedNfts().toMutableList().run { add(data); this }.toSet())
    }

    fun removeNonMintedNft(data: String) = provider.prefs.edit {
        putStringSet("NonMintedNft", getNonMintedNfts().toMutableList().run { remove(data); this }.toSet())
    }

    fun getNonRepairedNfts(): List<String> =
        provider.prefs.getStringSet("NonRepairedNft", null)?.toList() ?: emptyList()

    fun setNonRepairedNft(data: String) = provider.prefs.edit {
        putStringSet("NonRepairedNft", getNonRepairedNfts().toMutableList().run { add(data); this }.toSet())
    }

    fun removeNonRepairedNft(data: String) = provider.prefs.edit {
        putStringSet("NonRepairedNft", getNonRepairedNfts().toMutableList().run { remove(data); this }.toSet())
    }

    fun reset(reason: LogOutReason? = null) {
        // todo do not clear onboarding related stuff
        provider.prefs.edit { clear() }
    }
}

enum class ThemeMode { Light, Dark, System }

enum class LogOutReason { Example }