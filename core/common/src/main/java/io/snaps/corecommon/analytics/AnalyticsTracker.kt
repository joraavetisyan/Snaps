package io.snaps.corecommon.analytics

interface AnalyticsTracker {

    fun trackMessage(message: String)

    fun trackError(error: Throwable, attributes: Map<String, String> = emptyMap())

    fun trackEvent(event: Events, clazz: Class<*>, attributes: Map<String, String> = emptyMap())
}

enum class Events(val value: String) {
    START_SCREEN("START_SCREEN"),
    STOP_SCREEN("STOP_SCREEN")
}