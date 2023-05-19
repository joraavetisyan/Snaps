package io.snaps.coredata.di

import dagger.hilt.internal.GeneratedComponentManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSessionComponentManager @Inject constructor(
    private val userComponentProvider: UserSessionComponentBuilder
) : GeneratedComponentManager<UserSessionComponent> {

    var userSessionComponent: UserSessionComponent = userComponentProvider.build()

    private fun rebuildComponent() {
        userSessionComponent = userComponentProvider.build()
    }

    fun onUserLoggedOut() = rebuildComponent()

    override fun generatedComponent(): UserSessionComponent = userSessionComponent
}