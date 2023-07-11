package io.snaps.coreuicompose.uikit.listtile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
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
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.tools.TileState
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionM
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.button.ProfileRoundedCornerChip
import io.snaps.coreuitheme.compose.AppTheme

data class EmptyListTileState(
    val title: TextValue,
    val message: TextValue? = null,
    val image: ImageValue? = null,
    val buttonData: ButtonData? = null,
) : TileState {

    data class ButtonData(
        val text: TextValue,
        val onClick: () -> Unit,
    )

    @Composable
    override fun Content(modifier: Modifier) {
        EmptyListTile(modifier, this)
    }

    companion object {

        fun defaultState(
            title: TextValue = StringKey.MessageNoData.textValue(),
            message: TextValue? = null,
            image: ImageValue? = R.drawable.img_guy_confused.imageValue(),
        ) = EmptyListTileState(
            title = title,
            message = message,
            image = image,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmptyListTile(
    modifier: Modifier = Modifier,
    data: EmptyListTileState,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Spacer(modifier = Modifier.height(16.dp))
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
                color = AppTheme.specificColorScheme.black,
                textAlign = TextAlign.Center,
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        ProfileRoundedCornerChip(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            selected = true,
            label = StringKey.ProfileTitleAddVideo.textValue(),
            textStyle = AppTheme.specificTypography.titleSmall,
            contentPadding = PaddingValues(10.dp),
            onClick = {},
        )
        data.buttonData?.let {
            Spacer(modifier = Modifier.height(4.dp))
            SimpleButtonActionM(
                onClick = it.onClick,
            ) {
                SimpleButtonContent(text = it.text)
            }
        }
    }
}