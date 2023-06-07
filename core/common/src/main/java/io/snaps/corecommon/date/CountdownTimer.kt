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

    fun start(scope: CoroutineScope, time: Duration, onTick: (Duration) -> Unit, onFinished: () -> Unit) {
        timerJob?.cancel()
        if (time <= 0.seconds) {
            onFinished()
            return
        }
        timerJob = scope.launch {
            var left = time.inWholeSeconds
            while (isActive && left > 0) {
                if (left <= 0) onFinished()
                else onTick(left.seconds)
                delay(1000L)
                left -= 1L
            }
        }
    }

    fun start(scope: CoroutineScope, time: LocalDateTime, onTick: (Duration) -> Unit, onFinished: () -> Unit) {
        val timeToTick = (time.toEpochMilli() - System.currentTimeMillis()).milliseconds
        start(scope = scope, time = timeToTick, onTick = onTick, onFinished = onFinished)
    }

    fun stop() {
        timerJob?.cancel()
    }
}