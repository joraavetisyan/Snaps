package io.snaps.featurecreate.screen

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.mutableStateOf
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
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionL
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.duplicate.SimpleTopAppBar
import io.snaps.coreuicompose.uikit.input.SimpleTextField
import io.snaps.coreuicompose.uikit.status.FullScreenLoaderUi
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.LocalStringHolder
import io.snaps.coreuitheme.compose.typography
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
        onPublishClicked = { viewModel.onPublishClicked(thumbnail = it) },
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(padding.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TitleTextField(uiState = uiState, onTitleChanged = onTitleChanged)
            /*DescriptionTextField(uiState = uiState, onDescriptionChanged = onDescriptionChanged)*/
            Text(
                text = LocalStringHolder.current(StringKey.UploadVideoTitlePreview),
                style = typography { titleSmall },
            )
            if (!uiState.isRetrievingBitmaps) {
                val previewWidth = config.screenWidthDp.minus(padding * 2)
                val frameSize = previewWidth / uiState.visibleFrameCount
                val pagerState = rememberPagerState()
                val selectedBitmap by remember(uiState.bitmaps) {
                    derivedStateOf { uiState.getBitmap(pagerState.currentPage) }
                }
                selectedBitmap?.let { bitmap ->
                    Canvas(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(bitmap.width.toFloat() / bitmap.height),
                    ) {
                        drawBitmap(bitmap)
                    }
                }
                val height by remember(selectedBitmap) {
                    mutableStateOf(
                        selectedBitmap?.let { it.height * frameSize / it.width }?.dp ?: frameSize.dp
                    )
                }
                Box {
                    HorizontalPager(
                        modifier = Modifier
                            .height(height)
                            .width((uiState.visibleFrameCount * frameSize).dp),
                        state = pagerState,
                        pageCount = uiState.frameCount + (uiState.visibleFrameCount - 1), // for white space when all scrolled to left; -1 -> to leave one visible
                        pageSize = PageSize.Fixed(frameSize.dp),
                        beyondBoundsPageCount = uiState.visibleFrameCount,
                    ) { page ->
                        if (page < uiState.frameCount) {
                            uiState.bitmaps[page]?.let { bitmap ->
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
                            .height(height)
                            .width(frameSize.dp)
                            .border(2.dp, Color.Red)
                            .align(Alignment.CenterStart),
                    )
                }
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
    FullScreenLoaderUi(isLoading = uiState.isLoading || uiState.isRetrievingBitmaps)
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
            imeAction = ImeAction.Done,
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