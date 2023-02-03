package io.snaps.baseprofile.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DataModule

@Module
@InstallIn(SingletonComponent::class)
interface DataBindModule