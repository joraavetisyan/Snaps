@file:Suppress("NOTHING_TO_INLINE") // inlining to get the java class simpleName

package io.snaps.corecommon.ext

import android.util.Log

private const val logTagPrefix = "snps" // keep this short to fit in the logs
val Any.logTag get() = "$logTagPrefix ${this.javaClass.simpleName}"

inline fun Any.log(value: String) {
    Log.d(logTag, value)
}

fun log(value: String) {
    Log.d(logTagPrefix, value)
}

inline fun Any.logE(value: String) {
    Log.e(logTag, "Error: ", Exception(value))
}

fun logE(value: String) {
    Log.e(logTagPrefix, "Error: ", Exception(value))
}

inline fun Any.log(value: Throwable, message: String = "Error: ") {
    Log.e(logTag, message, value)
}

fun log(value: Throwable, message: String = "Error: ") {
    Log.e(logTagPrefix, message, value)
}