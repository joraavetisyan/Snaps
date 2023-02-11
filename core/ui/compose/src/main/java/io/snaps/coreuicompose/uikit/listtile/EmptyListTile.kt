package io.snaps.coreuicompose.uikit.listtile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.TextValue
import io.snaps.coreuicompose.tools.TileState
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuitheme.compose.AppTheme

data class EmptyListTileState(
    val message: TextValue,
    val image: ImageValue,
    val modifier: Modifier = Modifier,
) : TileState {

    @Composable
    override fun Content(modifier: Modifier) {
        EmptyListTile(modifier, this)
    }
}

@Composable
fun EmptyListTile(
    modifier: Modifier = Modifier,
    data: EmptyListTileState,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = data.image.get(),
            modifier = Modifier.size(128.dp),
            contentDescription = null,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = data.message.get(),
            style = AppTheme.specificTypography.bodyMedium,
            color = AppTheme.specificColorScheme.textSecondary,
            textAlign = TextAlign.Center,
        )
    }
}