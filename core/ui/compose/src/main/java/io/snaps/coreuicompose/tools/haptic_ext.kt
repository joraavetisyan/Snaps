package io.snaps.coreuicompose.tools

import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

fun HapticFeedback.longPress() {
    performHapticFeedback(HapticFeedbackType.LongPress)
}

fun HapticFeedback.error() {
    // todo: изменить, когда в compose появится поддержка большего количества вариантов
    performHapticFeedback(HapticFeedbackType.LongPress)
}