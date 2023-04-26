package io.snaps.basefeed.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.corecommon.strings.emojis
import io.snaps.coreuicompose.tools.addIf
import io.snaps.coreuicompose.tools.defaultTileRipple
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.input.SimpleTextField
import io.snaps.coreuitheme.compose.AppTheme

@Composable
fun CommentInput(
    profileImage: ImageValue?,
    value: TextFieldValue,
    isSendEnabled: Boolean,
    onValueChange: (TextFieldValue) -> Unit,
    isEditable: Boolean,
    onEmojiClick: (String) -> Unit,
    onInputClick: () -> Unit,
    onSendClick: () -> Unit,
    focusRequester: FocusRequester? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 16.dp)
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (isEditable) {
            InputRow(
                focusRequester = focusRequester,
                onClick = null,
                profileImage = profileImage,
                value = value,
                onValueChange = onValueChange,
                onSendClick = onSendClick,
                isSendEnabled = isSendEnabled,
            )
            EmojiRow(onEmojiClick = onEmojiClick)
        } else {
            EmojiRow(onEmojiClick = { onInputClick() })
            InputRow(
                onClick = onInputClick,
                profileImage = profileImage,
                value = value,
                onValueChange = onValueChange,
                onSendClick = null,
                isSendEnabled = isSendEnabled,
            )
        }
    }
}

@Composable
private fun EmojiRow(
    onEmojiClick: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        emojis.forEach {
            Text(it, modifier = Modifier.defaultTileRipple { onEmojiClick(it) })
        }
    }
}

@Composable
private fun InputRow(
    onClick: (() -> Unit)?,
    profileImage: ImageValue?,
    value: TextFieldValue,
    isSendEnabled: Boolean,
    onValueChange: (TextFieldValue) -> Unit,
    onSendClick: (() -> Unit)?,
    focusRequester: FocusRequester? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        profileImage?.let {
            Card(
                shape = CircleShape,
            ) {
                Image(
                    painter = profileImage.get(),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    contentScale = ContentScale.Crop,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
        }
        SimpleTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(text = StringKey.CommentsHint.textValue().get()) },
            modifier = Modifier
                .addIf(focusRequester != null) {
                    focusRequester(focusRequester!!)
                }
                .weight(1f)
                .addIf(onClick != null) {
                    defaultTileRipple(onClick = onClick)
                },
            enabled = onClick == null,
            readOnly = onClick != null,
        )
        onSendClick?.let {
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onSendClick,
                enabled = isSendEnabled,
            ) {
                Icon(
                    painter = AppTheme.specificIcons.sendCircled.get(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .addIf(!isSendEnabled) { alpha(0.5f) },
                    tint = Color.Unspecified,
                )
            }
        }
    }
}