package io.snaps.featurecreate.screen

import android.Manifest
import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.CameraSelector.DEFAULT_FRONT_CAMERA
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import io.snaps.corecommon.container.IconValue
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAll
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionL
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionS
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.button.SimpleButtonInlineL
import io.snaps.coreuicompose.uikit.button.SimpleButtonLightS
import io.snaps.coreuicompose.uikit.other.KeepScreenOn
import io.snaps.coreuicompose.uikit.other.Progress
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featurecreate.ScreenNavigator
import io.snaps.featurecreate.createVideoCaptureUseCase
import io.snaps.featurecreate.startRecordingVideo
import io.snaps.featurecreate.toTextValue
import io.snaps.featurecreate.viewmodel.CreateVideoViewModel
import io.snaps.featurecreate.viewmodel.RecordDelay
import io.snaps.featurecreate.viewmodel.RecordTiming
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CreateVideoScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<CreateVideoViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    viewModel.command.collectAsCommand {
        when (it) {
            is CreateVideoViewModel.Command.OpenPreviewScreen -> router.toPreviewScreen(it.uri)
            CreateVideoViewModel.Command.CloseScreen -> router.back()
        }
    }

    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
        )
    )

    LaunchedEffect(Unit) {
        permissionState.launchMultiplePermissionRequest()
    }

    BackHandler(enabled = !uiState.isRecording, onBack = router::back)

    val videoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {
            if (it != null) {
                viewModel.onVideoSelected(it)
            }
        },
    )

    CreateVideoScreen(
        isPermissionGranted = permissionState.allPermissionsGranted,
        uiState = uiState,
        onSelectFileClicked = { videoPicker.launch("video/*") },
        onCloseClicked = router::back,
        onDelayCanceled = viewModel::onDelayCanceled,
        onRecordDelaySelected = viewModel::onRecordDelaySelected,
        onGrantPermissionClicked = permissionState::launchMultiplePermissionRequest,
        onTimingSelected = viewModel::onTimingSelected,
        onRecordingStartClicked = viewModel::onRecordingStartClicked,
        onCameraChanged = viewModel::onCameraChanged,
        onRecordFinished = router::toPreviewScreen,
        onRecorded = viewModel::onRecorded,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateVideoScreen(
    isPermissionGranted: Boolean,
    uiState: CreateVideoViewModel.UiState,
    onCloseClicked: () -> Unit,
    onDelayCanceled: () -> Unit,
    onRecordDelaySelected: (RecordDelay) -> Unit,
    onGrantPermissionClicked: () -> Unit,
    onSelectFileClicked: () -> Unit,
    onTimingSelected: (RecordTiming) -> Unit,
    onRecordingStartClicked: (((Boolean) -> Unit) -> Unit) -> Unit,
    onCameraChanged: (Boolean) -> Unit,
    onRecordFinished: (String) -> Unit,
    onRecorded: (Long) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {},
    ) {
        Box {
            if (isPermissionGranted) {
                PermissionGrantedContent(
                    modifier = Modifier
                        .padding(it)
                        .inset(insetAll())
                        .padding(12.dp),
                    uiState = uiState,
                    onCloseClicked = onCloseClicked,
                    onRecordDelaySelected = onRecordDelaySelected,
                    onSelectFileClicked = onSelectFileClicked,
                    onTimingSelected = onTimingSelected,
                    onRecordingStartClicked = onRecordingStartClicked,
                    onRecordFinished = onRecordFinished,
                    onCameraChanged = onCameraChanged,
                    onRecorded = onRecorded,
                )
            } else {
                PermissionNotGrantedContent(
                    onGrantPermissionClicked = onGrantPermissionClicked,
                )
            }
        }
    }

    DelayTimer(delayValue = uiState.delayValue, onDelayCanceled = onDelayCanceled)
}

@Composable
fun PermissionNotGrantedContent(
    onGrantPermissionClicked: () -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        SimpleButtonActionL(
            modifier = Modifier.align(Alignment.Center),
            onClick = onGrantPermissionClicked,
        ) {
            SimpleButtonContent(text = StringKey.CreateVideoActionGrantPerms.textValue())
        }
    }
}

