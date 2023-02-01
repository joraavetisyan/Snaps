package com.defince.coreuicompose.uikit.other

import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import com.defince.corecommon.container.TextValue
import com.defince.coreuicompose.tools.get
import com.defince.coreuitheme.compose.AppTheme

data class LinkTextData(
    val text: TextValue,
    val clickListener: () -> Unit,
)

@Composable
fun LinkText(
    text: TextValue,
    linkTextData: List<LinkTextData>,
    modifier: Modifier = Modifier,
    style: TextStyle = AppTheme.specificTypography.bodySmall,
    color: Color = AppTheme.specificColorScheme.textSecondary,
) {
    val annotatedString = createAnnotatedString(text.get().text, linkTextData)

    ClickableText(
        text = annotatedString,
        style = style.copy(color = color),
        modifier = modifier,
        onClick = { offset ->
            linkTextData.forEach { item ->
                annotatedString
                    .getStringAnnotations(item.text.toString(), offset, offset)
                    .firstOrNull()
                    ?.let { item.clickListener() }
            }
        },
    )
}

@Composable
private fun createAnnotatedString(text: String, data: List<LinkTextData>): AnnotatedString {
    return buildAnnotatedString {
        append(text)
        data.forEach {
            val linkText = it.text.get().text
            val startIndex = text.indexOf(linkText)
            val endIndex = startIndex + linkText.length
            addStyle(
                style = SpanStyle(color = AppTheme.specificColorScheme.textLink, textDecoration = TextDecoration.Underline),
                start = startIndex,
                end = endIndex,
            )
            addStringAnnotation(
                tag = it.text.toString(),
                annotation = linkText,
                start = startIndex,
                end = endIndex
            )
        }
    }
}