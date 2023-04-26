package io.snaps.coreuicompose.uikit.bottomsheetdialog

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.TextValue
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionL
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.indicator.HorizontalPagerIndicatorBridge
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
    SimpleBottomDialogUI {
        item {
            val pagerState: PagerState = rememberPagerState()
            HorizontalPager(
                pageCount = data.size,
                state = pagerState,
            ) {
                FootnoteBottomSheetDialogPage(data = data[it])
            }
            HorizontalPagerIndicatorBridge(pagerState = pagerState, pageCount = data.size)
        }
    }
}

@Composable
private fun FootnoteBottomSheetDialogPage(
    data: FootnoteBottomDialogItem,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = data.image.get(),
            contentDescription = null,
            modifier = Modifier.size(320.dp),
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = data.title.get(),
            style = AppTheme.specificTypography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = data.text.get(),
            style = AppTheme.specificTypography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(24.dp))
        if (data.onClick != null && data.buttonText != null) {
            SimpleButtonActionL(
                onClick = data.onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                SimpleButtonContent(text = data.buttonText)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}