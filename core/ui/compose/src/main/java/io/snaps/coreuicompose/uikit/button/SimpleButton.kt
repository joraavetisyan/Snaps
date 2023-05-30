package io.snaps.coreuicompose.uikit.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.snaps.corecommon.container.textValue
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.PreviewAppTheme

@Composable
fun SimpleButtonActionS(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit,
) {
    SimpleButton(
        modifier = modifier,
        size = SimpleButtonSize.s(),
        onClick = onClick,
        onLongClick = onLongClick,
        enabled = enabled,
        interactionSource = interactionSource,
        colors = SimpleButtonColors.action(),
        content = content,
    )
}

@Composable
fun SimpleButtonActionM(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit,
) {
    SimpleButton(
        modifier = modifier,
        size = SimpleButtonSize.m(),
        onClick = onClick,
        onLongClick = onLongClick,
        enabled = enabled,
        interactionSource = interactionSource,
        colors = SimpleButtonColors.action(),
        content = content,
    )
}

@Composable
fun SimpleButtonActionL(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit,
) {
    SimpleButton(
        modifier = modifier,
        size = SimpleButtonSize.l(),
        onClick = onClick,
        onLongClick = onLongClick,
        enabled = enabled,
        interactionSource = interactionSource,
        colors = SimpleButtonColors.action(),
        content = content,
    )
}

@Composable
fun SimpleButtonLightS(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit,
) {
    SimpleButton(
        modifier = modifier,
        size = SimpleButtonSize.s(),
        onClick = onClick,
        onLongClick = onLongClick,
        enabled = enabled,
        interactionSource = interactionSource,
        colors = SimpleButtonColors.light(),
        content = content,
    )
}

@Composable
fun SimpleButtonLightM(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit,
) {
    SimpleButton(
        modifier = modifier,
        size = SimpleButtonSize.m(),
        onClick = onClick,
        onLongClick = onLongClick,
        enabled = enabled,
        interactionSource = interactionSource,
        colors = SimpleButtonColors.light(),
        content = content,
    )
}

@Composable
fun SimpleButtonLightL(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit,
) {
    SimpleButton(
        modifier = modifier,
        size = SimpleButtonSize.l(),
        onClick = onClick,
        onLongClick = onLongClick,
        enabled = enabled,
        interactionSource = interactionSource,
        colors = SimpleButtonColors.light(),
        content = content,
    )
}

@Composable
fun SimpleButtonDefaultS(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit,
) {
    SimpleButton(
        modifier = modifier,
        size = SimpleButtonSize.s(),
        onClick = onClick,
        onLongClick = onLongClick,
        enabled = enabled,
        interactionSource = interactionSource,
        colors = SimpleButtonColors.default(),
        content = content,
    )
}

@Composable
fun SimpleButtonDefaultM(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit,
) {
    SimpleButton(
        modifier = modifier,
        size = SimpleButtonSize.m(),
        onClick = onClick,
        onLongClick = onLongClick,
        enabled = enabled,
        interactionSource = interactionSource,
        colors = SimpleButtonColors.default(),
        content = content,
    )
}

@Composable
fun SimpleButtonDefaultL(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit,
) {
    SimpleButton(
        modifier = modifier,
        size = SimpleButtonSize.l(),
        onClick = onClick,
        onLongClick = onLongClick,
        enabled = enabled,
        interactionSource = interactionSource,
        colors = SimpleButtonColors.default(),
        content = content,
    )
}

@Composable
fun SimpleButtonOutlineS(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit,
) {
    SimpleButton(
        modifier = modifier,
        size = SimpleButtonSize.s(),
        onClick = onClick,
        onLongClick = onLongClick,
        enabled = enabled,
        interactionSource = interactionSource,
        colors = SimpleButtonColors.outline(),
        border = if (enabled) SimpleButtonConfig.borderOutline() else null,
        content = content,
    )
}

