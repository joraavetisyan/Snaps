package io.snaps.coreuicompose.uikit.input.formatter

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

private const val trueSpace = ' '

class OrdinalNumberFormatter(
    private val ordinalNumber: Int,
) : VisualTransformation {

    private val prefix = ordinalNumber.toString()
    private var lastValue = ""

    private val offsetMapping = object : OffsetMapping {

        override fun originalToTransformed(offset: Int): Int {
            var arg = 0
            var index = offset
            lastValue.forEach {
                if (index == 0) return@forEach
                if (it == trueSpace) arg += 1
                else index -= 1
            }
            return offset + arg
        }

        override fun transformedToOriginal(offset: Int): Int {
            var arg = 0
            var index = offset
            lastValue.forEach {
                if (index == 0) return@forEach
                if (it == trueSpace) arg += 1
                index -= 1
            }
            return offset - arg
        }
    }

    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(format(text.text), offsetMapping)
    }

    private fun format(text: String): AnnotatedString {
        return buildAnnotatedString {
            append(prefix)
            append(text)
        }
    }
}