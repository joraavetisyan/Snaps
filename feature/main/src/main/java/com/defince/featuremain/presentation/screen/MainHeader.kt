package com.defince.featuremain.presentation.screen

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.defince.corecommon.R
import com.defince.corecommon.container.ImageValue
import com.defince.coreuicompose.tools.get
import com.defince.coreuicompose.uikit.listtile.CellTileState
import com.defince.coreuitheme.compose.AppTheme
import com.defince.coreuitheme.compose.MainHeaderElementShape

sealed interface MainHeaderState {

    data class Data(
        val profileImage: ImageValue,
        val energy: String,
        val gold: String,
        val silver: String,
        val bronze: String,
    ) : MainHeaderState

    object Shimmer : MainHeaderState

    object Error : MainHeaderState
}

@Composable
fun MainHeader(
    uiState: MainHeaderState,
) {
    when (uiState) {
        is MainHeaderState.Data -> Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp),
        ) {
            Card(
                shape = CircleShape,
            ) {
                Image(
                    painter = uiState.profileImage.get(),
                    contentDescription = null,
                    modifier = Modifier.size(44.dp),
                    contentScale = ContentScale.Crop,
                )
            }
            Spacer(Modifier.weight(1f))
            Card(
                modifier = Modifier
                    .shadow(elevation = 16.dp, shape = MainHeaderElementShape)
                    .background(
                        color = AppTheme.specificColorScheme.uiContentBg,
                        shape = MainHeaderElementShape,
                    ),
                shape = MainHeaderElementShape,
            ) {
                Element(
                    imageRes = R.drawable.img_energy,
                    value = uiState.energy,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }
            Spacer(Modifier.width(4.dp))
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
                    Element(R.drawable.img_coin_silver, uiState.silver)
                    Element(R.drawable.img_coin_gold, uiState.gold)
                    Element(R.drawable.img_coin_bronze, uiState.bronze)
                }
            }
        }
        MainHeaderState.Error -> TODO()
        MainHeaderState.Shimmer -> CellTileState.Shimmer(
            needLeftCircle = true,
            needRightLine = true,
        ).Content(modifier = Modifier)
    }
}

@Composable
private fun Element(
    @DrawableRes imageRes: Int,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = ImageValue.ResImage(imageRes).get(),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            contentScale = ContentScale.Crop,
        )
        Text(text = value, style = AppTheme.specificTypography.bodySmall)
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun Preview() {
    MainHeader(
        uiState = MainHeaderState.Data(
            profileImage = ImageValue.Url("https://picsum.photos/44"),
            energy = "12",
            gold = "12",
            silver = "12",
            bronze = "12",
        )
    )
}