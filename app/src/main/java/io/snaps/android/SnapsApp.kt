package io.snaps.android

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.SvgDecoder
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import io.snaps.basesources.NetworkStateSource
import io.snaps.basesources.featuretoggle.FeatureToggleUpdater
import io.snaps.corecommon.analytics.AnalyticsTracker
import io.snaps.corecommon.analytics.AnalyticsTrackerHolder
import io.snaps.corecommon.model.BuildInfo
import io.snaps.corecrypto.core.CryptoKit
import io.snaps.coredata.coroutine.ApplicationCoroutineScopeHolder
import io.snaps.coredata.network.ApiConfig
import io.snaps.coreui.notification.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import net.gotev.uploadservice.UploadServiceConfig
import net.gotev.uploadservice.protocols.multipart.MultipartUploadRequest
import javax.inject.Inject

@HiltAndroidApp
class SnapsApp : Application(), ApplicationCoroutineScopeHolder, ImageLoaderFactory {

    @Inject
    lateinit var tracker: AnalyticsTracker

    @Inject
    lateinit var featureToggleUpdater: FeatureToggleUpdater

    @Inject
    lateinit var networkStateSource: NetworkStateSource

    @Inject
    lateinit var notificationHelper: NotificationHelper // for channels to be inited

    @Inject
    lateinit var apiConfig: ApiConfig

    @Inject
    lateinit var buildInfo: BuildInfo

    override val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        System.loadLibrary("TrustWalletCore")
    }

    override fun onCreate() {
        super.onCreate()

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(
            // todo on release only
            true
        )

        AnalyticsTrackerHolder.init(tracker)

        CryptoKit.init(this)

        // todo move to wrapper with  notificationHelper
        UploadServiceConfig.initialize(
            context = this,
            defaultNotificationChannel = NotificationHelper.Channels.Upload.getId(this),
            debug = BuildConfig.DEBUG,
        )
    }

    override fun newImageLoader() = ImageLoader.Builder(this)
        .okHttpClient { apiConfig.okHttpBuilder().build() }
        .components { add(SvgDecoder.Factory()) }
        .crossfade(true)
        .build()
}