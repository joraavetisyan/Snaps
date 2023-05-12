package io.snaps.coreuicompose.uikit.button

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SelectableChipBorder
import androidx.compose.material3.SelectableChipElevation
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.TextValue
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuitheme.compose.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
object SimpleChipConfig {

    @Composable
    fun defaultColor() = FilterChipDefaults.filterChipColors(
        containerColor = AppTheme.specificColorScheme.darkGrey,
        labelColor = AppTheme.specificColorScheme.white,
        selectedContainerColor = AppTheme.specificColorScheme.uiAccent,
        selectedLabelColor = AppTheme.specificColorScheme.white,
        selectedLeadingIconColor = AppTheme.specificColorScheme.white,
        selectedTrailingIconColor = AppTheme.specificColorScheme.white,
    )

    @Composable
    fun greyColor() = FilterChipDefaults.filterChipColors(
        containerColor = AppTheme.specificColorScheme.grey.copy(alpha = 0.32f),
        labelColor = AppTheme.specificColorScheme.white,
        selectedContainerColor = AppTheme.specificColorScheme.grey.copy(alpha = 0.16f),
        selectedLabelColor = AppTheme.specificColorScheme.white,
        selectedLeadingIconColor = AppTheme.specificColorScheme.white,
        selectedTrailingIconColor = AppTheme.specificColorScheme.white,
    )

    @Composable
    fun defaultElevation() = FilterChipDefaults.filterChipElevation(
        elevation = 0.dp,
        pressedElevation = 0.dp,
        focusedElevation = 0.dp,
        hoveredElevation = 0.dp,
        draggedElevation = 0.dp,
        disabledElevation = 0.dp,
    )

    @Composable
    fun defaultBorder() = FilterChipDefaults.filterChipBorder(
        borderColor = Color.Transparent,
        selectedBorderColor = Color.Transparent,
        disabledBorderColor = Color.Transparent,
        disabledSelectedBorderColor = Color.Transparent,
        borderWidth = 0.dp,
        selectedBorderWidth = 0.dp,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleChip(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: () -> Unit,
    label: TextValue,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    textStyle: TextStyle = AppTheme.specificTypography.bodySmall,
    enabled: Boolean = true,
    leadingIcon: ImageValue? = null,
    trailingIcon: ImageValue? = null,
    shape: Shape = CircleShape,
    colors: SelectableChipColors = SimpleChipConfig.defaultColor(),
    elevation: SelectableChipElevation = SimpleChipConfig.defaultElevation(),
    border: SelectableChipBorder = SimpleChipConfig.defaultBorder(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    FilterChip(
        modifier = modifier,
        onClick = onClick,
        shape = shape,
        selected = selected,
        colors = colors,
        border = border,
        interactionSource = interactionSource,
        leadingIcon = {
            leadingIcon?.let {
                Icon(
                    imageValue = it
                )
            }
        },
        trailingIcon = {
            trailingIcon?.let {
                Icon(imageValue = it)
            }
        },
        elevation = elevation,
        enabled = enabled,
        label = {
            Text(
                text = label.get(),
                style = textStyle,
                modifier = modifier.padding(contentPadding),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
        },
    )
}

@Composable
private fun Icon(
    modifier: Modifier = Modifier,
    imageValue: ImageValue,
) {
    Icon(
        painter = imageValue.get(),
        contentDescription = null,
        modifier = modifier.size(16.dp),
    )
}