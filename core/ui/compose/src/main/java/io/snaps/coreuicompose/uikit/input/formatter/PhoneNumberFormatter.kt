package io.snaps.coreuicompose.uikit.input.formatter

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import io.snaps.corecommon.container.textValue

class PhoneNumberFormatter(format: String) : SimpleFormatter {

    private val pattern = format.map {
        when (it) {
            PatternSymbol -> SimpleInputSymbol.Inputted("0")
            else -> SimpleInputSymbol.Divider(it.toString())
        }
    }

    val length = pattern.count { it is SimpleInputSymbol.Inputted }

    override fun placeholder() = pattern.joinToString("") { it.value }.textValue()

    override fun onValueChanged(action: (String) -> Unit) = { value: String ->
        action(value.filter { it.isDigit() })
    }

    override fun visualTransformation(
        inputtedPartColor: Color,
        otherPartColor: Color
    ): VisualTransformation = SimpleVisualTransformation(
        pattern = pattern,
        inputtedPartColor = inputtedPartColor,
        otherPartColor = otherPartColor,
    )
}