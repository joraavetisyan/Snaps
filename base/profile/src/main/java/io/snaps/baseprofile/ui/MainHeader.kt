package io.snaps.baseprofile.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import io.snaps.coreuicompose.uikit.listtile.MiddlePart
import io.snaps.coreuicompose.uikit.listtile.RightPart
import io.snaps.coreuicompose.uikit.other.ShimmerTileCircle

sealed class MainHeaderState {

    open val onProfileClicked: () -> Unit = {}
    open val onWalletClicked: () -> Unit = {}

    data class Data(
        val profileImage: ImageValue?,
        val energy: String,
        val locked: String,
        val unlocked: String,
        override val onProfileClicked: () -> Unit,
        override val onWalletClicked: () -> Unit,
    ) : MainHeaderState()

    object Shimmer : MainHeaderState()

    data class Error(
        override val onProfileClicked: () -> Unit,
        override val onWalletClicked: () -> Unit,
    ) : MainHeaderState()
}

@Composable
fun MainHeader(
    modifier: Modifier = Modifier,
    state: MainHeaderState,
) {
    when (state) {
        is MainHeaderState.Data -> Data(modifier, state)
        is MainHeaderState.Error -> Error(modifier, state)
        MainHeaderState.Shimmer -> Shimmer(modifier)
    }
}

@Composable
private fun Data(
    modifier: Modifier,
    state: MainHeaderState.Data
) {
    Content(
        modifier = modifier,
        profileImage = state.profileImage,
        energy = state.energy,
        locked = state.locked,
        unlocked = state.unlocked,
        onProfileClicked = state.onProfileClicked,
        onWalletClicked = state.onWalletClicked,
    )
}

@Composable
private fun Error(
    modifier: Modifier,
    state: MainHeaderState
) {
    Content(
        modifier = modifier,
        profileImage = null,
        energy = null,
        locked = null,
        unlocked = null,
        onProfileClicked = state.onProfileClicked,
        onWalletClicked = state.onWalletClicked,
    )
}

@Composable
private fun Shimmer(modifier: Modifier) {
    Container(modifier = modifier) {
        CellTileState.Shimmer(
            leftPart = LeftPart.Shimmer,
            middlePart = MiddlePart.Shimmer(),
            rightPart = RightPart.Shimmer(needBoldLine = true),
        ).Content(modifier = Modifier)
    }
}

@Composable
private fun Content(
    modifier: Modifier,
    profileImage: ImageValue?,
    energy: String?,
    locked: String?,
    unlocked: String?,
    onProfileClicked: () -> Unit,
    onWalletClicked: () -> Unit,
) {
    Container(modifier) {
        Card(
            shape = CircleShape,
            modifier = Modifier
                .defaultTileRipple(onClick = onProfileClicked),
        ) {
            if (profileImage != null) Image(
                painter = profileImage.get(),
                contentDescription = null,
                modifier = Modifier.size(44.dp),
                contentScale = ContentScale.Crop,
            ) else ShimmerTileCircle(
                size = 44.dp,
            )
        }
        Spacer(Modifier.weight(1f))
        EnergyWidget(energy.orEmpty())
        Spacer(Modifier.width(4.dp))
        ValueWidget(
            ImageValue.ResImage(R.drawable.img_coin_locked) to locked.orEmpty(),
            ImageValue.ResImage(R.drawable.img_coin_gold) to unlocked.orEmpty(),
            modifier = Modifier.defaultTileRipple(onClick = onWalletClicked),
        )
    }
}

@Composable
private fun Container(
    modifier: Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 12.dp),
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun Preview() {
    MainHeader(
        state = MainHeaderState.Data(
            profileImage = null,
            energy = "12",
            unlocked = "12",
            locked = "12",
            onProfileClicked = {},
            onWalletClicked = {},
        )
    )
}