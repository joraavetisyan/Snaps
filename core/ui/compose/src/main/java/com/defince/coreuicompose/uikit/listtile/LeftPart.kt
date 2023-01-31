package com.defince.coreuicompose.uikit.listtile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.request.ImageRequest
import com.defince.corecommon.container.ImageValue
import com.defince.corecommon.container.TextValue
import com.defince.coreuicompose.tools.TileState
import com.defince.coreuicompose.tools.addIf
import com.defince.coreuicompose.tools.get
import com.defince.coreuicompose.uikit.other.ShimmerTileCircle
import com.defince.coreuitheme.compose.AppTheme

sealed class LeftPart : TileState {

    data class Icon(
        val icon: ImageValue,
        val size: Dp? = null,
        val tint: Color? = null,
        val background: Color? = null,
        val builder: ImageRequest.Builder.() -> Unit = {},
    ) : LeftPart()

    data class GreyIcon(
        val icon: ImageValue,
        val builder: ImageRequest.Builder.() -> Unit = {},
    ) : LeftPart()

    data class UiSystemGreenIcon(
        val icon: ImageValue,
        val builder: ImageRequest.Builder.() -> Unit = {},
    ) : LeftPart()

    data class GreyColoredIcon(
        val icon: ImageValue,
        val builder: ImageRequest.Builder.() -> Unit = {},
    ) : LeftPart()

    data class UiAccentColoredIcon(
        val icon: ImageValue,
        val isEnabled: Boolean = true,
        val builder: ImageRequest.Builder.() -> Unit = {},
    ) : LeftPart()

    data class UiBrandColoredIcon(
        val icon: ImageValue,
        val isEnabled: Boolean = true,
        val builder: ImageRequest.Builder.() -> Unit = {},
    ) : LeftPart()

    data class SmallLogo(
        val icon: ImageValue,
        val builder: ImageRequest.Builder.() -> Unit = {},
    ) : LeftPart()

    data class Logo(
        val source: ImageValue,
        val builder: ImageRequest.Builder.() -> Unit = {},
    ) : LeftPart()

    data class OrderNumber(val orderNumber: TextValue) : LeftPart()

    object Shimmer : LeftPart()

    @Composable
    override fun Content(modifier: Modifier) {
        LeftPartTile(modifier, this)
    }
}

object LeftPartTileConfig {

    val IconPadding = 10.dp

    @Composable
    fun disabledBackgroundColor() = AppTheme.specificColorScheme.lightDisabled

    @Composable
    fun disabledTintColor() = AppTheme.specificColorScheme.uiDisabledLabel
}

@Composable
fun LeftPartTile(modifier: Modifier, data: LeftPart) {
    when (data) {
        is LeftPart.Icon -> SimpleIcon(
            icon = data.icon,
            builder = data.builder,
            tint = data.tint,
            size = data.size,
            modifier = modifier.addIf(data.background != null) {
                background(
                    data.background!!,
                    CircleShape
                )
            },
        )
        is LeftPart.GreyIcon -> SimpleIcon(
            icon = data.icon,
            builder = data.builder,
            modifier = modifier.background(AppTheme.specificColorScheme.grey, CircleShape),
        )
        is LeftPart.UiSystemGreenIcon -> SimpleIcon(
            icon = data.icon,
            builder = data.builder,
            modifier = modifier.background(
                AppTheme.specificColorScheme.uiSystemGreen.copy(alpha = 0.16f), CircleShape
            ),
        )
        is LeftPart.GreyColoredIcon -> SimpleIcon(
            icon = data.icon,
            builder = data.builder,
            tint = AppTheme.specificColorScheme.grey,
            modifier = modifier.background(AppTheme.specificColorScheme.grey, CircleShape),
        )
        is LeftPart.UiAccentColoredIcon -> SimpleIcon(
            icon = data.icon,
            builder = data.builder,
            tint = if (data.isEnabled) {
                AppTheme.specificColorScheme.uiAccent
            } else LeftPartTileConfig.disabledTintColor(),
            modifier = modifier.background(
                color = if (data.isEnabled) {
                    AppTheme.specificColorScheme.uiAccent.copy(alpha = 0.16f)
                } else LeftPartTileConfig.disabledBackgroundColor(),
                shape = CircleShape,
            ),
        )
        is LeftPart.UiBrandColoredIcon -> SimpleIcon(
            icon = data.icon,
            builder = data.builder,
            tint = if (data.isEnabled) {
                AppTheme.specificColorScheme.uiAccent
            } else LeftPartTileConfig.disabledTintColor(),
            modifier = modifier.background(
                color = if (data.isEnabled) {
                    AppTheme.specificColorScheme.uiAccent.copy(alpha = 0.16f)
                } else LeftPartTileConfig.disabledBackgroundColor(),
                shape = CircleShape,
            ),
        )
        is LeftPart.SmallLogo -> Image(
            painter = data.icon.get(data.builder),
            contentDescription = null,
            modifier = modifier.size(CellTileConfig.SmallIconSize),
        )
        is LeftPart.Logo -> Image(
            painter = data.source.get(data.builder),
            contentDescription = null,
            modifier = modifier.size(CellTileConfig.IconSize),
        )
        is LeftPart.OrderNumber -> Text(
            text = data.orderNumber.get(),
            style = AppTheme.specificTypography.bodyLarge,
            color = AppTheme.specificColorScheme.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .background(
                    color = AppTheme.specificColorScheme.defaultBase,
                    shape = CircleShape,
                )
                .size(24.dp),
        )
        is LeftPart.Shimmer -> ShimmerTileCircle(size = CellTileConfig.IconSize)
    }
}

@Composable
private fun SimpleIcon(
    modifier: Modifier = Modifier,
    icon: ImageValue,
    builder: ImageRequest.Builder.() -> Unit,
    tint: Color? = null,
    size: Dp? = null,
) {
    Icon(
        painter = icon.get(builder),
        tint = tint ?: Color.Unspecified,
        contentDescription = null,
        modifier = Modifier
            .size(size ?: CellTileConfig.IconSize)
            .then(modifier)
            .padding(LeftPartTileConfig.IconPadding),
    )
}