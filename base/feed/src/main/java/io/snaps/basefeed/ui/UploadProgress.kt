package io.snaps.basefeed.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.snaps.coreuicompose.uikit.other.Progress
import io.snaps.coreuicompose.uikit.other.SimpleCard
import io.snaps.coreuitheme.compose.AppTheme

@Composable
fun UploadProgress(
    modifier: Modifier = Modifier,
    uploadingProgress: Float,
) {
    SimpleCard(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Progress(
                modifier = Modifier.weight(1f),
                progress = uploadingProgress,
                isDashed = true,
                backColor = AppTheme.specificColorScheme.white_10,
                fillColor = AppTheme.specificColorScheme.white_20,
                height = 24.dp,
                cornerSize = 4.dp,
            )
            Text(
                text = "${(uploadingProgress * 100).toInt()}/100%",
                modifier = Modifier
                    .background(
                        color = AppTheme.specificColorScheme.darkGrey.copy(alpha = 0.5f),
                        shape = AppTheme.shapes.small,
                    )
                    .padding(4.dp),
            )
        }
    }
}