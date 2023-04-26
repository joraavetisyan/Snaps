package io.snaps.coreuicompose.uikit.bottomsheetdialog

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.snaps.corecommon.container.TextValue
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAllExcludeTop
import io.snaps.coreuicompose.uikit.indicator.SheetIndicator
import io.snaps.coreuitheme.compose.AppTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SimpleBottomDialogUI(
    modifier: Modifier = Modifier,
    header: TextValue? = null,
    content: LazyListScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .background(AppTheme.specificColorScheme.white)
            .inset(insetAllExcludeTop()),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            stickyHeader {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 16.dp),
                ) {
                    SheetIndicator()
                    header?.let {
                        Text(
                            text = it.get(),
                            color = AppTheme.specificColorScheme.textPrimary,
                            style = AppTheme.specificTypography.headlineSmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .fillMaxWidth(),
                        )
                    }
                }
            }
            content()
        }
    }
}