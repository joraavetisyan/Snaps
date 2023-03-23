package io.snaps.baseprofile.ui

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
import io.snaps.corecommon.R
import io.snaps.corecommon.container.ImageValue
import io.snaps.coreuicompose.tools.defaultTileRipple
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.listtile.CellTileState
import io.snaps.coreuicompose.uikit.listtile.LeftPart
import io.snaps.coreuicompose.uikit.listtile.RightPart

sealed interface MainHeaderState {

    data class Data(
        val profileImage: ImageValue?,
        val energy: String,
        val locked: String,
        val unlocked: String,
        val onProfileClicked: () -> Unit,
        val onWalletClicked: () -> Unit,
    ) : MainHeaderState

    object Shimmer : MainHeaderState

    object Error : MainHeaderState
}

@Composable
fun MainHeader(
    modifier: Modifier = Modifier,
    state: MainHeaderState,
) {
    when (state) {
        is MainHeaderState.Data -> Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.padding(horizontal = 12.dp),
        ) {
            Card(
                shape = CircleShape,
                modifier = Modifier.defaultTileRipple(onClick = state.onProfileClicked),
            ) {
                state.profileImage?.let {
                    Image(
                        painter = state.profileImage.get(),
                        contentDescription = null,
                        modifier = Modifier.size(44.dp),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            EnergyWidget(state.energy)
            Spacer(Modifier.width(4.dp))
            ValueWidget(
                ImageValue.ResImage(R.drawable.img_coin_locked) to state.locked,
                ImageValue.ResImage(R.drawable.img_coin_gold) to state.unlocked,
                modifier = Modifier.defaultTileRipple(onClick = state.onWalletClicked),
            )
        }
        MainHeaderState.Error, // todo
        MainHeaderState.Shimmer -> CellTileState.Shimmer(
            leftPart = LeftPart.Shimmer,
            rightPart = RightPart.Shimmer(needRightLine = true),
        ).Content(modifier = modifier)
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun Preview() {
    MainHeader(
        state = MainHeaderState.Data(
            profileImage = ImageValue.Url("https://picsum.photos/44"),
            energy = "12",
            unlocked = "12",
            locked = "12",
            onProfileClicked = {},
            onWalletClicked = {},
        )
    )
}