@Composable
fun BoxScope.PermissionGrantedContent(
    modifier: Modifier,
    uiState: CreateVideoViewModel.UiState,
    onCloseClicked: () -> Unit,
    onRecordDelaySelected: (RecordDelay) -> Unit,
    onSelectFileClicked: () -> Unit,
    onTimingSelected: (RecordTiming) -> Unit,
    onRecordingStartClicked: (((Boolean) -> Unit) -> Unit) -> Unit,
    onCameraChanged: (Boolean) -> Unit,
    onRecordFinished: (String) -> Unit,
    onRecorded: (Long) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    var recording by remember { mutableStateOf<Recording?>(null) }
    val previewView: PreviewView = remember { PreviewView(context) }
    val videoCapture: MutableState<VideoCapture<Recorder>?> = remember { mutableStateOf(null) }
    val cameraSelector: MutableState<CameraSelector> = remember {
        mutableStateOf(DEFAULT_BACK_CAMERA)
    }

    KeepScreenOn()

    LaunchedEffect(previewView) {
        videoCapture.value = context.createVideoCaptureUseCase(
            lifecycleOwner = lifecycleOwner,
            cameraSelector = cameraSelector.value,
            previewView = previewView,
        )
    }

    fun start() {
        onRecordingStartClicked { onRecordingStatusChanged ->
            videoCapture.value?.let {
                recording = context.startRecordingVideo(it) { event ->
                    when (event) {
                        is VideoRecordEvent.Start -> {
                            onRecordingStatusChanged(true)
                        }
                        is VideoRecordEvent.Pause -> {}
                        is VideoRecordEvent.Resume -> {}
                        is VideoRecordEvent.Finalize -> {
                            val uri = event.outputResults.outputUri
                            if (uri != Uri.EMPTY) {
                                onRecordFinished(uri.path!!)
                            }
                            onRecordingStatusChanged(false)
                        }
                        is VideoRecordEvent.Status -> {
                            onRecorded(event.recordingStats.recordedDurationNanos)
                        }
                    }
                }
            }
        }
    }

    fun stop() {
        recording?.stop()
        recording = null
    }

    fun changeCamera() {
        val isFrontCamera = cameraSelector.value == DEFAULT_FRONT_CAMERA
        onCameraChanged(!isFrontCamera)
        cameraSelector.value = if (isFrontCamera) DEFAULT_BACK_CAMERA else DEFAULT_FRONT_CAMERA
        coroutineScope.launch {
            videoCapture.value = context.createVideoCaptureUseCase(
                lifecycleOwner = lifecycleOwner,
                cameraSelector = cameraSelector.value,
                previewView = previewView,
            )
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = Modifier.fillMaxSize(),
    )

    Column(modifier = modifier) {
        Progress(
            modifier = Modifier.fillMaxWidth(),
            progress = uiState.progress,
            isDashed = false,
            backColor = AppTheme.specificColorScheme.white_40,
            fillColor = AppTheme.specificColorScheme.white,
            height = 6.dp,
        )
        Spacer(Modifier.height(16.dp))
        Box(modifier = Modifier.fillMaxSize()) {
            if (!uiState.isRecording) {
                Actions(
                    selectedRecordDelay = uiState.selectedDelay,
                    onCloseClicked = onCloseClicked,
                    onRecordDelaySelected = onRecordDelaySelected,
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
            ) {
                if (!uiState.isRecording) {
                    Timings(
                        uiState = uiState,
                        onTimingSelected = onTimingSelected,
                    )
                }
                Spacer(Modifier.height(40.dp))
                Controls(
                    uiState = uiState,
                    onChangeCameraClicked = ::changeCamera,
                    onSelectFileClicked = onSelectFileClicked,
                    onRecordingStartClicked = ::start,
                    onRecordingStopClicked = ::stop,
                )
            }
        }
    }
}

@Composable
private fun BoxScope.Actions(
    selectedRecordDelay: RecordDelay,
    onCloseClicked: () -> Unit,
    onRecordDelaySelected: (RecordDelay) -> Unit,
) {
    IconButton(
        onClick = onCloseClicked,
        modifier = Modifier.align(Alignment.TopStart),
    ) {
        Icon(
            painter = AppTheme.specificIcons.close.get(),
            contentDescription = null,
            modifier = Modifier.size(36.dp),
            tint = AppTheme.specificColorScheme.white,
        )
    }
    Row(
        modifier = Modifier.align(Alignment.TopEnd),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (selectedRecordDelay != RecordDelay._0) {
            Text(
                selectedRecordDelay.toTextValue().get(),
                color = AppTheme.specificColorScheme.white,
                modifier = Modifier.padding(end = 8.dp),
            )
        }
        Column {
            var isMenuExpanded by remember { mutableStateOf(false) }
            IconButton(
                onClick = { isMenuExpanded = !isMenuExpanded },
            ) {
                Image(
                    painter = AppTheme.specificIcons.cameraTimer.get(),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                )
            }
            DropdownMenu(
                expanded = isMenuExpanded,
                onDismissRequest = { isMenuExpanded = false },
            ) {
                RecordDelay.values().forEach {
                    DropdownMenuItem(
                        onClick = {
                            onRecordDelaySelected(it)
                            isMenuExpanded = false
                        },
                    ) {
                        Text(
                            text = it.toTextValue().get(),
                            style = AppTheme.specificTypography.bodySmall,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Timings(
    uiState: CreateVideoViewModel.UiState,
    onTimingSelected: (RecordTiming) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RecordTiming.values().forEach {
            if (uiState.isSelected(it)) {
                SimpleButtonActionS(onClick = {}) {
                    SimpleButtonContent(text = it.toTextValue())
                }
            } else {
                SimpleButtonLightS(onClick = { onTimingSelected(it) }) {
                    SimpleButtonContent(text = it.toTextValue())
                }
            }
            if (it != RecordTiming.values().last()) {
                Spacer(Modifier.width(16.dp))
            }
        }
    }
}

@Composable
private fun Controls(
    uiState: CreateVideoViewModel.UiState,
    onChangeCameraClicked: () -> Unit,
    onSelectFileClicked: () -> Unit,
    onRecordingStartClicked: () -> Unit,
    onRecordingStopClicked: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconTextButton(
            isVisible = !uiState.isRecording,
            iconValue = AppTheme.specificIcons.flipCamera,
            onClicked = onChangeCameraClicked,
            text = StringKey.CreateVideoActionFlip.textValue(),
        )
        IconButton(
            onClick = {
                if (uiState.isRecording) {
                    onRecordingStopClicked()
                } else {
                    onRecordingStartClicked()
                }
            },
            modifier = Modifier.background(AppTheme.specificColorScheme.uiAccent, CircleShape),
        ) {
            Icon(
                painter = if (uiState.isRecording) {
                    AppTheme.specificIcons.pause
                } else {
                    AppTheme.specificIcons.play
                }.get(),
                contentDescription = null,
                tint = AppTheme.specificColorScheme.white,
                modifier = Modifier
                    .padding(8.dp)
                    .size(140.dp),
            )
        }
        IconTextButton(
            isVisible = !uiState.isRecording,
            iconValue = AppTheme.specificIcons.picture,
            onClicked = onSelectFileClicked,
            text = StringKey.CreateVideoActionChoose.textValue(),
        )
    }
}

@Composable
private fun IconTextButton(
    isVisible: Boolean,
    iconValue: IconValue,
    onClicked: () -> Unit,
    text: TextValue,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.alpha(if (isVisible) 1f else 0f),
    ) {
        IconButton(enabled = isVisible, onClick = onClicked) {
            Icon(
                painter = iconValue.get(),
                contentDescription = null,
                tint = AppTheme.specificColorScheme.white,
                modifier = Modifier.size(144.dp),
            )
        }
        Text(
            text.get(),
            color = AppTheme.specificColorScheme.white,
        )
    }
}

@Composable
private fun DelayTimer(
    delayValue: String?,
    onDelayCanceled: () -> Unit,
) {
    if (delayValue != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppTheme.specificColorScheme.white_90),
        ) {
            Text(
                text = delayValue,
                modifier = Modifier.align(Alignment.Center),
                color = AppTheme.specificColorScheme.textPrimary,
                style = AppTheme.specificTypography.displayLarge.copy(fontSize = 124.sp),
            )
            SimpleButtonInlineL(
                onClick = onDelayCanceled,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp),
            ) {
                SimpleButtonContent(text = StringKey.ActionCancel.textValue())
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun Preview() {
}