@Composable
fun SimpleButtonOutlineM(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit,
) {
    SimpleButton(
        modifier = modifier,
        size = SimpleButtonSize.m(),
        onClick = onClick,
        onLongClick = onLongClick,
        enabled = enabled,
        interactionSource = interactionSource,
        colors = SimpleButtonColors.outline(),
        border = if (enabled) SimpleButtonConfig.borderOutline() else null,
        content = content,
    )
}

@Composable
fun SimpleButtonOutlineL(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit,
) {
    SimpleButton(
        modifier = modifier,
        size = SimpleButtonSize.l(),
        onClick = onClick,
        onLongClick = onLongClick,
        enabled = enabled,
        interactionSource = interactionSource,
        colors = SimpleButtonColors.outline(),
        border = if (enabled) SimpleButtonConfig.borderOutline() else null,
        content = content,
    )
}

@Composable
fun SimpleButtonInlineS(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit,
) {
    SimpleButton(
        modifier = modifier,
        size = SimpleButtonSize.s(),
        onClick = onClick,
        onLongClick = onLongClick,
        enabled = enabled,
        interactionSource = interactionSource,
        colors = SimpleButtonColors.inline(),
        content = content,
    )
}

@Composable
fun SimpleButtonInlineM(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit,
) {
    SimpleButton(
        modifier = modifier,
        size = SimpleButtonSize.m(),
        onClick = onClick,
        onLongClick = onLongClick,
        enabled = enabled,
        interactionSource = interactionSource,
        colors = SimpleButtonColors.inline(),
        content = content,
    )
}

@Composable
fun SimpleButtonInlineL(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit,
) {
    SimpleButton(
        modifier = modifier,
        size = SimpleButtonSize.l(),
        onClick = onClick,
        onLongClick = onLongClick,
        enabled = enabled,
        interactionSource = interactionSource,
        colors = SimpleButtonColors.inline(),
        content = content,
    )
}

@Composable
fun SimpleButtonGreyS(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit,
) {
    SimpleButton(
        modifier = modifier,
        size = SimpleButtonSize.s(),
        onClick = onClick,
        onLongClick = onLongClick,
        enabled = enabled,
        interactionSource = interactionSource,
        colors = SimpleButtonColors.grey(),
        content = content,
    )
}

@Composable
fun SimpleButtonGreyM(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit,
) {
    SimpleButton(
        modifier = modifier,
        size = SimpleButtonSize.m(),
        onClick = onClick,
        onLongClick = onLongClick,
        enabled = enabled,
        interactionSource = interactionSource,
        colors = SimpleButtonColors.grey(),
        content = content,
    )
}

@Composable
fun SimpleButtonGreyL(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit,
) {
    SimpleButton(
        modifier = modifier,
        size = SimpleButtonSize.l(),
        onClick = onClick,
        onLongClick = onLongClick,
        enabled = enabled,
        interactionSource = interactionSource,
        colors = SimpleButtonColors.grey(),
        content = content,
    )
}

@Composable
fun SimpleButtonRedActionS(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit,
) {
    SimpleButton(
        modifier = modifier,
        size = SimpleButtonSize.s(),
        onClick = onClick,
        onLongClick = onLongClick,
        enabled = enabled,
        interactionSource = interactionSource,
        colors = SimpleButtonColors.redAction(),
        content = content,
    )
}

@Composable
fun SimpleButtonRedInlineM(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit,
) {
    SimpleButton(
        modifier = modifier,
        size = SimpleButtonSize.m(),
        onClick = onClick,
        onLongClick = onLongClick,
        enabled = enabled,
        interactionSource = interactionSource,
        colors = SimpleButtonColors.redInline(),
        content = content,
    )
}

object SimpleButtonConfig {

    @Composable
    fun borderOutline() =
        BorderStroke(width = 1.dp, color = AppTheme.specificColorScheme.outlineBorderBase)
}

