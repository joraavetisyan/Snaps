package io.snaps.android

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import io.snaps.corecommon.analytics.AnalyticsTracker
import io.snaps.corecommon.analytics.AnalyticsTrackerHolder
import io.snaps.coredata.coroutine.ApplicationCoroutineScopeHolder
import io.snaps.basesources.featuretoggle.FeatureToggleUpdater
import io.snaps.basesources.NetworkStateSource
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import coil.decode.SvgDecoder
import io.snaps.coredata.network.ApiConfig

@HiltAndroidApp
class SnapsApp : Application(), ApplicationCoroutineScopeHolder, ImageLoaderFactory {

    @Inject lateinit var tracker: AnalyticsTracker

    @Inject lateinit var featureToggleUpdater: FeatureToggleUpdater

    @Inject lateinit var networkStateSource: NetworkStateSource

    @Inject lateinit var apiConfig: ApiConfig

    override val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()

        AnalyticsTrackerHolder.init(tracker)
    }

    override fun newImageLoader() = ImageLoader.Builder(this)
        .okHttpClient {
            apiConfig.okHttpBuilder()
                .build()
        }
        .components {
            add(SvgDecoder.Factory())
        }
        .crossfade(true)
        .build()
}