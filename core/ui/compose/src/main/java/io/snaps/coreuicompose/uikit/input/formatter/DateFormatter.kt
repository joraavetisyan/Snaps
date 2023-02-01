package io.snaps.coreuicompose.uikit.input.formatter

import androidx.compose.ui.graphics.Color
import io.snaps.corecommon.container.textValue

class DateFormatter(
    format: String,
) : SimpleFormatter {

    private val pattern = format.map {
        when {
            it == '.' || it == '/' -> SimpleInputSymbol.Divider(it.toString())
            else -> SimpleInputSymbol.Inputted(it.toString())
        }
    }

    private val length = pattern.count { it is SimpleInputSymbol.Inputted }

    override fun placeholder() = pattern.joinToString("") { it.value }.textValue()

    override fun onValueChanged(action: (String) -> Unit) = { value: String ->
        action(value.filter { it.isDigit() }.take(length))
    }

    override fun visualTransformation(
        inputtedPartColor: Color,
        otherPartColor: Color,
    ) = SimpleVisualTransformation(
        pattern = pattern,
        inputtedPartColor = inputtedPartColor,
        otherPartColor = otherPartColor,
    )
}