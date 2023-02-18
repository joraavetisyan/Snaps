package io.snaps.corecrypto.core.managers

import android.app.Activity
import io.snaps.corecrypto.other.BackgroundManager
import io.snaps.corecrypto.other.IKeyStoreManager
import io.snaps.corecrypto.other.ISystemInfoManager

class BackgroundStateChangeListener(
    private val systemInfoManager: ISystemInfoManager,
    private val keyStoreManager: IKeyStoreManager,
) : BackgroundManager.Listener {

    override fun willEnterForeground(activity: Activity) {

    }

    override fun didEnterBackground() {
    }

    override fun onAllActivitiesDestroyed() {
    }

}
