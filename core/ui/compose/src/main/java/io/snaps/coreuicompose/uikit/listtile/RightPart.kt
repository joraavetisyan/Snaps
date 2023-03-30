package io.snaps.coreuicompose.uikit.listtile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.TextValue
import io.snaps.coreuicompose.tools.TileState
import io.snaps.coreuicompose.tools.addIf
import io.snaps.coreuicompose.tools.doOnClick
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionS
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.button.SimpleChip
import io.snaps.coreuicompose.uikit.other.ShimmerTileCircle
import io.snaps.coreuicompose.uikit.other.ShimmerTileConfig
import io.snaps.coreuicompose.uikit.other.ShimmerTileLine
import io.snaps.coreuitheme.compose.AppTheme

sealed class RightPart : TileState {

    data class ActionIcon(
        val source: ImageValue,
        val tint: Color? = null,
        val size: Dp? = null,
        val clickListener: (() -> Unit)? = null,
    ) : RightPart()

    data class DeleteIcon(
        val clickListener: () -> Unit,
    ) : RightPart()

    object CheckIcon : RightPart()

    data class NavigateNextIcon(val text: TextValue? = null) : RightPart()

    data class Logo(val source: ImageValue) : RightPart()

    data class Switch(val isChecked: Boolean) : RightPart()

    data class Text(val text: TextValue) : RightPart()

    data class TextMoney(
        val coin: String,
        val fiatCurrency: String,
    ) : RightPart()

    data class ButtonData(
        val text: TextValue,
        val enable: Boolean = true,
        val onClick: (() -> Unit)? = null,
    ) : RightPart()

    data class ChipData(
        val text: TextValue,
        val selected: Boolean = true,
        val onClick: () -> Unit,
    ) : RightPart()

    data class Shimmer(
        val needCircle: Boolean = false,
        val needLine: Boolean = false,
        val needBoldLine: Boolean = false,
        val needAdditionalInfo: Boolean = false,
    ) : RightPart()

    @Composable
    override fun Content(modifier: Modifier) {
        RightPartTile(modifier, this)
    }
}

object RightPartTileConfig {

    val IconSize = 40.dp
    val ActionIconSize = 32.dp
    val ActionLargeIconSize = 40.dp

    @Composable
    fun rightPartTextStyle() = AppTheme.specificTypography.bodyMedium

    @Composable
    fun rightPartAdditionalTextStyle() = AppTheme.specificTypography.headlineSmall
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RightPartTile(modifier: Modifier, data: RightPart) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.End,
    ) {
        when (data) {
            is RightPart.ActionIcon -> Icon(
                painter = data.source.get(),
                tint = data.tint ?: AppTheme.specificColorScheme.darkGrey,
                contentDescription = null,
                modifier = Modifier
                    .size(data.size ?: RightPartTileConfig.ActionIconSize)
                    .clip(AppTheme.shapes.medium)
                    .doOnClick(onClick = data.clickListener)
                    .padding(6.dp),
            )
            is RightPart.DeleteIcon -> Icon(
                painter = AppTheme.specificIcons.close.get(),
                tint = Color.Unspecified,
                contentDescription = null,
                modifier = Modifier
                    .size(RightPartTileConfig.ActionLargeIconSize)
                    .clip(AppTheme.shapes.medium)
                    .doOnClick(onClick = data.clickListener)
                    .padding(6.dp),
            )
            is RightPart.CheckIcon -> Icon(
                painter = AppTheme.specificIcons.done.get(),
                tint = AppTheme.specificColorScheme.uiAccent,
                contentDescription = null,
                modifier = Modifier
                    .size(RightPartTileConfig.ActionIconSize)
                    .padding(6.dp),
            )
            is RightPart.NavigateNextIcon -> Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                data.text?.let {
                    Text(
                        text = data.text.get(),
                        color = AppTheme.specificColorScheme.textSecondary,
                        style = AppTheme.specificTypography.bodySmall,
                        maxLines = 1,
                        textAlign = TextAlign.End,
                    )
                }
                Icon(
                    painter = AppTheme.specificIcons.navigateNext.get(),
                    tint = AppTheme.specificColorScheme.darkGrey,
                    contentDescription = null,
                    modifier = Modifier
                        .size(RightPartTileConfig.ActionIconSize)
                        .padding(6.dp),
                )
            }
            is RightPart.Logo -> Image(
                painter = data.source.get(),
                contentDescription = null,
                modifier = Modifier
                    .size(RightPartTileConfig.IconSize),
            )
            is RightPart.Switch -> Switch(
                checked = data.isChecked,
                onCheckedChange = null,
            )
            is RightPart.Text -> Text(
                text = data.text.get(),
                color = AppTheme.specificColorScheme.textSecondary,
                style = AppTheme.specificTypography.bodyMedium,
            )
            is RightPart.TextMoney -> {
                Text(
                    text = data.coin,
                    color = AppTheme.specificColorScheme.textPrimary,
                    style = AppTheme.specificTypography.bodySmall,
                    maxLines = 1,
                    textAlign = TextAlign.End,
                )
                Text(
                    text = "≈ ${data.fiatCurrency}",
                    color = AppTheme.specificColorScheme.textSecondary,
                    style = AppTheme.specificTypography.bodySmall,
                    maxLines = 1,
                    textAlign = TextAlign.End,
                )
            }
            is RightPart.ButtonData -> {
                SimpleButtonActionS(onClick = { data.onClick?.invoke() }, enabled = data.enable) {
                    SimpleButtonContent(data.text)
                }
            }
            is RightPart.ChipData -> {
                SimpleChip(
                    modifier = Modifier.width(100.dp),
                    contentPadding = PaddingValues(4.dp),
                    selected = data.selected,
                    label = data.text,
                    textStyle = AppTheme.specificTypography.labelSmall,
                    onClick = data.onClick,
                )
            }
            is RightPart.Shimmer -> {
                if (data.needCircle) {
                    ShimmerTileCircle(size = RightPartTileConfig.IconSize)
                }
                if (data.needLine) {
                    ShimmerTileLine(
                        width = ShimmerTileConfig.WidthExtraSmall,
                        height = RightPartTileConfig.rightPartTextStyle().lineHeight.value.dp,
                        modifier = Modifier.addIf(data.needCircle) { padding(top = 4.dp) }
                    )
                }
                if (data.needBoldLine) {
                    ShimmerTileLine(
                        width = ShimmerTileConfig.WidthExtraSmall,
                        height = RightPartTileConfig.rightPartTextStyle().lineHeight.value.dp.times(2),
                        modifier = Modifier.addIf(data.needCircle) { padding(top = 4.dp) }
                    )
                }
                if (data.needAdditionalInfo) {
                    ShimmerTileLine(
                        width = ShimmerTileConfig.WidthExtraSmall,
                        height = RightPartTileConfig.rightPartAdditionalTextStyle().lineHeight.value.dp,
                        modifier = Modifier.addIf(data.needCircle || data.needLine) {
                            padding(top = 4.dp)
                        }
                    )
                }
            }
        }
    }
}