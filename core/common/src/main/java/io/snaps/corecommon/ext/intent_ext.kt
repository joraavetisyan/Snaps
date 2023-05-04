package io.snaps.corecommon.ext

import android.content.Context
import android.content.Intent
import android.net.Uri
import io.snaps.corecommon.model.SocialNetwork

fun Context.startShareLinkIntent(url: String, title: String? = null) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, title)
        putExtra(Intent.EXTRA_TEXT, url)
    }
    val shareIntent = Intent.createChooser(intent, null)
    startActivity(shareIntent)
}

fun Context.startSharePhotoToInstagramIntent(uri: Uri) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/*"
        putExtra(Intent.EXTRA_STREAM, uri)
        setPackage(SocialNetwork.Instagram.url)
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }
    val shareIntent = Intent.createChooser(intent, null)
    startActivity(shareIntent)
}

fun Context.startSharePhotoIntent(uri: Uri, text: String? = null) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/*"
        putExtra(Intent.EXTRA_STREAM, uri)
        text?.let { putExtra(Intent.EXTRA_TEXT, it) }
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }
    val shareIntent = Intent.createChooser(intent, null)
    startActivity(shareIntent)
}