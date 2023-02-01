package io.snaps.baseprofile.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.snaps.corecommon.container.ImageValue
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.MainHeaderElementShape

@Composable
fun WorthWidget(vararg items: Pair<ImageValue, String>) {
    @Composable
    fun Element(
        image: ImageValue,
        value: String,
        modifier: Modifier = Modifier,
    ) {
        Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = image.get(),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                contentScale = ContentScale.Crop,
            )
            Text(text = value, style = AppTheme.specificTypography.bodySmall)
        }
    }
    Card(
        modifier = Modifier
            .shadow(elevation = 16.dp, shape = MainHeaderElementShape)
            .background(
                color = AppTheme.specificColorScheme.uiContentBg,
                shape = MainHeaderElementShape,
            ),
        shape = MainHeaderElementShape,
        elevation = CardDefaults.cardElevation(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            items.forEach { Element(it.first, it.second) }
        }
    }
}