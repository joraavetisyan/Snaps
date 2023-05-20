package io.snaps.coredata.di

import dagger.hilt.DefineComponent
import dagger.hilt.components.SingletonComponent

@UserSessionScope
@DefineComponent(parent = SingletonComponent::class)
interface UserSessionComponent

@DefineComponent.Builder
interface UserSessionComponentBuilder {

    fun build(): UserSessionComponent
}