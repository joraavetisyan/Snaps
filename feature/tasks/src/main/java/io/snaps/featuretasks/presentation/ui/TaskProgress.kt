package io.snaps.featuretasks.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.snaps.baseprofile.ui.ValueWidget
import io.snaps.corecommon.R
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.imageValue
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.other.Progress

@Composable
fun TaskProgress(
    modifier: Modifier = Modifier,
    progress: Int,
    maxValue: Int = 100,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Image(
            painter = R.drawable.img_energy.imageValue().get(),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            contentScale = ContentScale.Crop,
        )
        Progress(
            modifier = Modifier.weight(1f),
            progress = (progress / maxValue.toFloat()).takeUnless { it.isNaN() } ?: 0f,
        )
        ValueWidget(null to "$progress/$maxValue")
    }
}