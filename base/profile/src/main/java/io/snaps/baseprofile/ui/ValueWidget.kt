package io.snaps.baseprofile.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.snaps.corecommon.R
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.imageValue
import io.snaps.corecommon.container.textValue
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.MainHeaderElementShape
import io.snaps.coreuitheme.compose.colors

@Composable
fun EnergyWidget(value: String, isFull: Boolean = false) {
    ValueWidget(
        R.drawable.img_energy.imageValue() to value.textValue(),
        backgroundColor = colors { if (isFull) uiSystemGreen.copy(alpha = 0.1f) else uiContentBg },
    )
}

@Composable
fun ValueWidget(
    vararg items: Pair<ImageValue?, TextValue>,
    modifier: Modifier = Modifier,
    backgroundColor: Color = AppTheme.specificColorScheme.uiContentBg,
) {
    @Composable
    fun Element(
        image: ImageValue?,
        value: TextValue,
        modifier: Modifier = Modifier,
    ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (image != null) Image(
                painter = image.get(),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 4.dp)
                    .size(24.dp),
                contentScale = ContentScale.Crop,
            )
            Text(
                text = value.get(),
                style = AppTheme.specificTypography.bodySmall,
            )
        }
    }
    Card(
        modifier = modifier.shadow(elevation = 16.dp, shape = MainHeaderElementShape),
        shape = MainHeaderElementShape,
        elevation = CardDefaults.cardElevation(),
    ) {
        Row(
            modifier = Modifier
                .background(color = backgroundColor, shape = MainHeaderElementShape)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items.forEach { Element(it.first, it.second) }
        }
    }
}