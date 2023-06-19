package io.snaps.featurecreate.viewmodel

import android.content.Context
import android.net.Uri
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.config.AppSpecificStorageConfiguration
import com.abedelazizshe.lightcompressorlibrary.config.Configuration
import dagger.hilt.android.qualifiers.ApplicationContext
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.ext.logE
import java.io.File
import javax.inject.Inject
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor as LightVideoCompressor

interface VideoCompressor {

    fun shouldCompress(uri: String): Boolean

    fun compress(uri: String, onFailure: () -> Unit, onSuccess: (String) -> Unit)
}

class VideoCompressorImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : VideoCompressor {

    override fun shouldCompress(uri: String): Boolean {
        val sizeInMb = File(uri).length() / 1024 / 1024
        return sizeInMb > 10
    }

    override fun compress(
        uri: String,
        onFailure: () -> Unit,
        onSuccess: (String) -> Unit,
    ) {
        LightVideoCompressor.start(
            context = context,
            uris = listOf(Uri.fromFile(File(uri))),
            configureWith = Configuration(
                isMinBitrateCheckEnabled = false,
            ),
            appSpecificStorageConfiguration = AppSpecificStorageConfiguration(
                videoName = "compressed_video",
            ),
            listener = object : CompressionListener {
                override fun onCancelled(index: Int) {
                    log("Video compress cancelled")
                    onFailure()
                }

                override fun onFailure(index: Int, failureMessage: String) {
                    logE("Video compress failure: $failureMessage")
                    onFailure()
                }

                override fun onProgress(index: Int, percent: Float) {
                }

                override fun onStart(index: Int) {
                }

                override fun onSuccess(index: Int, size: Long, path: String?) {
                    path ?: return
                    onSuccess(path)
                }
            },
        )
    }
}