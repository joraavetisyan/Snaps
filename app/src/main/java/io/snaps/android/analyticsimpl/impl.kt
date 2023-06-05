package io.snaps.android.analyticsimpl

import android.content.Context
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.qualifiers.ApplicationContext
import io.sentry.Hint
import io.sentry.Sentry
import io.snaps.corecommon.analytics.AnalyticsTracker
import io.snaps.corecommon.analytics.Events
import javax.inject.Inject

class AnalyticsTrackerSentryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : AnalyticsTracker {

    override fun trackMessage(message: String) {
        Sentry.captureMessage(message)
    }

    override fun trackError(error: Throwable, attributes: Map<String, String>) {
        Sentry.captureException(error, Hint().apply { attributes.forEach(::set) })
    }

    override fun trackEvent(event: Events, clazz: Class<*>, attributes: Map<String, String>) {

    }
}

class AnalyticsTrackerFirebaseImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : AnalyticsTracker {

    private val firebaseAnalytics by lazy { FirebaseAnalytics.getInstance(context) }

    override fun trackMessage(message: String) {
    }

    override fun trackError(error: Throwable, attributes: Map<String, String>) {
    }

    override fun trackEvent(event: Events, clazz: Class<*>, attributes: Map<String, String>) {
        val bundle = bundleOf(*attributes.map { it.key to it.value }.toTypedArray())
        firebaseAnalytics.logEvent("${event.value} ${clazz.simpleName}", bundle)
    }
}