package io.snaps.coredata.database

import androidx.core.content.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import io.snaps.corecommon.model.OnboardingType
import io.snaps.corecommon.model.Uuid
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
        set(value) = provider.prefs.edit { putBoolean("isStartOnBoardingFinished", value) }

    var countBannerViews: Int
        get() = provider.prefs.getInt("countBannerViews", 0)
        set(value) = provider.prefs.edit { putInt("countBannerViews", value) }

    var bannerVersion: Int
        get() = provider.prefs.getInt("bannerVersion", 1)
        set(value) = provider.prefs.edit { putInt("bannerVersion", value) }

    var captchaResult: String? = null

    var prodBaseUrl: String?
        get() = provider.prefs.getString("prodBaseUrl", null)
        set(value) = provider.prefs.edit { putString("prodBaseUrl", value) }

    fun isOnboardingShown(type: OnboardingType): Boolean {
        return provider.prefs.getBoolean("onboarding${type.name}", false)
    }

    fun setIsOnboardingShown(type: OnboardingType, value: Boolean) {
        provider.prefs.edit { putBoolean("onboarding${type.name}", value) }
    }

    fun getCreatedVideoCount(userId: Uuid, date: Long): Int {
        return provider.prefs.getInt("getCreatedVideoCount${userId}${date}", 0)
    }

    fun setCreatedVideoCount(userId: Uuid, date: Long, count: Int) {
        return provider.prefs.edit { putInt("getCreatedVideoCount${userId}${date}", count) }
    }

    fun getPostedInstagramTemplateCount(userId: Uuid, date: Long): Int {
        return provider.prefs.getInt("getPostedInstagramTemplateCount${userId}${date}", 0)
    }

    fun setPostedInstagramTemplateCount(userId: Uuid, date: Long, count: Int) {
        return provider.prefs.edit { putInt("getPostedInstagramTemplateCount${userId}${date}", count) }
    }

    fun reset(reason: LogOutReason? = null) {
        captchaResult = null
    }
}

enum class ThemeMode { Light, Dark, System }

enum class LogOutReason { Example }