data class SimpleButtonSize(
    val heightModifier: Modifier,
    val contentPadding: PaddingValues,
    val shape: Shape,
    val textStyle: TextStyle,
) {

    companion object {

        @Composable
        fun s() = SimpleButtonSize(
            heightModifier = Modifier.sizeIn(minWidth = 80.dp, minHeight = 24.dp),
            contentPadding = PaddingValues(8.dp),
            shape = CircleShape,
            textStyle = AppTheme.specificTypography.bodySmall,
        )

        @Composable
        fun m() = SimpleButtonSize(
            heightModifier = Modifier.sizeIn(minWidth = 44.dp, minHeight = 44.dp),
            contentPadding = PaddingValues(13.dp),
            shape = CircleShape,
            textStyle = AppTheme.specificTypography.titleSmall,
        )

        @Composable
        fun l() = SimpleButtonSize(
            heightModifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp),
            contentPadding = PaddingValues(17.dp),
            shape = CircleShape,
            textStyle = AppTheme.specificTypography.titleSmall,
        )
    }
}

@Immutable
data class SimpleButtonColors(
    private val containerColor: Color,
    private val contentColor: Color,
    private val disabledContainerColor: Color,
    private val disabledContentColor: Color,
) {

    companion object {

        @Composable
        fun default() = SimpleButtonColors(
            containerColor = AppTheme.specificColorScheme.defaultBase,
            contentColor = AppTheme.specificColorScheme.defaultLabel,
            disabledContainerColor = AppTheme.specificColorScheme.defaultDisabled,
            disabledContentColor = AppTheme.specificColorScheme.uiDisabledLabel,
        )

        @Composable
        fun action() = SimpleButtonColors(
            containerColor = AppTheme.specificColorScheme.actionBase,
            contentColor = AppTheme.specificColorScheme.actionLabel,
            disabledContainerColor = AppTheme.specificColorScheme.actionDisabled,
            disabledContentColor = AppTheme.specificColorScheme.actionLabel,
        )

        @Composable
        fun light() = SimpleButtonColors(
            containerColor = AppTheme.specificColorScheme.lightBase,
            contentColor = AppTheme.specificColorScheme.lightLabel,
            disabledContainerColor = AppTheme.specificColorScheme.lightDisabled,
            disabledContentColor = AppTheme.specificColorScheme.uiDisabledLabel,
        )

        @Composable
        fun outline() = SimpleButtonColors(
            containerColor = Color.Transparent,
            contentColor = AppTheme.specificColorScheme.outlineLabel,
            disabledContainerColor = AppTheme.specificColorScheme.outlineDisabled,
            disabledContentColor = AppTheme.specificColorScheme.uiDisabledLabel,
        )

        @Composable
        fun inline() = SimpleButtonColors(
            containerColor = Color.Transparent,
            contentColor = AppTheme.specificColorScheme.actionBase,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = AppTheme.specificColorScheme.actionDisabled,
        )

        @Composable
        fun grey() = SimpleButtonColors(
            containerColor = AppTheme.specificColorScheme.lightGrey,
            contentColor = AppTheme.specificColorScheme.darkGrey,
            disabledContainerColor = AppTheme.specificColorScheme.defaultDisabled,
            disabledContentColor = AppTheme.specificColorScheme.uiDisabledLabel,
        )

        @Composable
        fun redAction() = SimpleButtonColors(
            containerColor = AppTheme.specificColorScheme.uiSystemRed,
            contentColor = AppTheme.specificColorScheme.actionLabel,
            disabledContainerColor = AppTheme.specificColorScheme.uiSystemRed.copy(alpha = 0.15f),
            disabledContentColor = AppTheme.specificColorScheme.actionLabel,
        )

        @Composable
        fun redInline() = SimpleButtonColors(
            containerColor = Color.Transparent,
            contentColor = AppTheme.specificColorScheme.uiSystemRed,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = AppTheme.specificColorScheme.uiSystemRed.copy(alpha = 0.15f),
        )
    }

    @Composable
    fun containerColor(enabled: Boolean): State<Color> =
        rememberUpdatedState(if (enabled) containerColor else disabledContainerColor)

    @Composable
    fun contentColor(enabled: Boolean): State<Color> =
        rememberUpdatedState(if (enabled) contentColor else disabledContentColor)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
internal fun SimpleButton(
    modifier: Modifier,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    size: SimpleButtonSize,
    colors: SimpleButtonColors,
    enabled: Boolean,
    interactionSource: MutableInteractionSource,
    border: BorderStroke? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val containerColor = colors.containerColor(enabled).value
    val contentColor = colors.contentColor(enabled).value
    val shadowElevation = /*elevation?.shadowElevation(enabled, interactionSource)?.value ?: */0.dp
    val tonalElevation = /*elevation?.tonalElevation(enabled, interactionSource)?.value ?: */0.dp

    MaterialTheme(typography = MaterialTheme.typography.copy(labelLarge = size.textStyle)) {
        CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
            Surface(
                modifier = modifier
                    .then(size.heightModifier)
                    .clip(size.shape)
                    .combinedClickable(
                        onClick = onClick,
                        onLongClick = onLongClick,
                        enabled = enabled,
                        role = Role.Button,
                        interactionSource = interactionSource,
                        indication = LocalIndication.current,
                    ),
                shape = size.shape,
                color = containerColor,
                contentColor = contentColor,
                tonalElevation = tonalElevation,
                shadowElevation = shadowElevation,
                border = border,
            ) {
                CompositionLocalProvider(LocalContentColor provides contentColor) {
                    ProvideTextStyle(value = MaterialTheme.typography.labelLarge) {
                        Row(
                            modifier = Modifier.padding(size.contentPadding),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            content = content,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    PreviewAppTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SimpleButtonActionS(onClick = { }) { SimpleButtonContent(text = "SimpleButtonActionS".textValue()) }
            SimpleButtonActionM(onClick = { }) { SimpleButtonContent(text = "SimpleButtonActionM".textValue()) }
            SimpleButtonActionL(onClick = { }) { SimpleButtonContent(text = "SimpleButtonActionL".textValue()) }
            SimpleButtonLightS(onClick = { }) { SimpleButtonContent(text = "SimpleButtonLightS".textValue()) }
            SimpleButtonLightM(onClick = { }) { SimpleButtonContent(text = "SimpleButtonLightM".textValue()) }
            SimpleButtonLightL(onClick = { }) { SimpleButtonContent(text = "SimpleButtonLightL".textValue()) }
            SimpleButtonDefaultS(onClick = { }) { SimpleButtonContent(text = "SimpleButtonDefaultS".textValue()) }
            SimpleButtonDefaultM(onClick = { }) { SimpleButtonContent(text = "SimpleButtonDefaultM".textValue()) }
            SimpleButtonDefaultL(onClick = { }) { SimpleButtonContent(text = "SimpleButtonDefaultL".textValue()) }
            SimpleButtonOutlineS(onClick = { }) { SimpleButtonContent(text = "SimpleButtonOutlineS".textValue()) }
            SimpleButtonOutlineM(onClick = { }) { SimpleButtonContent(text = "SimpleButtonOutlineM".textValue()) }
            SimpleButtonOutlineL(onClick = { }) { SimpleButtonContent(text = "SimpleButtonOutlineL".textValue()) }
            SimpleButtonInlineS(onClick = { }) { SimpleButtonContent(text = "SimpleButtonInlineS".textValue()) }
            SimpleButtonInlineM(onClick = { }) { SimpleButtonContent(text = "SimpleButtonInlineM".textValue()) }
            SimpleButtonInlineL(onClick = { }) { SimpleButtonContent(text = "SimpleButtonInlineL".textValue()) }
            SimpleButtonGreyS(onClick = { }) { SimpleButtonContent(text = "SimpleButtonGreyS".textValue()) }
            SimpleButtonGreyM(onClick = { }) { SimpleButtonContent(text = "SimpleButtonGreyM".textValue()) }
            SimpleButtonGreyL(onClick = { }) { SimpleButtonContent(text = "SimpleButtonGreyL".textValue()) }
        }
    }
}