package io.snaps.basesources.featuretoggle

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import io.snaps.corecommon.ext.log
import io.snaps.coredata.BuildConfig
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours

private val FETCH_TIME_DURATION_DEBUG = 0.hours
private val FETCH_TIME_DURATION_RELEASE = 0.hours

class FeatureToggleUpdater @Inject constructor(
    private val featureToggle: EditableFeatureToggle,
) {

    private val firebaseRemoteConfig by lazy { FirebaseRemoteConfig.getInstance() }

    init {
        // initDefaultValues()
        saveRemoteValues()
    }

    private fun initDefaultValues() {
        Feature.values().associate {
            it.key to it.defaultValue
        }.also { firebaseRemoteConfig.setDefaultsAsync(it) }
    }

    private fun saveRemoteValues() {
        firebaseRemoteConfig.fetch(
            when {
                BuildConfig.DEBUG -> FETCH_TIME_DURATION_DEBUG
                else -> FETCH_TIME_DURATION_RELEASE
            }.inWholeSeconds
        ).addOnSuccessListener {
            log("Fetched Firebase remote configs")
            firebaseRemoteConfig.activate().addOnCompleteListener {
                featureToggle.clearRemoteValues()
                Feature.values().filter(Feature::isRemote).forEach { feature ->
                    featureToggle.setRemoteValue(
                        feature = feature,
                        value = firebaseRemoteConfig.getBoolean(feature.key).also {
                            log("Fetched config $feature: $it")
                        },
                    )
                }
            }
        }.addOnFailureListener {
            log(it)
        }
    }
}