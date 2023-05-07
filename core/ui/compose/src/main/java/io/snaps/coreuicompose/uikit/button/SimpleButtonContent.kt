package io.snaps.coreuicompose.uikit.button

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.snaps.corecommon.container.IconValue
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.textValue
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.PreviewAppTheme

@Composable
fun SimpleButtonContent(
    text: TextValue?,
    textColor: Color = LocalContentColor.current,
    iconLeft: IconValue? = null,
    iconRight: IconValue? = null,
    iconTint: Color = Color.Unspecified,
) {
    SimpleButtonContent(
        text = text,
        textColor = textColor,
        contentLeft = iconLeft?.get()?.let {
            {
                Icon(
                    painter = it,
                    contentDescription = "Button left icon",
                    tint = iconTint,
                    modifier = Modifier.size(getContentHeight()),
                )
            }
        },
        contentRight = iconRight?.get()?.let {
            {
                Icon(
                    painter = it,
                    contentDescription = "Button right icon",
                    tint = iconTint,
                    modifier = Modifier.size(getContentHeight()),
                )
            }
        },
    )
}

@Composable
fun SimpleButtonContent(
    text: TextValue?,
    textColor: Color = LocalContentColor.current,
    contentLeft: @Composable (() -> Unit)? = null,
    contentRight: @Composable (() -> Unit)? = null,
) {
    val textPaddingStart = when (contentLeft) {
        null -> 4.dp
        else -> 8.dp
    }
    val textPaddingEnd = when (contentRight) {
        null -> 4.dp
        else -> 8.dp
    }

    contentLeft?.invoke()
    text?.get()?.let {
        Text(
            text = it,
            color = textColor,
            style = LocalTextStyle.current,
            modifier = Modifier
                .padding(start = textPaddingStart, end = textPaddingEnd)
                .height(getContentHeight()),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
    contentRight?.invoke()
}

@Composable
fun SimpleButtonContentLoader() {
    CircularProgressIndicator(
        color = LocalContentColor.current,
        modifier = Modifier.size(getContentHeight()),
    )
}

@Composable
private fun getContentHeight() = LocalTextStyle.current.lineHeight.value.dp + 3.5.dp

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun Preview() {
    PreviewAppTheme {
        Row {
            SimpleButtonActionL(
                modifier = Modifier.padding(horizontal = 4.dp),
                onClick = {},
            ) { SimpleButtonContent("Simple button".textValue()) }

            SimpleButtonActionL(
                modifier = Modifier.padding(horizontal = 4.dp),
                onClick = {},
            ) { SimpleButtonContent(null, iconLeft = AppTheme.specificIcons.add) }

            SimpleButtonActionL(
                modifier = Modifier.padding(horizontal = 4.dp),
                onClick = {},
            ) { SimpleButtonContentLoader() }
            SimpleButtonInlineL(
                modifier = Modifier.padding(horizontal = 4.dp),
                onClick = {},
            ) { SimpleButtonContentLoader() }
        }
    }
}