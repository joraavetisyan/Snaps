package io.snaps.coreuicompose.uikit.input

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.snaps.coreuitheme.compose.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
object SimpleTextFieldConfig {

    val MinWidth = TextFieldDefaults.MinWidth
    val MinHeight = 48.dp

    @Composable
    fun shape() = CircleShape

    @Composable
    fun defaultColors(borderAndCursorColor: Color? = null) = SimpleTextFieldColors(
        textColor = AppTheme.specificColorScheme.textPrimary,
        disabledTextColor = AppTheme.specificColorScheme.uiDisabledLabel,

        placeholderColor = AppTheme.specificColorScheme.darkGrey,
        disabledPlaceholderColor = AppTheme.specificColorScheme.uiDisabledLabel,

        containerColor = AppTheme.specificColorScheme.darkGrey.copy(alpha = 0.15f),

        cursorColor = borderAndCursorColor ?: AppTheme.specificColorScheme.uiAccent,
        errorCursorColor = AppTheme.specificColorScheme.uiSystemRed,

        focusedIndicatorColor = borderAndCursorColor ?: AppTheme.specificColorScheme.uiAccent,
        unfocusedIndicatorColor = borderAndCursorColor ?: Color.Transparent,
        errorIndicatorColor = AppTheme.specificColorScheme.uiSystemRed,
        disabledIndicatorColor = Color.Transparent,

        focusedLeadingIconColor = AppTheme.specificColorScheme.darkGrey,
        unfocusedLeadingIconColor = AppTheme.specificColorScheme.grey,
        disabledLeadingIconColor = AppTheme.specificColorScheme.uiDisabledLabel,
        errorLeadingIconColor = AppTheme.specificColorScheme.grey,

        textSelectionColors = LocalTextSelectionColors.current,

        focusedTrailingIconColor = AppTheme.specificColorScheme.darkGrey,
        unfocusedTrailingIconColor = AppTheme.specificColorScheme.grey,
        disabledTrailingIconColor = AppTheme.specificColorScheme.uiDisabledLabel,
        errorTrailingIconColor = AppTheme.specificColorScheme.grey,

        focusedLabelColor = AppTheme.specificColorScheme.uiAccent,
        unfocusedLabelColor = AppTheme.specificColorScheme.textPrimary,
        disabledLabelColor = AppTheme.specificColorScheme.uiDisabledLabel,
        errorLabelColor = AppTheme.specificColorScheme.uiSystemRed,
    )

    @Composable
    fun successColors() =
        defaultColors(borderAndCursorColor = AppTheme.specificColorScheme.uiSystemGreen)

    @Composable
    fun errorColors() =
        defaultColors(borderAndCursorColor = AppTheme.specificColorScheme.uiSystemRed)
}

enum class SimpleTextFieldStatus {

    Normal, Error, Success;

    fun isError() = this == Error
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = AppTheme.specificTypography.titleSmall,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = SimpleTextFieldConfig.shape(),
    status: SimpleTextFieldStatus = SimpleTextFieldStatus.Normal,
    textAlign: TextAlign? = null,
) {
    val colors = when (status) {
        SimpleTextFieldStatus.Normal -> SimpleTextFieldConfig.defaultColors()
        SimpleTextFieldStatus.Error -> SimpleTextFieldConfig.errorColors()
        SimpleTextFieldStatus.Success -> SimpleTextFieldConfig.successColors()
    }

    BasicTextField(
        value = value,
        modifier = modifier.defaultMinSize(minHeight = SimpleTextFieldConfig.MinHeight),
        onValueChange = onValueChange,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textAlign?.let { textStyle.copy(textAlign = it) } ?: textStyle,
        cursorBrush = SolidColor(colors.cursorColor(status.isError()).value),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
        singleLine = maxLines == 1,
        maxLines = maxLines,
        decorationBox = @Composable { innerTextField ->
            TextFieldDefaults.OutlinedTextFieldDecorationBox(
                value = value,
                innerTextField = innerTextField,
                enabled = enabled,
                singleLine = maxLines == 1,
                visualTransformation = visualTransformation,
                interactionSource = interactionSource,
                colors = colors.toLibColors(),
                isError = status.isError(),
                trailingIcon = trailingIcon,
                leadingIcon = leadingIcon,
                label = label,
                placeholder = placeholder,
                container = {
                    TextFieldDefaults.OutlinedBorderContainerBox(
                        enabled = enabled,
                        isError = status.isError(),
                        interactionSource = interactionSource,
                        colors = colors.toLibColors(),
                        shape = shape,
                    )
                },
            )
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = AppTheme.specificTypography.titleSmall,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = SimpleTextFieldConfig.shape(),
    status: SimpleTextFieldStatus = SimpleTextFieldStatus.Normal,
    textAlign: TextAlign? = null,
) {
    val colors = when (status) {
        SimpleTextFieldStatus.Normal -> SimpleTextFieldConfig.defaultColors()
        SimpleTextFieldStatus.Error -> SimpleTextFieldConfig.errorColors()
        SimpleTextFieldStatus.Success -> SimpleTextFieldConfig.successColors()
    }

    BasicTextField(
        value = value,
        modifier = modifier.defaultMinSize(minHeight = SimpleTextFieldConfig.MinHeight),
        onValueChange = onValueChange,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textAlign?.let { textStyle.copy(textAlign = it) } ?: textStyle,
        cursorBrush = SolidColor(colors.cursorColor(status.isError()).value),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
        singleLine = maxLines == 1,
        maxLines = maxLines,
        decorationBox = @Composable { innerTextField ->
            TextFieldDefaults.OutlinedTextFieldDecorationBox(
                value = value.text,
                innerTextField = innerTextField,
                enabled = enabled,
                singleLine = maxLines == 1,
                visualTransformation = visualTransformation,
                interactionSource = interactionSource,
                colors = colors.toLibColors(),
                isError = status.isError(),
                trailingIcon = trailingIcon,
                leadingIcon = leadingIcon,
                label = label,
                placeholder = placeholder,
                container = {
                    TextFieldDefaults.OutlinedBorderContainerBox(
                        enabled = enabled,
                        isError = status.isError(),
                        interactionSource = interactionSource,
                        colors = colors.toLibColors(),
                        shape = shape,
                    )
                },
            )
        },
    )
}