package io.snaps.corecrypto.other

import android.app.Application
import io.snaps.corecrypto.core.security.KeyStoreValidationResult
import java.util.Date
import javax.crypto.SecretKey

interface ICoreApp {
    var backgroundManager: BackgroundManager
    var encryptionManager: IEncryptionManager
    var systemInfoManager: ISystemInfoManager
    var keyStoreManager: IKeyStoreManager
    var keyProvider: IKeyProvider
    var thirdKeyboardStorage: IThirdKeyboard
    var instance: Application

    val testMode: Boolean
}

interface IEncryptionManager {
    fun encrypt(data: String): String
    fun decrypt(data: String): String
}

interface ISystemInfoManager {
    val appVersion: String
    val deviceModel: String
    val osVersion: String
}

interface IThirdKeyboard {
    var isThirdPartyKeyboardAllowed: Boolean
}

interface IKeyStoreManager {
    fun validateKeyStore(): KeyStoreValidationResult
    fun removeKey()
    fun resetApp(reason: String)
}

interface IKeyStoreCleaner {
    var encryptedSampleText: String?
    fun cleanApp()
}

interface IKeyProvider {
    fun getKey(): SecretKey
}

interface ICurrentDateProvider {
    val currentDate: Date
}
