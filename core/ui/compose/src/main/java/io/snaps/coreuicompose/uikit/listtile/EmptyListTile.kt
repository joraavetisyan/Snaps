package io.snaps.coreuicompose.uikit.listtile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.snaps.corecommon.R
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.imageValue
import io.snaps.corecommon.container.textValue
import io.snaps.coreuicompose.tools.TileState
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuitheme.compose.AppTheme

data class EmptyListTileState(
    val title: TextValue,
    val message: TextValue? = null,
    val image: ImageValue? = null,
) : TileState {

    @Composable
    override fun Content(modifier: Modifier) {
        EmptyListTile(modifier, this)
    }

    companion object {

        fun defaultState(
            title: TextValue = "No data".textValue(), // todo localize
            message: TextValue? = null,
            image: ImageValue? = R.drawable.img_guy_confused.imageValue(),
        ) = EmptyListTileState(
            title = title,
            message = message,
            image = image,
        )
    }
}

@Composable
fun EmptyListTile(
    modifier: Modifier = Modifier,
    data: EmptyListTileState,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        data.image?.let {
            Image(
                painter = data.image.get(),
                modifier = Modifier.size(128.dp),
                contentDescription = null,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        Text(
            text = data.title.get(),
            style = AppTheme.specificTypography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        data.message?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = it.get(),
                style = AppTheme.specificTypography.bodySmall,
                color = AppTheme.specificColorScheme.textSecondary,
                textAlign = TextAlign.Center,
            )
        }
    }
}