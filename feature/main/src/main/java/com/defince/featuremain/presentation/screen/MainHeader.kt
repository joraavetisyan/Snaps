package com.defince.featuremain.presentation.screen

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.defince.corecommon.R
import com.defince.corecommon.container.ImageValue
import com.defince.coreuicompose.tools.get
import com.defince.coreuicompose.uikit.listtile.CellTileState
import com.defince.coreuicompose.uikit.listtile.LeftPart
import com.defince.coreuicompose.uikit.listtile.RightPart

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
            WorthWidget(ImageValue.ResImage(R.drawable.img_energy) to uiState.energy)
            Spacer(Modifier.width(4.dp))
            WorthWidget(
                ImageValue.ResImage(R.drawable.img_coin_silver) to uiState.silver,
                ImageValue.ResImage(R.drawable.img_coin_gold) to uiState.gold,
                ImageValue.ResImage(R.drawable.img_coin_bronze) to uiState.bronze,
            )
        }
        MainHeaderState.Error -> TODO()
        MainHeaderState.Shimmer -> CellTileState.Shimmer(
            leftPart = LeftPart.Shimmer,
            rightPart = RightPart.Shimmer(needRightLine = true),
        ).Content(modifier = Modifier)
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