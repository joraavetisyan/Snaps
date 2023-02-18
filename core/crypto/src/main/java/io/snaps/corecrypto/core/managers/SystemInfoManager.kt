package io.snaps.corecrypto.core.managers

import android.os.Build
import io.snaps.corecrypto.other.ISystemInfoManager

class SystemInfoManager : ISystemInfoManager {

    override val appVersion: String
        get() = "Fake app version"

    override val deviceModel: String
        get() = "${Build.MANUFACTURER} ${Build.MODEL}"

    override val osVersion: String
        get() = "Android ${Build.VERSION.RELEASE} (${Build.VERSION.SDK_INT})"
}
