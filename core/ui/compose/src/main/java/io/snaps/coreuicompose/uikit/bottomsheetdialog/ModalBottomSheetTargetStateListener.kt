package io.snaps.coreuicompose.uikit.bottomsheetdialog

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.drop

@OptIn(ExperimentalMaterialApi::class)
@Composable
inline fun ModalBottomSheetCurrentStateListener(
    sheetState: ModalBottomSheetState,
    drop: Int = 0,
    crossinline onStateChanged: (isHidden: Boolean) -> Unit,
) {
    LaunchedEffect(Unit) {
        snapshotFlow { sheetState.currentValue }.drop(drop).collect {
            onStateChanged(it == ModalBottomSheetValue.Hidden)
        }
    }
}

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