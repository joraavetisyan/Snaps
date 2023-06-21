package io.snaps.featurecreate

import android.annotation.SuppressLint
import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import androidx.lifecycle.LifecycleOwner
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.featurecreate.viewmodel.RecordDelay
import io.snaps.featurecreate.viewmodel.RecordTiming
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val filenameFormat = "yyyy-MM-dd-HH-mm-ss-SSS"

fun RecordTiming.toTextValue() = if (seconds > 60) {
    StringKey.FieldMinutes.textValue((seconds / 60).toString())
} else {
    StringKey.FieldSeconds.textValue(seconds.toString())
}

fun RecordDelay.toTextValue() = when (this) {
    RecordDelay._0 -> StringKey.ActionCancel.textValue()
    else -> StringKey.FieldSecondsShort.textValue(seconds.toString())
}

suspend fun Context.createVideoCaptureUseCase(
    lifecycleOwner: LifecycleOwner,
    cameraSelector: CameraSelector,
    previewView: PreviewView,
): VideoCapture<Recorder> {
    val preview = Preview.Builder()
        .build()
        .apply { setSurfaceProvider(previewView.surfaceProvider) }

    val qualitySelector = QualitySelector.from(
        Quality.FHD,
        FallbackStrategy.lowerQualityOrHigherThan(Quality.FHD)
    )
    val recorder = Recorder.Builder()
        .setExecutor(executor)
        .setQualitySelector(qualitySelector)
        .build()
    val videoCapture = VideoCapture.withOutput(recorder)

    val cameraProvider = getCameraProvider()
    cameraProvider.unbindAll()
    cameraProvider.bindToLifecycle(
        lifecycleOwner,
        cameraSelector,
        preview,
        videoCapture,
    )

    return videoCapture
}

private val Context.executor get() = ContextCompat.getMainExecutor(this)

private suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { future ->
        future.addListener({ continuation.resume(future.get()) }, executor)
    }
}

@SuppressLint("MissingPermission") // withAudioEnabled, false check error, it's requested
fun Context.startRecordingVideo(
    videoCapture: VideoCapture<Recorder>,
    consumer: Consumer<VideoRecordEvent>,
): Recording {
    // todo use file manager
    // todo clear cache, once the recorded video is not needed or save in gallery initially
    val outputDirectory: File = externalCacheDirs.firstOrNull()?.let {
        File(it, "snaps").apply { mkdirs() }
    }?.takeIf(File::exists) ?: filesDir

    val videoFile = File(
        outputDirectory,
        SimpleDateFormat(filenameFormat, Locale.US).format(System.currentTimeMillis()) + ".mp4",
    )

    val outputOptions = FileOutputOptions.Builder(videoFile).build()

    return videoCapture.output
        .prepareRecording(this, outputOptions)
        .withAudioEnabled()
        .start(executor, consumer)
}