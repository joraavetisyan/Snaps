package io.snaps.featurewallet.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.snaps.corecommon.R
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.tools.TileState
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.listtile.MessageBannerState
import io.snaps.coreuicompose.uikit.other.ShimmerTileConfig
import io.snaps.coreuicompose.uikit.other.ShimmerTileLine
import io.snaps.coreuitheme.compose.AppTheme

sealed class RewardsTileState : TileState {

    data class Locked(
        val lockedTokensBalance: String,
    ) : RewardsTileState()

    data class Unlocked(
        val unlockedTokensBalance: String,
    ) : RewardsTileState()

    object Shimmer : RewardsTileState()

    data class Error(val clickListener: () -> Unit) : RewardsTileState()

    @Composable
    override fun Content(modifier: Modifier) {
        RewardsTile(modifier, this)
    }
}

@Composable
fun RewardsTile(
    modifier: Modifier = Modifier,
    data: RewardsTileState,
) {
    when (data) {
        is RewardsTileState.Unlocked -> RewardsCard(
            modifier = modifier,
            title = StringKey.WalletTitleAvailableRewards.textValue(),
            description = StringKey.WalletDescriptionAvailableRewards.textValue(),
            coin = data.unlockedTokensBalance,
            imageValue = ImageValue.ResImage(R.drawable.img_available_rewards_background)
        )
        is RewardsTileState.Locked -> RewardsCard(
            modifier = modifier,
            title = StringKey.WalletTitleLockedRewards.textValue(),
            description = StringKey.WalletDescriptionLockedRewards.textValue(),
            coin = data.lockedTokensBalance,
            imageValue = ImageValue.ResImage(R.drawable.img_locked_rewards_background)
        )
        is RewardsTileState.Shimmer -> Shimmer(modifier = modifier)
        is RewardsTileState.Error -> MessageBannerState
            .defaultState(onClick = data.clickListener)
            .Content(modifier = modifier)
    }
}

@Composable
private fun Shimmer(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(color = AppTheme.specificColorScheme.white, shape = AppTheme.shapes.medium)
            .fillMaxWidth()
            .height(200.dp)
            .padding(20.dp),
    ) {
        ShimmerTileLine(
            width = ShimmerTileConfig.WidthExtraSmall,
            height = AppTheme.specificTypography.bodySmall.lineHeight.value.dp,
        )
        Spacer(modifier = Modifier.height(4.dp))
        ShimmerTileLine(
            width = ShimmerTileConfig.WidthSmall,
            height = AppTheme.specificTypography.headlineLarge.lineHeight.value.dp,
        )
        Spacer(modifier = Modifier.weight(1f))
        ShimmerTileLine(
            width = ShimmerTileConfig.WidthMedium,
            height = AppTheme.specificTypography.titleMedium.lineHeight.value.dp,
        )
    }
}

@Composable
private fun RewardsCard(
    modifier: Modifier,
    title: TextValue,
    description: TextValue,
    coin: String,
    imageValue: ImageValue,
) {
    Box(
        modifier = modifier
            .clip(AppTheme.shapes.medium)
            .fillMaxWidth()
            .height(200.dp),
    ) {
        Image(
            painter = imageValue.get(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
        )
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = title.get(),
                style = AppTheme.specificTypography.bodySmall,
                color = AppTheme.specificColorScheme.white,
            )
            Text(
                text = "$coin SNPS",
                style = AppTheme.specificTypography.headlineLarge,
                color = AppTheme.specificColorScheme.white,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = description.get(),
                style = AppTheme.specificTypography.titleMedium,
                color = AppTheme.specificColorScheme.white,
                modifier = Modifier.width(200.dp),
            )
        }
    }
}