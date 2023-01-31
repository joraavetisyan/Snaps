package com.defince.coreuicompose.uikit.status

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.defince.corecommon.container.TextValue
import com.defince.coreuicompose.tools.get
import com.defince.coreuicompose.tools.inset
import com.defince.coreuicompose.tools.insetAllExcludeTop
import com.defince.coreuicompose.uikit.other.SheetIndicator
import com.defince.coreuitheme.compose.AppTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SimpleBottomDialogUI(
    header: TextValue,
    content: LazyListScope.() -> Unit,
) {
    Box(
        modifier = Modifier
            .background(AppTheme.specificColorScheme.white)
            .inset(insetAllExcludeTop()),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
        ) {
            stickyHeader {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 16.dp),
                ) {
                    SheetIndicator()
                    Text(
                        text = header.get(),
                        color = AppTheme.specificColorScheme.textPrimary,
                        style = AppTheme.specificTypography.headlineSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth(),
                    )
                }
            }
            content()
        }
    }
}