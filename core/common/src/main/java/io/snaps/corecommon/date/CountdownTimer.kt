package io.snaps.corecommon.date

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class CountdownTimer {

    private var timerJob: Job? = null

    fun start(scope: CoroutineScope, timeToTick: Duration, onTick: (Duration) -> Unit, onFinished: () -> Unit) {
        timerJob?.cancel()
        if (timeToTick <= 0.seconds) {
            onFinished()
            return
        }
        timerJob = scope.launch {
            var left = timeToTick.inWholeSeconds
            while (isActive && left > 0) {
                onTick(left.seconds)
                delay(1000L)
                left--
                if (left <= 0) onFinished()
            }
        }
    }

    fun start(scope: CoroutineScope, tickUntil: LocalDateTime, onTick: (Duration) -> Unit, onFinished: () -> Unit) {
        val timeToTick = (tickUntil.toEpochMilli() - System.currentTimeMillis()).milliseconds
        start(scope = scope, timeToTick = timeToTick, onTick = onTick, onFinished = onFinished)
    }

    fun stop() {
        timerJob?.cancel()
    }
}