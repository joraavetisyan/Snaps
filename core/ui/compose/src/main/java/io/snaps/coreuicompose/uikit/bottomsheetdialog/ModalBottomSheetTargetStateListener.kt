package io.snaps.coreuicompose.uikit.bottomsheetdialog

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow

@OptIn(ExperimentalMaterialApi::class)
@Composable
inline fun ModalBottomSheetTargetStateListener(
    sheetState: ModalBottomSheetState,
    crossinline onStateToChange: (willBeHidden: Boolean) -> Unit,
) {
    LaunchedEffect(Unit) {
        snapshotFlow { sheetState.targetValue }.collect {
            onStateToChange(it == ModalBottomSheetValue.Hidden)
        }
    }
}