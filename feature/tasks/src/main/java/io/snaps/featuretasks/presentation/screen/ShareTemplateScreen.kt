package io.snaps.featuretasks.presentation.screen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.ext.startSharePhotoIntent
import io.snaps.corecommon.strings.StringKey
import io.snaps.corenavigation.base.resultFlow
import io.snaps.coreui.viewmodel.collectAsCommand
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
import io.snaps.corecommon.R as commonR

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
            is ShareTemplateViewModel.Command.OpenWebView -> router.toConnectInstagramScreen()
            is ShareTemplateViewModel.Command.OpenShareDialog -> context.startSharePhotoIntent(it.uri)
            is ShareTemplateViewModel.Command.BackToTasksScreen -> router.backToTasksScreen()
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
        payments = uiState.payments,
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
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
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
    payments: String,
): Bitmap {
    val templateBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
    Canvas(templateBitmap.asImageBitmap()).apply {
        with(this.nativeCanvas) {
            val horizontalPadding = 128.dp.toPx()
            val topPadding = 300.dp.toPx()
            val radius = 24.dp.toPx()
            val staticLayout = getStaticLayout(
                text = "Я смотрю короткие видео в SNAPS и \nмне за это платят!", // todo localization
                paint = getTextPaint(
                    size = 100.sp.toPx(),
                    typeface = AppTheme.specificTypography.headlineLarge.toTypeface(),
                ),
                width = bitmap.width - horizontalPadding.toInt() * 2,
            )
            val textPaint = getTextPaint(
                size = 64.dp.toPx(),
                typeface = AppTheme.specificTypography.labelLarge.toTypeface(),
                textColor = AppTheme.specificColorScheme.textPrimary.toArgb(),
            )
            val text1 = "Пиши" // todo localization
            val text1Bounds = getTextBounds(textPaint, text1)

            val text2 = "мне!" // todo localization
            val text2Bounds = getTextBounds(textPaint, text2)

            val paddingBetweenTexts = 92.dp.toPx()
            val textsWidth = text2Bounds.width() + text1Bounds.width() + paddingBetweenTexts
            val text_yOffset = topPadding + staticLayout.height + 64.dp.toPx()
            val textPadding = 40.dp.toPx()

            val text3 = "И я расскажу!" // todo localization
            val text3Bounds = getTextBounds(textPaint, text3)
            val text3_yOffset = text_yOffset + text1Bounds.height() + 64.dp.toPx()

            drawBitmap(bitmap, 0f, 0f, null)
            DrawRoundedCornerText(
                text = text3,
                yOffset = text3_yOffset,
                xOffset = (bitmap.width - text3Bounds.width()) / 2f,
                padding = textPadding,
                radius = radius,
                textPaint = textPaint,
                degrees = 0f,
            )
            DrawRoundedCornerText(
                text = text1,
                yOffset = text_yOffset,
                xOffset = (bitmap.width - textsWidth) / 2f,
                padding = textPadding,
                radius = radius,
                textPaint = textPaint,
                degrees = -8f,
            )
            DrawRoundedCornerText(
                text = text2,
                yOffset = text_yOffset,
                xOffset = (bitmap.width - textsWidth) / 2f + text1Bounds.width() + paddingBetweenTexts,
                padding = textPadding,
                radius = radius,
                textPaint = textPaint,
                degrees = 8f,
            )
            DrawPaymentsInfo(
                title = "Я сегодня заработал (-а)", // todo localization
                text = "$payments долларов", // todo localization
                radius = 48.dp.toPx(),
                yOffset = text3_yOffset + text3Bounds.height() + textPadding * 2 + 128.dp.toPx(),
                xOffset = 100.dp.toPx(),
                padding = 64.dp.toPx(),
                width = bitmap.width - 100.dp.toPx() * 2f,
            )
            translate(horizontalPadding, topPadding)
            staticLayout.draw(this)
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
        .obtain(text, 0, text.length, paint, width)
        .setIncludePad(true)
        .setAlignment(Layout.Alignment.ALIGN_CENTER)
        .build()
}

private fun getTextPaint(
    size: Float,
    typeface: Typeface,
    textColor: Int = Color.WHITE,
) = TextPaint().apply {
    isAntiAlias = true
    this.typeface = typeface
    textSize = size
    color = textColor
}

private fun getTextBounds(
    paint: TextPaint,
    text: String,
): Rect {
    val bounds = Rect()
    paint.getTextBounds(text, 0, text.length, bounds)
    return bounds
}

@Composable
private fun android.graphics.Canvas.DrawRoundedCornerText(
    text: String,
    radius: Float,
    yOffset: Float,
    xOffset: Float,
    padding: Float,
    textPaint: TextPaint,
    degrees: Float,
) {
    val textBounds = getTextBounds(textPaint, text)
    val rectanglePaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        setShadowLayer(radius, 0f, 0f, AppTheme.specificColorScheme.darkGrey.toArgb())
    }
    val rectF = RectF(
        /* left = */ xOffset - padding,
        /* top = */ yOffset,
        /* right = */ xOffset + textBounds.width() + padding,
        /* bottom = */ yOffset + textBounds.height() + padding * 2,
    )

    save()
    if (degrees > 0) {
        rotate(degrees, xOffset + textBounds.width() + padding, yOffset + textBounds.height() + padding * 2)
    } else {
        rotate(degrees, xOffset, yOffset)
    }
    drawRoundRect(rectF, radius, radius, rectanglePaint)
    drawText(
        /* text = */ text,
        /* x = */ xOffset,
        /* y = */ yOffset + padding * 2,
        /* paint = */ textPaint,
    )
    restore()
}

@Composable
private fun android.graphics.Canvas.DrawPaymentsInfo(
    title: String,
    text: String,
    radius: Float,
    yOffset: Float,
    xOffset: Float,
    padding: Float,
    width: Float,
) {
    val context = LocalContext.current
    val bitmap = BitmapFactory.decodeResource(context.resources, commonR.drawable.img_snaps_logo)
    val textPaint = getTextPaint(
        size = 56.dp.toPx(),
        typeface = AppTheme.specificTypography.labelLarge.toTypeface(),
        textColor = AppTheme.specificColorScheme.textPrimary.toArgb(),
    )
    val textBounds = getTextBounds(textPaint, text)
    val titleBounds = getTextBounds(textPaint, title)
    val height = if (textBounds.height() + titleBounds.height() > bitmap.height) {
        bitmap.height + padding * 2
    } else textBounds.height() + titleBounds.height() + padding * 2
    val textsHeight = textBounds.height() + titleBounds.height()
    val textVerticalPadding = (height - textsHeight) / 2f - (textPaint.descent() + textPaint.ascent())
    val bitmapVerticalPadding = (height - bitmap.height) / 2f
    val rectanglePaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
    }
    val rectF = RectF(
        /* left = */ xOffset,
        /* top = */ yOffset,
        /* right = */ width + xOffset,
        /* bottom = */ yOffset + height,
    )
    drawRoundRect(rectF, radius, radius, rectanglePaint)
    drawBitmap(
        /* bitmap = */ bitmap,
        /* left = */ xOffset + padding / 2,
        /* top = */ yOffset + bitmapVerticalPadding,
        /* paint = */ null,
    )
    drawText(
        /* text = */ title,
        /* x = */ xOffset + bitmap.width + padding,
        /* y = */ yOffset + textVerticalPadding,
        /* paint = */ textPaint,
    )
    textPaint.color = AppTheme.specificColorScheme.uiAccent.toArgb()
    drawText(
        /* text = */ text,
        /* x = */ xOffset + bitmap.width + padding,
        /* y = */ yOffset + textVerticalPadding + titleBounds.height(),
        /* paint = */ textPaint,
    )
}