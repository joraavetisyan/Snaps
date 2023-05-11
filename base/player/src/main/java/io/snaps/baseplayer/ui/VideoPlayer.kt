package io.snaps.baseplayer.ui

import android.view.ViewGroup
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import io.snaps.corecommon.R
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.model.FullUrl
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuitheme.compose.withColors
import io.snaps.coreuitheme.compose.withIcons
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun VideoPlayer(
    networkUrl: FullUrl? = null,
    localUri: String? = null,
    shouldPlay: Boolean,
    isMuted: Boolean = false,
    onMuted: ((Boolean) -> Unit)? = null,
    isLiked: Boolean = false,
    onLiked: (() -> Unit)? = null,
    progressPollFrequencyInMillis: Long = 1000L, /*every 1 second*/
    onProgressChanged: ((Float) -> Unit)? = null, /*[0f,1f] every [progressChangePollFrequency]*/
    isScrolling: Boolean = false,
    isRepeat: Boolean = true,
) {
    require(networkUrl == null || localUri == null) {
        "Don't provide both local and network sources!"
    }

    val exoPlayer = rememberExoPlayerWithLifecycle(
        networkUrl = networkUrl,
        localUri = localUri,
        isRepeat = isRepeat,
    )
    val playerView = rememberPlayerView(exoPlayer)

    var isVolumeIconVisible by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.lottie_like),
    )
    val lottieAnimatable = rememberLottieAnimatable()

    val onProgressChangedRemembered by rememberUpdatedState(onProgressChanged)

    if (onProgressChangedRemembered != null) {
        LaunchedEffect(exoPlayer) {
            while (isActive) {
                onProgressChangedRemembered?.invoke(
                    try {
                        exoPlayer.currentPosition.toFloat() / exoPlayer.duration.toFloat()
                    } catch (e: Exception) {
                        log(e)
                        0f
                    }.coerceIn(0f, 1f)
                )
                delay(progressPollFrequencyInMillis)
            }
        }
    }

    Box {
        AndroidView(
            factory = { playerView },
            modifier = Modifier
                .pointerInput(isLiked, isMuted) {
                    detectTapGestures(
                        onDoubleTap = {
                            onLiked?.let {
                                it()
                                coroutineScope.launch {
                                    lottieAnimatable.animate(composition)
                                }
                            }
                        },
                        onTap = {
                            onMuted?.let {
                                if (exoPlayer.playWhenReady) {
                                    if (isMuted) {
                                        exoPlayer.volume = 1f
                                        it(false)
                                    } else {
                                        exoPlayer.volume = 0f
                                        it(true)
                                    }
                                    coroutineScope.launch {
                                        isVolumeIconVisible = true
                                        delay(800)
                                        isVolumeIconVisible = false
                                    }
                                }
                            }
                        },
                        onPress = {
                            if (!isScrolling) {
                                exoPlayer.playWhenReady = false
                                awaitRelease()
                                exoPlayer.playWhenReady = true
                            }
                        },
                        onLongPress = {}
                    )
                },
            update = {
                exoPlayer.volume = if (isMuted) 0f else 1f
                exoPlayer.playWhenReady = shouldPlay
            }
        )

        Crossfade(targetState = lottieAnimatable.isPlaying, label = "likeLottieAnimation") {
            if (it) {
                LottieAnimation(composition = composition, progress = { lottieAnimatable.progress })
            }
        }

        if (isVolumeIconVisible) {
            Icon(
                painter = withIcons { if (isMuted) volumeDown else volumeUp }.get(),
                contentDescription = null,
                tint = withColors { white },
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(100.dp),
            )
        }
    }

    DisposableEffect(key1 = true) {
        onDispose {
            exoPlayer.release()
        }
    }
}

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
private fun rememberExoPlayerWithLifecycle(
    networkUrl: String?,
    localUri: String?,
    isRepeat: Boolean,
): ExoPlayer {
    val context = LocalContext.current
    val exoPlayer = remember(networkUrl ?: localUri) {
        ExoPlayer.Builder(context).build().apply {
            videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
            repeatMode = if (isRepeat) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
            setHandleAudioBecomingNoisy(true)
            if (networkUrl != null) {
                /*val defaultDataSource = DefaultHttpDataSource.Factory()
                val source = ProgressiveMediaSource.Factory(defaultDataSource)
                    .createMediaSource(MediaItem.fromUri(networkUrl))
                setMediaSource(source)*/
                setMediaItem(MediaItem.fromUri(networkUrl))
            } else if (localUri != null) {
                val defaultDataSource = DefaultDataSource.Factory(context)
                val source = ProgressiveMediaSource.Factory(defaultDataSource)
                    .createMediaSource(MediaItem.fromUri(localUri))
                setMediaSource(source)
            }
            prepare()
        }
    }
    var appInBackground by remember {
        mutableStateOf(false)
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner, appInBackground) {
        val lifecycleObserver = getExoPlayerLifecycleObserver(exoPlayer, appInBackground) {
            appInBackground = it
        }
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }
    return exoPlayer
}

private fun getExoPlayerLifecycleObserver(
    exoPlayer: ExoPlayer,
    wasAppInBackground: Boolean,
    setWasAppInBackground: (Boolean) -> Unit,
): LifecycleEventObserver = LifecycleEventObserver { _, event ->
    when (event) {
        Lifecycle.Event.ON_RESUME -> {
            if (wasAppInBackground) {
                exoPlayer.playWhenReady = true
            }
            setWasAppInBackground(false)
        }

        Lifecycle.Event.ON_PAUSE -> {
            exoPlayer.playWhenReady = false
            setWasAppInBackground(true)
        }

        Lifecycle.Event.ON_STOP -> {
            exoPlayer.playWhenReady = false
            setWasAppInBackground(true)
        }

        Lifecycle.Event.ON_DESTROY -> {
            exoPlayer.release()
        }

        else -> {}
    }
}

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
private fun rememberPlayerView(exoPlayer: ExoPlayer): PlayerView {
    val context = LocalContext.current
    val playerView = remember {
        PlayerView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            useController = false
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            player = exoPlayer
            setShowBuffering(PlayerView.SHOW_BUFFERING_NEVER)
        }
    }
    DisposableEffect(key1 = true) {
        onDispose {
            playerView.player = null
        }
    }
    return playerView
}