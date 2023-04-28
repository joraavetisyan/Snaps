package io.snaps.corecommon.ext

import android.util.Log

private const val tag = "simple_logger"

fun log(value: String) {
    Log.d(tag, value)
}

fun log(value: Exception) {
    Log.e(tag, "Error: ", value)
}