package io.snaps.featurecreate.screen

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAllExcludeTop
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionL
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.duplicate.SimpleTopAppBar
import io.snaps.coreuicompose.uikit.input.SimpleTextField
import io.snaps.coreuicompose.uikit.other.Progress
import io.snaps.coreuicompose.uikit.other.SimpleCard
import io.snaps.coreuicompose.uikit.status.FullScreenLoaderUi
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.LocalStringHolder
import io.snaps.featurecreate.ScreenNavigator
import io.snaps.featurecreate.viewmodel.UploadViewModel

@Composable
fun UploadScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<UploadViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    viewModel.command.collectAsCommand {
        when (it) {
            UploadViewModel.Command.CloseScreen -> router.toProfileScreen()
        }
    }

    UploadScreen(
        uiState = uiState,
        onBackClicked = router::back,
        onPublishClicked = viewModel::onPublishClicked,
        onTitleChanged = viewModel::onTitleChanged,
        onDescriptionChanged = viewModel::onDescriptionChanged,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun UploadScreen(
    uiState: UploadViewModel.UiState,
    onBackClicked: () -> Boolean,
    onPublishClicked: (Bitmap?) -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = AppTheme.specificColorScheme.uiContentBg,
        topBar = {
            SimpleTopAppBar(
                title = StringKey.UploadVideoTitle.textValue(),
                navigationIcon = AppTheme.specificIcons.back to onBackClicked,
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        val padding = 16
        val config = LocalConfiguration.current
        val previewWidth = config.screenWidthDp.minus(padding * 2)

        val retriever = remember { MediaMetadataRetriever().apply { setDataSource(uiState.uri) } }
        val durationMillis = remember {
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L
        }
        val visibleFrameCount = 6
        val frameDuration = durationMillis / (2 * visibleFrameCount)
        val frameCount = (durationMillis / frameDuration).toInt().coerceAtLeast(1)
        val bitmaps = remember { mutableMapOf<Int, Bitmap?>() }
        val frameSize = previewWidth / visibleFrameCount
        val pagerState = rememberPagerState()
        val selectedBitmap by remember(bitmaps) {
            derivedStateOf {
                bitmaps[pagerState.currentPage] ?: bitmaps[frameCount - 1]
            }
        }
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .inset(insetAllExcludeTop())
                .padding(padding.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TitleTextField(uiState = uiState, onTitleChanged = onTitleChanged)
            /*DescriptionTextField(uiState = uiState, onDescriptionChanged = onDescriptionChanged)*/
            Text(text = "Select video preview", style = AppTheme.specificTypography.titleSmall)
            selectedBitmap?.let { bitmap ->
                Canvas(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(bitmap.width.toFloat() / bitmap.height),
                ) {
                    drawBitmap(bitmap)
                }
            }
            Box {
                HorizontalPager(
                    modifier = Modifier
                        .height(frameSize.dp)
                        .width((visibleFrameCount * frameSize).dp),
                    state = pagerState,
                    pageCount = frameCount + visibleFrameCount - 1, // to leave one visible
                    pageSize = PageSize.Fixed(frameSize.dp),
                    beyondBoundsPageCount = visibleFrameCount,
                ) { page ->
                    if (page < frameCount) {
                        bitmaps.getOrPut(page) {
                            retriever.getFrameAtTime(
                                page * frameDuration * 1000L, // micros
                                MediaMetadataRetriever.OPTION_CLOSEST,
                            )
                        }?.let { bitmap ->
                            Canvas(
                                modifier = Modifier.fillMaxSize(),
                            ) {
                                drawBitmap(bitmap)
                            }
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .size(frameSize.dp)
                        .border(2.dp, Color.Red)
                        .align(Alignment.CenterStart),
                )
            }
            if (uiState.uploadingProgress != null) {
                Progress(uploadingProgress = uiState.uploadingProgress)
            } else {
                SimpleButtonActionL(
                    onClick = { onPublishClicked(selectedBitmap) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.isPublishEnabled,
                ) {
                    SimpleButtonContent(
                        text = StringKey.UploadVideoActionPublish.textValue(),
                    )
                }
            }
        }
    }
    FullScreenLoaderUi(isLoading = uiState.isLoading)
}

private fun DrawScope.drawBitmap(bitmap: Bitmap) {
    val scaleX = size.width / bitmap.width.toFloat()
    val scaleY = size.height / bitmap.height.toFloat()
    val w = (bitmap.width * scaleX).toInt()
    val h = (bitmap.height * scaleY).toInt()
    drawImage(
        image = bitmap.asImageBitmap(),
        dstSize = IntSize(w, h),
    )
}

@Composable
private fun TitleTextField(
    uiState: UploadViewModel.UiState,
    onTitleChanged: (String) -> Unit,
) {
    SimpleTextField(
        modifier = Modifier.fillMaxWidth(),
        onValueChange = onTitleChanged,
        value = uiState.titleValue,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
        ),
        placeholder = {
            Text(
                text = LocalStringHolder.current(StringKey.UploadVideoHintTitle),
                style = AppTheme.specificTypography.titleSmall,
            )
        },
    )
}

@Composable
private fun DescriptionTextField(
    uiState: UploadViewModel.UiState,
    onDescriptionChanged: (String) -> Unit
) {
    SimpleTextField(
        modifier = Modifier.fillMaxWidth(),
        onValueChange = onDescriptionChanged,
        value = uiState.descriptionValue,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done,
        ),
        placeholder = {
            Text(
                text = LocalStringHolder.current(StringKey.UploadVideoHintDescription),
                style = AppTheme.specificTypography.titleSmall,
            )
        },
    )
}

@Composable
private fun Progress(
    uploadingProgress: Float,
) {
    SimpleCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Progress(
                modifier = Modifier.weight(1f),
                progress = uploadingProgress,
                isDashed = true,
                backColor = AppTheme.specificColorScheme.white_10,
                fillColor = AppTheme.specificColorScheme.white_20,
                height = 24.dp,
                cornerSize = 4.dp,
            )
            Text(
                text = "${(uploadingProgress * 100).toInt()}/100%",
                modifier = Modifier
                    .background(
                        color = AppTheme.specificColorScheme.darkGrey.copy(alpha = 0.5f),
                        shape = AppTheme.shapes.small,
                    )
                    .padding(4.dp),
            )
        }
    }
}