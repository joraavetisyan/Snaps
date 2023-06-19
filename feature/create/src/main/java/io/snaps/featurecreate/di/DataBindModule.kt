package io.snaps.featurecreate.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import io.snaps.featurecreate.viewmodel.VideoCompressor
import io.snaps.featurecreate.viewmodel.VideoCompressorImpl

@Module
@InstallIn(ViewModelComponent::class)
interface DataBindModule {

    @Binds
    @ViewModelScoped
    fun videoCompressor(bind: VideoCompressorImpl): VideoCompressor
}