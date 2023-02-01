package io.snaps.coreuicompose.uikit.listtile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.snaps.corecommon.container.TextValue
import io.snaps.coreuicompose.tools.TileState
import io.snaps.coreuicompose.tools.addIf
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.other.MultiLineFadeText
import io.snaps.coreuicompose.uikit.other.ShimmerTileConfig
import io.snaps.coreuicompose.uikit.other.ShimmerTileLine
import io.snaps.coreuitheme.compose.AppTheme

sealed class MiddlePart : TileState {

    data class Data(
        val additionalHeader: TextValue? = null,
        val header: TextValue? = null,
        val value: TextValue? = null,
        val valueColor: Color? = null,
        val valueDisabled: TextValue? = null,
        val maxValueLines: Int = Int.MAX_VALUE,
        val valueBold: TextValue? = null,
        val valueLarge: TextValue? = null,
        val description: TextValue? = null,
        val action: TextValue? = null,
        val actionColor: Color? = null,
        val additionalDescription: TextValue? = null,
        val additionalInfo: TextValue? = null,
    ) : MiddlePart()

    data class Shimmer(
        val needAdditionalHeaderLine: Boolean = false,
        val needHeaderLine: Boolean = false,
        val needValueLine: Boolean = false,
        val needValueBoldLine: Boolean = false,
        val needValueLargeLine: Boolean = false,
        val needDescriptionLine: Boolean = false,
        val needActionLine: Boolean = false,
        val needAdditionalDescriptionLine: Boolean = false,
        val needAdditionalInfo: Boolean = false,
    ) : MiddlePart()

    @Composable
    override fun Content(modifier: Modifier) {
        MiddlePartTile(modifier, this)
    }
}

object MiddlePartTileConfig {

    @Composable
    fun additionalHeaderStyle() = AppTheme.specificTypography.headlineSmall

    @Composable
    fun headerStyle() = AppTheme.specificTypography.bodyMedium

    @Composable
    fun valueStyle() = AppTheme.specificTypography.bodyLarge

    @Composable
    fun valueBoldStyle() = AppTheme.specificTypography.bodyLarge

    @Composable
    fun valueLargeStyle() = AppTheme.specificTypography.headlineLarge

    @Composable
    fun descriptionStyle() = AppTheme.specificTypography.bodyMedium

    @Composable
    fun actionStyle() = AppTheme.specificTypography.bodyMedium

    @Composable
    fun additionalDescriptionStyle() = AppTheme.specificTypography.bodyMedium
}

@Composable
fun MiddlePartTile(
    modifier: Modifier = Modifier,
    data: MiddlePart,
) {
    MiddlePartContainer(modifier = modifier) {
        when (data) {
            is MiddlePart.Data -> Data(data)
            is MiddlePart.Shimmer -> Shimmer(data)
        }
    }
}

