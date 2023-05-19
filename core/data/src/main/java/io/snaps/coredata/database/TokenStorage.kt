package io.snaps.coredata.database

import androidx.core.content.edit
import io.snaps.corecommon.model.Token
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenStorage @Inject constructor(
    private val provider: PreferencesProvider,
) {
    private val _resetState = MutableStateFlow<Unit?>(null)
    val resetState = _resetState.asStateFlow()

    var decodedAccessToken: Token = ""

    var decodedPinCode = IntArray(4) { 0 }

    var encodedRefreshToken: Token
        get() = provider.cryptoPrefs.getString("encodedRefreshToken", "").orEmpty()
        set(value) = provider.cryptoPrefs.edit {
            putString("encodedRefreshToken", value)
        }

    val pinCodeLength: Int = 4

    var salt: String
        get() = provider.cryptoPrefs.getString("salt", "").orEmpty()
        set(value) = provider.cryptoPrefs.edit {
            putString("salt", value)
        }

    var encodedPinCode: String
        get() = provider.prefs.getString("encodedPinCode", "").orEmpty()
        set(value) = provider.prefs.edit {
            putString("encodedPinCode", value)
        }

    var authToken: Token?
        get() = provider.cryptoPrefs.getString("authToken", null)
        set(value) = provider.cryptoPrefs.edit { putString("authToken", value) }

    fun reset() {
        decodedAccessToken = ""
        encodedRefreshToken = ""
        salt = ""
        encodedPinCode = ""
        authToken = null
        _resetState.tryEmit(Unit)
    }
}