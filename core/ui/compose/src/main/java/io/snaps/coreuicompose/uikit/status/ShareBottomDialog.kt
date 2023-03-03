package io.snaps.coreuicompose.uikit.status

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import io.snaps.corecommon.container.TextValue
import io.snaps.coreuicompose.tools.defaultTileRipple
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAllExcludeTop
import io.snaps.coreuicompose.uikit.other.SheetIndicator
import io.snaps.coreuitheme.compose.AppTheme

data class ShareBottomDialogItem(
    val name: String,
    val icon: Drawable,
    val clickListener: () -> Unit,
)

@Composable
fun ShareBottomDialog(
    header: TextValue,
    items: List<ShareBottomDialogItem>,
) {
    Box(
        modifier = Modifier
            .background(AppTheme.specificColorScheme.white)
            .inset(insetAllExcludeTop()),
    ) {
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.Center,
        ) {
            item(
                span = { GridItemSpan(this.maxLineSpan) },
            ) {
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
            items(items) {
                Item(item = it)
            }
        }
    }
}

@Composable
private fun Item(
    item: ShareBottomDialogItem,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            bitmap = item.icon.toBitmap().asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .defaultTileRipple(shape = CircleShape, onClick = item.clickListener),
        )
        Text(
            text = item.name,
            style = AppTheme.specificTypography.titleSmall,
        )
    }
}