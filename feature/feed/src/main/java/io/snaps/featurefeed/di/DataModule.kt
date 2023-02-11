package io.snaps.featurefeed.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class DataModule

@Module
@InstallIn(ViewModelComponent::class)
interface DataBindModule