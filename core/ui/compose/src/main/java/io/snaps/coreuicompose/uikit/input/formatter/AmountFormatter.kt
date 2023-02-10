package io.snaps.coreuicompose.uikit.input.formatter

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.CurrencyType
import io.snaps.corecommon.strings.RU_LOCALE
import java.text.NumberFormat

private const val numberFormatterSpace = ' '
private const val trueSpace = ' '
private const val fractionalPartMaxLength = 2

const val amountDivider = ','

class AmountFormatter(
    private val currencyType: CurrencyType,
    private val maxLength: Int = 10,
) : SimpleFormatter {

    private val formatter = NumberFormat.getNumberInstance(RU_LOCALE)

    override fun placeholder(): TextValue = "".textValue()

    override fun onValueChanged(action: (String) -> Unit) = { value: String ->
        val transformedValue = value
            .take(maxLength)
            .replace('.', amountDivider)
            .removeUnwantedChars()
            .removeExtraDividers()
            .addLeadingZeroIfStartsWithDivider()
            .removeLeadingZeroForNonZeroIntegers()
            .restrictToOneLeadingZero()
            .restrictFractionalPart()
        action(transformedValue)
    }

    private fun String.removeUnwantedChars(): String = filter {
        it.isDigit() || it == amountDivider
    }

    private fun String.removeExtraDividers(): String {
        var isFound = false
        return filter {
            if (it == amountDivider) {
                return@filter !isFound.also { isFound = true }
            }
            true
        }
    }

    private fun String.addLeadingZeroIfStartsWithDivider() =
        if (startsWith(amountDivider)) "0$this"
        else this

    private fun String.removeLeadingZeroForNonZeroIntegers() =
        if (startsWith("0") && length > 1 && this[1] != amountDivider) removePrefix("0")
        else this

    private fun String.restrictToOneLeadingZero(): String {
        var edited = this
        while (edited.startsWith("00")) {
            edited = edited.removePrefix("0")
        }
        return edited
    }

    private fun String.restrictFractionalPart(): String {
        val indexOfDivider = indexOf(amountDivider)
        if (indexOfDivider != -1) {
            val indexOfStrip = indexOfDivider + fractionalPartMaxLength
            if (indexOfStrip < length - 1) {
                return substring(0, indexOfStrip + 1)
            }
        }
        return this
    }

    override fun visualTransformation(
        inputtedPartColor: Color,
        otherPartColor: Color,
    ) = object : VisualTransformation {

        private val suffix = " ${currencyType.symbol}"
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
            lastValue = formatForLocale(text)
            return buildAnnotatedString {
                append(lastValue)
                append(suffix)
                addStyle(SpanStyle(color = inputtedPartColor), 0, length - suffix.length)
                addStyle(SpanStyle(color = otherPartColor), length - suffix.length, length)
            }
        }

        private fun formatForLocale(value: String): String {
            return if (value.isEmpty()) {
                "0"
            } else {
                val parts = value.split(amountDivider)
                val wholePart = parts[0]
                val fractionalPart = parts.getOrNull(1)
                val parsedWholePart = formatter.parse(wholePart)
                formatter.format(parsedWholePart) +
                        if (fractionalPart != null) "$amountDivider$fractionalPart" else ""
            }.replace(numberFormatterSpace, trueSpace)
        }
    }
}