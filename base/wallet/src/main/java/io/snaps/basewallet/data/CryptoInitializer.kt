package io.snaps.basewallet.data

import android.app.Application
import io.snaps.corecrypto.core.CryptoKit

object CryptoInitializer {

    fun loadLibs() {
        System.loadLibrary("TrustWalletCore")
    }

    fun initKit(application: Application) {
        CryptoKit.init(application)
    }
}