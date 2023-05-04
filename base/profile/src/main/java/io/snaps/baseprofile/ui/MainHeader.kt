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
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.tools.defaultTileRipple
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.other.ShimmerTileCircle
import io.snaps.coreuicompose.uikit.other.ShimmerTileLine
import io.snaps.coreuitheme.compose.AppTheme

object MainHeaderConfig {

    val AvatarSize = 44.dp
}

sealed class MainHeaderState {

    open val onProfileClicked: () -> Unit = {}
    open val onWalletClicked: () -> Unit = {}

    data class Data(
        val profileImage: ImageValue,
        val energy: String,
        val bnb: String,
        val snp: String,
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
    Container(modifier) {
        Avatar(
            onProfileClicked = state.onProfileClicked,
            imageValue = state.profileImage,
        )
        Spacer(Modifier.weight(1f))
        EnergyWidget(state.energy)
        Spacer(Modifier.width(4.dp))
        ValueWidget(
            ImageValue.ResImage(R.drawable.ic_bnb_token) to state.bnb,
            ImageValue.ResImage(R.drawable.ic_snp_token) to state.snp,
            modifier = Modifier.defaultTileRipple(onClick = state.onWalletClicked),
        )
    }
}

@Composable
private fun Avatar(onProfileClicked: () -> Unit, imageValue: ImageValue) {
    Card(
        shape = CircleShape,
        modifier = Modifier.defaultTileRipple(onClick = onProfileClicked),
    ) {
        Image(
            painter = imageValue.get(),
            contentDescription = null,
            modifier = Modifier.size(MainHeaderConfig.AvatarSize),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun Error(
    modifier: Modifier,
    state: MainHeaderState
) {
    Container(modifier) {
        Avatar(
            onProfileClicked = state.onProfileClicked,
            imageValue = ImageValue.ResImage(R.drawable.img_avatar),
        )
        Spacer(Modifier.weight(1f))
        ValueWidget(null to StringKey.Error.textValue().get().text)
        Spacer(Modifier.width(4.dp))
        ValueWidget(
            null to StringKey.Error.textValue().get().text,
            modifier = Modifier.defaultTileRipple(onClick = state.onWalletClicked),
        )
    }
}

@Composable
private fun Shimmer(modifier: Modifier) {
    Container(modifier = modifier) {
        ShimmerTileCircle(
            modifier = Modifier.padding(AppTheme.specificValues.ripple_inner_padding),
            size = MainHeaderConfig.AvatarSize,
        )
        Spacer(Modifier.weight(1f))
        ShimmerTileLine(width = 50.dp, height = 32.dp)
        Spacer(Modifier.width(6.dp))
        ShimmerTileLine(width = 82.dp, height = 32.dp)
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
            profileImage = ImageValue.ResImage(R.drawable.img_guy_welcoming),
            energy = "12",
            snp = "12",
            bnb = "12",
            onProfileClicked = {},
            onWalletClicked = {},
        )
    )
}