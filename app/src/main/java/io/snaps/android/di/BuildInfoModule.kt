package io.snaps.android.di

import io.snaps.corecommon.model.BuildInfo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.snaps.android.BuildConfig
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class BuildInfoModule {

    @Provides
    @Singleton
    fun buildInfo() = BuildInfo(
        applicationId = BuildConfig.APPLICATION_ID,
        buildType = BuildConfig.BUILD_TYPE,
        versionCode = BuildConfig.VERSION_CODE,
        versionName = BuildConfig.VERSION_NAME,
    )
}