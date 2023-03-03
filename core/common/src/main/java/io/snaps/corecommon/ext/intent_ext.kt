package io.snaps.corecommon.ext

import android.content.Context
import android.content.Intent

fun Context.startShareVideoIntent(url: String, packageName: String) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        setPackage(packageName)
        putExtra(Intent.EXTRA_TEXT, url)
    }
    startActivity(sendIntent)
}

fun Context.startShareLinkIntent(url: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, url)
    }
    val shareIntent = Intent.createChooser(intent, null)
    startActivity(shareIntent)
}