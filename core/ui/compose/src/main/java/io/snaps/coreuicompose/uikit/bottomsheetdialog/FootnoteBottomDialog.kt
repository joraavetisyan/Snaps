package io.snaps.coreuicompose.uikit.bottomsheetdialog

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.TextValue
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAllExcludeTop
import io.snaps.coreuicompose.tools.insetTop
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionL
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.indicator.HorizontalPagerIndicatorBridge
import io.snaps.coreuicompose.uikit.indicator.SheetIndicator
import io.snaps.coreuitheme.compose.AppTheme

data class FootnoteBottomDialogItem(
    val image: ImageValue,
    val title: TextValue,
    val text: TextValue,
    val onClick: (() -> Unit)? = null,
    val buttonText: TextValue? = null,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FootnoteBottomDialog(
    vararg data: FootnoteBottomDialogItem,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(
                1 - insetTop()
                    .asPaddingValues()
                    .calculateTopPadding() / LocalConfiguration.current.screenHeightDp.dp
            )
            .background(AppTheme.specificColorScheme.white)
            .inset(insetAllExcludeTop()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        SheetIndicator()
        val pagerState: PagerState = rememberPagerState()
        HorizontalPager(
            pageCount = data.size,
            state = pagerState,
            modifier = Modifier.weight(1f),
        ) {
            FootnoteBottomDialogPage(data = data[it])
        }
        if (data.size > 1) {
            HorizontalPagerIndicatorBridge(pagerState = pagerState, pageCount = data.size)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun FootnoteBottomDialogPage(
    data: FootnoteBottomDialogItem,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize(),
    ) {
        Image(
            painter = data.image.get(),
            contentDescription = null,
            modifier = Modifier.size(240.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = data.title.get(),
            style = AppTheme.specificTypography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = data.text.get(),
            style = AppTheme.specificTypography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (data.onClick != null && data.buttonText != null) {
            Spacer(modifier = Modifier.weight(1f))
            SimpleButtonActionL(
                onClick = data.onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                SimpleButtonContent(text = data.buttonText)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}