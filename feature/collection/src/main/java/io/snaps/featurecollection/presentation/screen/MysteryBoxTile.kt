package io.snaps.featurecollection.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.snaps.basenft.data.model.MysteryBoxType
import io.snaps.basenft.domain.ProbabilitiesModel
import io.snaps.baseprofile.ui.ValueWidget
import io.snaps.corecommon.R
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.imageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.FiatValue
import io.snaps.corecommon.model.NftType
import io.snaps.coreuicompose.tools.TileState
import io.snaps.coreuicompose.tools.defaultTileRipple
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.listtile.MessageBannerState
import io.snaps.coreuicompose.uikit.listtile.MiddlePart
import io.snaps.coreuicompose.uikit.other.ShimmerTile
import io.snaps.coreuitheme.compose.AppTheme

sealed class MysteryBoxTileState : TileState {

    data class Data(
        val type: MysteryBoxType,
        val cost: FiatValue,
        val probabilities: ProbabilitiesModel,
        val clickListener: () -> Unit,
    ) : MysteryBoxTileState()

    object Shimmer : MysteryBoxTileState()

    data class Error(val onClick: () -> Unit) : MysteryBoxTileState()

    @Composable
    override fun Content(modifier: Modifier) {
       MysteryBoxTile(modifier = modifier, data = this)
    }
}

@Composable
private fun MysteryBoxTile(
    modifier: Modifier,
    data: MysteryBoxTileState,
) {
    when (data) {
        is MysteryBoxTileState.Shimmer -> Shimmer(modifier = modifier)
        is MysteryBoxTileState.Error -> MessageBannerState.defaultState(onClick = data.onClick)
        is MysteryBoxTileState.Data -> Data(modifier, data)
    }
}

@Composable
private fun Data(
    modifier: Modifier,
    data: MysteryBoxTileState.Data,
) {
    Container(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .defaultTileRipple(onClick = data.clickListener, padding = 0.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(100.dp)
                .background(color = Color(0xFFDEE5FB), shape = AppTheme.shapes.medium), // todo add color to palette
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = when (data.type) {
                    MysteryBoxType.FirstTier -> R.drawable.img_mystery_box_first
                    MysteryBoxType.SecondTier -> R.drawable.img_mystery_box_second
                }.imageValue().get(),
                contentDescription = null,
                modifier = Modifier.width(100.dp),
                contentScale = ContentScale.Crop,
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "Mystery Box", // todo localization
                    style = AppTheme.specificTypography.labelMedium,
                    color = AppTheme.specificColorScheme.textPrimary,
                )
                Spacer(modifier = Modifier.weight(1f))
                ValueWidget(R.drawable.img_coin_bronze.imageValue() to data.cost.getFormatted().textValue())
            }
            Text(
                text = "Беспроигрышный", // todo localization
                style = AppTheme.specificTypography.labelLarge.copy(fontSize = 8.sp),
                color = AppTheme.specificColorScheme.textSecondary,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                when (data.type) {
                    MysteryBoxType.FirstTier -> {
                        MysteryBoxItem(
                            nftType = NftType.Sub,
                            isGuaranteed = true,
                            probability = data.probabilities.sub ?: 0.0,
                            imageBackgroundColor = Color(0xFF65AFF5), // todo add color to palette
                            image = R.drawable.img_sunglasses3.imageValue(),
                        )
                        MysteryBoxItem(
                            nftType = NftType.Follower,
                            isGuaranteed = false,
                            probability = data.probabilities.follower ?: 0.0,
                            imageBackgroundColor = Color(0xFF7165F5), // todo add color to palette
                            image = R.drawable.img_sunglasses3.imageValue(),
                        )
                        MysteryBoxItem(
                            nftType = NftType.Follower,
                            isGuaranteed = false,
                            probability = data.probabilities.follower ?: 0.0,
                            imageBackgroundColor = Color(0xFFAD65F5), // todo add color to palette
                            image = R.drawable.img_sunglasses3.imageValue(),
                        )
                    }
                    MysteryBoxType.SecondTier -> {
                        MysteryBoxItem(
                            nftType = NftType.Sponsor,
                            isGuaranteed = true,
                            probability = data.probabilities.sponsor ?: 0.0,
                            imageBackgroundColor = Color(0xFFAD65F5), // todo add color to palette
                            image = R.drawable.img_sunglasses5.imageValue(),
                        )
                        MysteryBoxItem(
                            nftType = NftType.Influencer,
                            isGuaranteed = false,
                            probability = data.probabilities.influencer ?: 0.0,
                            imageBackgroundColor = Color(0xFFF56E65), // todo add color to palette
                            image = R.drawable.img_sunglasses6.imageValue(),
                        )
                        MysteryBoxItem(
                            nftType = NftType.Follower,
                            isGuaranteed = false,
                            probability = data.probabilities.follower ?: 0.0,
                            imageBackgroundColor = Color(0xFFE3B40C), // todo add color to palette
                            image = R.drawable.img_sunglasses3.imageValue(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.MysteryBoxItem(
    nftType: NftType,
    isGuaranteed: Boolean,
    probability: Double,
    imageBackgroundColor: Color,
    image: ImageValue,
) {
    @Composable
    fun probabilityInfo() {
        Text(
            text = "Шанс", // todo localization
            style = AppTheme.specificTypography.labelLarge.copy(fontSize = 5.sp, lineHeight = 6.sp),
            color = AppTheme.specificColorScheme.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp),
        )
        Text(
            text = "${probability.toInt()}%", // todo localization
            style = AppTheme.specificTypography.labelLarge.copy(fontSize = 5.sp),
            color = AppTheme.specificColorScheme.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }

    Column(
        modifier = Modifier
            .weight(1f)
            .background(color = Color(0xFFDEE5FB), shape = AppTheme.shapes.medium) // todo add color to palette
            .padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = imageBackgroundColor, shape = AppTheme.shapes.medium)
                .height(56.dp),
        ) {
            Image(
                painter = image.get(),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 4.dp)
            )
            Text(
                text = nftType.displayName,
                style = AppTheme.specificTypography.labelLarge.copy(fontSize = 5.sp),
                color = AppTheme.specificColorScheme.white,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(vertical = 2.dp)
                    .padding(bottom = 4.dp)
                    .background(color = AppTheme.specificColorScheme.white.copy(alpha = 0.4f), shape = AppTheme.shapes.medium)
                    .padding(horizontal = 8.dp),
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            if (isGuaranteed) {
                Text(
                    text = "Гарантировано", // todo localization
                    style = AppTheme.specificTypography.labelLarge.copy(fontSize = 8.sp, lineHeight = 9.sp),
                    color = AppTheme.specificColorScheme.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                probabilityInfo()
            }
        }
    }
}

@Composable
private fun Shimmer(
    modifier: Modifier,
) {
    Container(modifier.fillMaxWidth()) {
        ShimmerTile(
            modifier = Modifier
                .size(100.dp)
                .padding(end = 8.dp),
            shape = AppTheme.shapes.small,
        )
        Spacer(modifier = Modifier.weight(1f))
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            repeat(4) {
                MiddlePart.Shimmer(needValueLine = true).Content(modifier = Modifier)
            }
        }
    }
}

@Composable
private fun Container(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.specificColorScheme.white,
        ),
        border = BorderStroke(width = 1.dp, color = AppTheme.specificColorScheme.darkGrey.copy(0.5f)),
        shape = AppTheme.shapes.medium,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}