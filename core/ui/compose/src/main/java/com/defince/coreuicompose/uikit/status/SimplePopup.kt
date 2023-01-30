package com.defince.coreuicompose.uikit.status

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.defince.corecommon.container.IconValue
import com.defince.corecommon.container.TextValue
import com.defince.coreuicompose.tools.addIf
import com.defince.coreuicompose.tools.defaultBorder
import com.defince.coreuicompose.tools.doOnClick
import com.defince.coreuicompose.uikit.input.DefaultSelectorItem
import com.defince.coreuicompose.uikit.input.SimpleSelectorConfig
import com.defince.coreuicompose.uikit.input.SimpleTextFieldConfig
import com.defince.coreuitheme.compose.AppTheme

object SimplePopupConfig {

    @Composable
    fun shape() = SimpleTextFieldConfig.shape()

    @Composable
    fun backgroundColor() = AppTheme.specificColorScheme.uiContentBg
}

@Composable
fun SimplePopup(
    modifier: Modifier = Modifier,
    offset: IntOffset = IntOffset(0, 0),
    shape: Shape = SimplePopupConfig.shape(),
    needBackground: Boolean = true,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    Popup(
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(focusable = true),
        offset = offset,
    ) {
        Column(
            modifier = Modifier
                .addIf(needBackground) {
                    defaultBorder(shape)
                        .clip(shape)
                        .background(SimplePopupConfig.backgroundColor())
                }
                .then(modifier),
        ) {
            content()
        }
    }
}

@Composable
fun SimplePopupItem(
    modifier: Modifier = Modifier,
    value: TextValue,
    icon: IconValue? = null,
    onClick: () -> Unit,
) {
    DefaultSelectorItem(
        modifier = modifier
            .fillMaxWidth()
            .doOnClick(onClick = onClick)
            .padding(horizontal = SimpleSelectorConfig.Padding, vertical = SimpleSelectorConfig.Padding / 2),
        value = value,
        icon = icon
    )
}