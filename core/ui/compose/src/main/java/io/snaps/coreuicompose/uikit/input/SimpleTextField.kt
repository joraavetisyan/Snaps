package io.snaps.coreuicompose.uikit.input

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import io.snaps.coreuitheme.compose.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
object SimpleTextFieldConfig {

    val MinWidth = TextFieldDefaults.MinWidth
    val MinHeight = 48.dp

    @Composable
    fun shape() = CircleShape

    @Composable
    fun colors() = TextFieldDefaults.outlinedTextFieldColors(
        containerColor = AppTheme.specificColorScheme.darkGrey.copy(alpha = 0.15f),
        unfocusedBorderColor = AppTheme.specificColorScheme.darkGrey.copy(alpha = 0.15f),
        disabledBorderColor = AppTheme.specificColorScheme.darkGrey.copy(alpha = 0.15f),
        placeholderColor = AppTheme.specificColorScheme.darkGrey,
    )
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
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = SimpleTextFieldConfig.shape(),
    colors: TextFieldColors = SimpleTextFieldConfig.colors(),
) {
    // todo
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
//        label = label,
//        placeholder = placeholder,
//        leadingIcon = leadingIcon,
//        trailingIcon = trailingIcon,
//        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
//        keyboardActions = keyboardActions,
        maxLines = maxLines,
        interactionSource = interactionSource,
//        shape = shape,
//        colors = colors,
    )
}