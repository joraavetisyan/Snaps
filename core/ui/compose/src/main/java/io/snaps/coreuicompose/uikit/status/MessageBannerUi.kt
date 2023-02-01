package io.snaps.coreuicompose.uikit.status

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.snaps.corecommon.container.TextValue
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAllExcludeBottom
import io.snaps.coreuitheme.compose.AppTheme

sealed class BannerMessage(val value: TextValue) {
    class Message(value: TextValue) : BannerMessage(value)
    class Warning(value: TextValue) : BannerMessage(value)
    class Error(value: TextValue) : BannerMessage(value)
}

@Composable
fun MessageBannerUi(state: BannerMessage?) {
    var currentText by remember { mutableStateOf(AnnotatedString("")) }
    var currentColor by remember { mutableStateOf(Color.Black) }
    if (state != null) {
        currentText = state.value.get()
        currentColor = when (state) {
            is BannerMessage.Error -> AppTheme.specificColorScheme.uiSystemRed
            is BannerMessage.Message -> AppTheme.specificColorScheme.uiSystemGreen
            is BannerMessage.Warning -> AppTheme.specificColorScheme.uiSystemGreen
        }
    }
    AnimatedVisibility(
        visible = state != null,
        enter = slideInVertically(),
        exit = slideOutVertically(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(currentColor)
                .inset(insetAllExcludeBottom())
                .padding(16.dp),
        ) {
            Text(
                text = currentText,
                modifier = Modifier.fillMaxWidth(),
                color = AppTheme.specificColorScheme.actionLabel,
                style = AppTheme.specificTypography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }
    }
}