@Composable
private fun Data(data: MiddlePart.Data) {
    data.additionalHeader?.get()?.let {
        Text(
            text = it,
            color = AppTheme.specificColorScheme.textSecondary,
            style = MiddlePartTileConfig.additionalHeaderStyle(),
            modifier = Modifier.fillMaxWidth(),
        )
    }
    data.header?.get()?.let {
        Text(
            text = it,
            color = AppTheme.specificColorScheme.textSecondary,
            style = MiddlePartTileConfig.headerStyle(),
            modifier = Modifier.fillMaxWidth(),
        )
    }
    data.value?.get()?.let {
        MultiLineFadeText(
            text = it,
            color = data.valueColor ?: AppTheme.specificColorScheme.textPrimary,
            style = MiddlePartTileConfig.valueStyle(),
            modifier = Modifier.fillMaxWidth(),
            maxLines = data.maxValueLines,
        )
    }
    data.valueDisabled?.get()?.let {
        Text(
            text = it,
            color = AppTheme.specificColorScheme.uiDisabledLabel,
            style = MiddlePartTileConfig.valueStyle(),
            modifier = Modifier.fillMaxWidth(),
        )
    }
    data.valueBold?.get()?.let {
        Text(
            text = it,
            color = AppTheme.specificColorScheme.textPrimary,
            style = MiddlePartTileConfig.valueBoldStyle(),
            modifier = Modifier.fillMaxWidth(),
        )
    }
    data.valueLarge?.get()?.let {
        Text(
            text = it,
            color = AppTheme.specificColorScheme.textPrimary,
            style = MiddlePartTileConfig.valueLargeStyle(),
            modifier = Modifier.fillMaxWidth(),
        )
    }
    data.description?.get()?.let {
        Text(
            text = it,
            color = AppTheme.specificColorScheme.textSecondary,
            style = MiddlePartTileConfig.descriptionStyle(),
            modifier = Modifier
                .fillMaxWidth()
                .addIf(data.valueBold != null || data.valueLarge != null) { padding(top = 4.dp) }
                .addIf(data.action == null && data.additionalDescription != null && data.additionalInfo != null) {
                    padding(bottom = 6.dp)
                },
        )
    }
    data.action?.get()?.let {
        Text(
            text = it,
            color = data.actionColor ?: AppTheme.specificColorScheme.textPrimary,
            style = MiddlePartTileConfig.actionStyle(),
            modifier = Modifier
                .fillMaxWidth()
                .addIf(data.valueBold != null || data.valueLarge != null || data.description != null) {
                    padding(top = 6.dp)
                }
                .addIf(data.additionalDescription != null && data.additionalInfo != null) {
                    padding(bottom = 6.dp)
                },
        )
    }
    data.additionalDescription?.get()?.let {
        Text(
            text = it,
            color = AppTheme.specificColorScheme.textSecondary,
            style = MiddlePartTileConfig.additionalDescriptionStyle(),
            modifier = Modifier
                .fillMaxWidth()
                .addIf(data.valueBold != null || data.valueLarge != null || data.description != null || data.action != null) {
                    padding(top = 6.dp)
                }
                .addIf(data.additionalInfo != null) { padding(bottom = 6.dp) },
        )
    }
    data.additionalInfo?.get()?.let {
        Text(
            text = it,
            color = AppTheme.specificColorScheme.textSecondary,
            style = MiddlePartTileConfig.additionalHeaderStyle(),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun Shimmer(data: MiddlePart.Shimmer) {
    if (data.needAdditionalHeaderLine) ShimmerTileLine(
        width = ShimmerTileConfig.WidthSmall,
        height = MiddlePartTileConfig.additionalHeaderStyle().lineHeight.value.dp,
    )
    if (data.needHeaderLine) ShimmerTileLine(
        width = ShimmerTileConfig.WidthSmall,
        height = MiddlePartTileConfig.headerStyle().lineHeight.value.dp,
    )
    if (data.needValueLine) ShimmerTileLine(
        width = ShimmerTileConfig.WidthLarge,
        height = MiddlePartTileConfig.valueStyle().lineHeight.value.dp,
    )
    if (data.needValueBoldLine) ShimmerTileLine(
        width = ShimmerTileConfig.WidthLarge,
        height = MiddlePartTileConfig.valueBoldStyle().lineHeight.value.dp,
    )
    if (data.needValueLargeLine) ShimmerTileLine(
        width = ShimmerTileConfig.WidthExtraLarge,
        height = MiddlePartTileConfig.valueLargeStyle().lineHeight.value.dp,
    )
    if (data.needDescriptionLine) ShimmerTileLine(
        width = ShimmerTileConfig.WidthMedium,
        height = MiddlePartTileConfig.descriptionStyle().lineHeight.value.dp,
        modifier = Modifier
            .padding(top = 4.dp)
            .addIf(data.needValueBoldLine || data.needValueLargeLine) { padding(top = 4.dp) }
            .addIf(!data.needActionLine && data.needAdditionalDescriptionLine && data.needAdditionalInfo) { padding(bottom = 6.dp) }
    )
    if (data.needActionLine) ShimmerTileLine(
        width = ShimmerTileConfig.WidthSmall,
        height = MiddlePartTileConfig.actionStyle().lineHeight.value.dp,
        modifier = Modifier
            .padding(top = 6.dp)
            .addIf(data.needValueBoldLine || data.needValueLargeLine || data.needDescriptionLine) {
                padding(top = 6.dp)
            }
            .addIf(data.needAdditionalDescriptionLine && data.needAdditionalInfo) { padding(bottom = 6.dp) }
    )
    if (data.needAdditionalDescriptionLine) ShimmerTileLine(
        width = ShimmerTileConfig.WidthMedium,
        height = MiddlePartTileConfig.additionalDescriptionStyle().lineHeight.value.dp,
        modifier = Modifier
            .padding(top = 6.dp)
            .addIf(data.needValueBoldLine || data.needValueLargeLine || data.needDescriptionLine || data.needActionLine) {
                padding(top = 6.dp)
            }
            .addIf(data.needAdditionalInfo) { padding(bottom = 6.dp) }
    )
    if (data.needAdditionalInfo) ShimmerTileLine(
        width = ShimmerTileConfig.WidthSmall,
        height = MiddlePartTileConfig.additionalHeaderStyle().lineHeight.value.dp,

    )
}

@Composable
fun MiddlePartContainer(
    modifier: Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier,
        content = content,
    )
}