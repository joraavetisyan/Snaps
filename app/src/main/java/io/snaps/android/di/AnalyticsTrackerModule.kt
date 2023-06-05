package io.snaps.android.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.snaps.android.analyticsimpl.AnalyticsTrackerSentryImpl
import io.snaps.corecommon.analytics.AnalyticsTracker
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AnalyticsTrackerModule {

    @Binds
    @Singleton
    fun analyticsTracker(tracker: AnalyticsTrackerSentryImpl): AnalyticsTracker
}