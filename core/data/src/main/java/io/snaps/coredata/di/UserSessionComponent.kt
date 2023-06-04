package io.snaps.coredata.di

import dagger.hilt.DefineComponent
import dagger.hilt.components.SingletonComponent

// NOTE: If entity depends on user session scoped entity, it must be user session scoped too,
// so it always gets the recreated dependencies
@UserSessionScope
@DefineComponent(parent = SingletonComponent::class)
interface UserSessionComponent

@DefineComponent.Builder
interface UserSessionComponentBuilder {

    fun build(): UserSessionComponent
}