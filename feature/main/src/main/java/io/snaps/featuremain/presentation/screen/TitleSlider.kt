package io.snaps.featuremain.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import io.snaps.corecommon.container.TextValue
import io.snaps.coreuicompose.tools.defaultTileRipple
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.button.SimpleButtonLightM
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.MainHeaderElementShape

@Composable
fun TitleSlider(
    modifier: Modifier = Modifier,
    title1: TextValue,
    title2: TextValue,
    isFirst: Boolean,
    onSlid: (Boolean) -> Unit,
) {
    Card(
        modifier = modifier
            .padding(12.dp)
            .shadow(elevation = 16.dp, shape = MainHeaderElementShape)
            .background(
                color = AppTheme.specificColorScheme.uiContentBg,
                shape = MainHeaderElementShape,
            ),
    ) {
        Row(
            modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val onClick = { onSlid(isFirst) }

            @Composable
            fun Active(text: TextValue) = SimpleButtonLightM(onClick = onClick) {
                SimpleButtonContent(text = text)
            }

            @Composable
            fun NotActive(text: TextValue) = Text(
                text = text.get(),
                modifier = Modifier.defaultTileRipple(onClick = onClick),
            )

            if (isFirst) Active(text = title1) else NotActive(text = title1)
            if (!isFirst) Active(text = title2) else NotActive(text = title2)
        }
    }
}