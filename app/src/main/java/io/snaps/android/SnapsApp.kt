package io.snaps.android

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.SvgDecoder
import com.appsflyer.AppsFlyerLib
import dagger.hilt.android.HiltAndroidApp
import io.snaps.basesources.NetworkStateSource
import io.snaps.basewallet.data.blockchain.CryptoInitializer
import io.snaps.corecommon.analytics.AnalyticsTracker
import io.snaps.corecommon.analytics.AnalyticsTrackerHolder
import io.snaps.corecommon.model.BuildInfo
import io.snaps.coredata.coroutine.ApplicationCoroutineScopeHolder
import io.snaps.coredata.network.ApiConfig
import io.snaps.coreui.notification.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

private const val AF_DEV_KEY = "4UBFreaFUz5JpLrC9KfxZY"

@HiltAndroidApp
class SnapsApp : Application(), ApplicationCoroutineScopeHolder, ImageLoaderFactory {

    // Injections for the init blocks to be executed

    @Inject
    lateinit var tracker: AnalyticsTracker

    @Inject
    lateinit var networkStateSource: NetworkStateSource

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var apiConfig: ApiConfig

    @Inject
    lateinit var buildInfo: BuildInfo

    override val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        CryptoInitializer.loadLibs()
    }

    override fun onCreate() {
        super.onCreate()

        AnalyticsTrackerHolder.init(tracker)

        CryptoInitializer.initKit(this)

        AppsFlyerLib.getInstance().apply {
            setDebugLog(true)
            init(AF_DEV_KEY, null, applicationContext)
            start(applicationContext)
        }
    }

    override fun newImageLoader() = ImageLoader.Builder(this)
        .okHttpClient { apiConfig.okHttpBuilder().build() }
        .components { add(SvgDecoder.Factory()) }
        .crossfade(true)
        .build()
}