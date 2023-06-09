package io.snaps.coredata.di

import dagger.hilt.internal.GeneratedComponentManager
import io.snaps.coredata.coroutine.UserSessionCoroutineScopeHolder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSessionComponentManager @Inject constructor(
    private val userComponentProvider: UserSessionComponentBuilder,
) : GeneratedComponentManager<UserSessionComponent> {

    var userSessionComponent: UserSessionComponent = userComponentProvider.build()

    fun onUserLoggedOut() {
        UserSessionCoroutineScopeHolder.cancel()
        rebuildComponent()
    }

    private fun rebuildComponent() {
        userSessionComponent = userComponentProvider.build()
    }

    override fun generatedComponent(): UserSessionComponent = userSessionComponent
}