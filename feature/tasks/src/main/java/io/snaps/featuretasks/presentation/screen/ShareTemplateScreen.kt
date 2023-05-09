package io.snaps.featuretasks.presentation.screen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.corecommon.R as commonR
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.ext.startSharePhotoIntent
import io.snaps.corecommon.strings.StringKey
import io.snaps.corenavigation.base.resultFlow
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAllExcludeTop
import io.snaps.coreuicompose.tools.toPx
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionM
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.button.SimpleButtonInlineM
import io.snaps.coreuicompose.uikit.duplicate.ActionIconData
import io.snaps.coreuicompose.uikit.duplicate.SimpleTopAppBar
import io.snaps.coreuicompose.uikit.status.FullScreenLoaderUi
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featuretasks.ScreenNavigator
import io.snaps.featuretasks.presentation.viewmodel.ShareTemplateViewModel
import toTypeface

@Composable
fun ShareTemplateScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<ShareTemplateViewModel>()
    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsState()

    navHostController.resultFlow<String>()?.collectAsCommand(action = viewModel::onAuthCodeResultReceived)

    viewModel.command.collectAsCommand {
        when (it) {
            is ShareTemplateViewModel.Command.OpenWebView -> router.toWebView()
            is ShareTemplateViewModel.Command.OpenShareDialog -> context.startSharePhotoIntent(it.uri)
        }
    }

    ShareTemplateScreen(
        uiState = uiState,
        onBackClicked = router::back,
        onSaveButtonClicked = viewModel::onSavePhotoButtonClicked,
        onShareIconClicked = viewModel::onShareIconClicked,
        onPostToInstagramButtonClicked = viewModel::onPostToInstagramButtonClicked,
    )

    FullScreenLoaderUi(isLoading = uiState.isLoading)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShareTemplateScreen(
    uiState: ShareTemplateViewModel.UiState,
    onBackClicked: () -> Boolean,
    onShareIconClicked: (Bitmap) -> Unit,
    onSaveButtonClicked: (Bitmap) -> Unit,
    onPostToInstagramButtonClicked: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current
    val templatePhoto = generateTemplatePhoto(
        bitmap = BitmapFactory.decodeResource(context.resources, commonR.drawable.img_template),
        qr = uiState.qr,
    )
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SimpleTopAppBar(
                title = StringKey.TaskShareTitle.textValue(),
                navigationIcon = AppTheme.specificIcons.back to onBackClicked,
                scrollBehavior = scrollBehavior,
                titleHorizontalArrangement = Arrangement.Center,
                actions = listOf(
                    ActionIconData(
                        icon = AppTheme.specificIcons.share,
                        color = AppTheme.specificColorScheme.darkGrey,
                        onClick = { onShareIconClicked(templatePhoto) },
                    ),
                ),
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .inset(insetAllExcludeTop())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Image(
                bitmap = templatePhoto.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
            )
            uiState.instagramConnectTileState.Content(
                    modifier = Modifier
                        .shadow(elevation = 16.dp, shape = AppTheme.shapes.medium)
                        .background(
                            color = AppTheme.specificColorScheme.white,
                            shape = AppTheme.shapes.medium,
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
            )
            SimpleButtonActionM(
                onClick = onPostToInstagramButtonClicked,
                modifier = Modifier.fillMaxWidth(),
            ) {
                SimpleButtonContent(text = StringKey.TaskShareActionPostToInstagram.textValue())
            }
            SimpleButtonInlineM(
                onClick = { onSaveButtonClicked(templatePhoto) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                SimpleButtonContent(text = StringKey.TaskShareActionSavePhoto.textValue())
            }
        }
    }
}

@Composable
private fun generateTemplatePhoto(
    bitmap: Bitmap,
    qr: Bitmap?,
): Bitmap {
    val templateBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
    Canvas(templateBitmap.asImageBitmap()).apply {
        with(this.nativeCanvas) {
            val padding = 64.dp.toPx()
            val staticLayout = getStaticLayout(
                text = StringKey.TaskShareFieldEarnCryptocurrencies.textValue().get().text,
                paint = getTextPaint(
                    size = 130.sp.toPx(),
                    typeface = AppTheme.specificTypography.headlineLarge.toTypeface(),
                ),
                width = bitmap.width - padding.toInt(),
            )
            val staticLayout2 = getStaticLayout(
                text = StringKey.TaskShareFieldDownloadApp.textValue().get().text,
                paint = getTextPaint(
                    size = 60.sp.toPx(),
                    typeface = AppTheme.specificTypography.bodyLarge.toTypeface(),
                ),
                width = bitmap.width / 2 - padding.toInt() * 2,
            )
            drawBitmap(bitmap, 0f, 0f, null)
            qr?.let {
                val top = bitmap.height - it.height - padding
                drawBitmap(it, padding, top, null)
            }
            translate(padding, padding)
            staticLayout.draw(this)
            translate(0f, staticLayout.height + padding)
            staticLayout2.draw(this)
        }
    }
    return templateBitmap
}

private fun getStaticLayout(
    text: String,
    paint: TextPaint,
    width: Int,
): StaticLayout {
    return StaticLayout
        .Builder
        .obtain(text, 0, text.length, paint, width,)
        .setAlignment(Layout.Alignment.ALIGN_NORMAL)
        .build()
}

private fun getTextPaint(
    size: Float,
    typeface: Typeface,
) = TextPaint().apply {
    isAntiAlias = true
    this.typeface = typeface
    textSize = size
    color = Color.WHITE
}