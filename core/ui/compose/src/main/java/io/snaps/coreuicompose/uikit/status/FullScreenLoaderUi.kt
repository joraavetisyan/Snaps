package io.snaps.coreuicompose.uikit.status

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.snaps.coreuitheme.compose.AppTheme

@Composable
fun FullScreenLoaderUi(
    isLoading: Boolean,
    backgroundColor: Color = AppTheme.specificColorScheme.darkGrey.copy(alpha = .7f),
) {
    Crossfade(targetState = isLoading) {
        if (it) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor),
            ) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}