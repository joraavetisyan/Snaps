package io.snaps.coreuicompose.uikit.button

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.snaps.corecommon.container.TextValue
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuitheme.compose.AppTheme

// todo standardize, make SimpleButton private in its file
@Composable
fun SimpleTwoLineButtonActionL(
    modifier: Modifier = Modifier,
    text: TextValue?,
    additionalText: TextValue?,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    SimpleButton(
        modifier = modifier,
        size = SimpleButtonSize.l().copy(
            contentPadding = PaddingValues(8.dp),
        ),
        onClick = onClick,
        onLongClick = onLongClick,
        enabled = enabled,
        interactionSource = interactionSource,
        colors = SimpleButtonColors.action(),
        content = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                text?.get()?.let {
                    Text(
                        text = it,
                        color = LocalContentColor.current,
                        style = LocalTextStyle.current,
                        modifier = Modifier
                            .padding(start = 4.dp, end = 8.dp)
                            .height(LocalTextStyle.current.lineHeight.value.dp + 3.5.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                additionalText?.get()?.let {
                    Text(
                        text = it,
                        color = LocalContentColor.current,
                        style = AppTheme.specificTypography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        },
    )
}