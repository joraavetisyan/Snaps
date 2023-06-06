package io.snaps.featurecollection.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuitheme.compose.AppTheme

@Composable
fun NftImageWithBadge(nftImage: ImageValue, badgeValue: String?) {
    Box {
        Image(
            painter = nftImage.get(),
            contentDescription = null,
            modifier = Modifier
                .padding(4.dp)
                .size(100.dp)
                .clip(AppTheme.shapes.small)
                .align(Alignment.Center),
        )
        if (badgeValue != null) {
            Text(
                text = StringKey.NftDetailsFieldLeftCopies.textValue(badgeValue).get(),
                color = AppTheme.specificColorScheme.white,
                style = AppTheme.specificTypography.labelMedium,
                modifier = Modifier
                    .background(
                        color = AppTheme.specificColorScheme.uiAccent,
                        shape = AppTheme.shapes.small,
                    )
                    .padding(horizontal = 8.dp, vertical = 2.dp)
                    .align(Alignment.TopStart),
            )
        }
    }
}