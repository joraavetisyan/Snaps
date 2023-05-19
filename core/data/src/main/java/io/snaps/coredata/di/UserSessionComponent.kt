package io.snaps.coredata.di

import dagger.hilt.DefineComponent
import dagger.hilt.components.SingletonComponent
import dagger.hilt.internal.GeneratedComponentManager
import javax.inject.Inject
import javax.inject.Qualifier
import javax.inject.Scope
import javax.inject.Singleton

@UserSessionScope
@DefineComponent(parent = SingletonComponent::class)
interface UserSessionComponent

@DefineComponent.Builder
interface UserSessionComponentBuilder {

    fun build(): UserSessionComponent
}