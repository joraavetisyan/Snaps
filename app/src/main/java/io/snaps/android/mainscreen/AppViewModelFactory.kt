package io.snaps.android.mainscreen

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.assisted.AssistedFactory
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ActivityComponent

object AppViewModelFactory {

    @AssistedFactory
    interface Factory {
        fun create(deeplink: String?): AppViewModel
    }

    @EntryPoint
    @InstallIn(ActivityComponent::class)
    interface ViewModelFactoryProvider {
        fun factory(): Factory
    }

    @Suppress("UNCHECKED_CAST")
    fun provide(activity: Activity): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T = EntryPointAccessors
                .fromActivity(activity, ViewModelFactoryProvider::class.java)
                .factory()
                .create(activity.intent.data?.toString()) as T
        }
    }
}