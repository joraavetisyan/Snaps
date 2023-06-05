package io.snaps.corecommon.analytics

object AnalyticsTrackerHolder {

    private lateinit var tracker: AnalyticsTracker

    fun init(tracker: AnalyticsTracker) {
        this.tracker = tracker
    }

    fun getInstance(): AnalyticsTracker = tracker
}