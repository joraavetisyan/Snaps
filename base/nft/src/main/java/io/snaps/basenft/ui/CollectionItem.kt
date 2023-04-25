package io.snaps.basenft.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.NftType
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.tools.TileState
import io.snaps.coreuicompose.tools.defaultTileRipple
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.listtile.MessageBannerState
import io.snaps.coreuicompose.uikit.listtile.MiddlePart
import io.snaps.coreuicompose.uikit.other.ShimmerTile
import io.snaps.coreuicompose.uikit.other.SimpleCard
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.LocalStringHolder

sealed class CollectionItemState : TileState {

    data class Nft(
        val type: NftType,
        val price: String,
        val image: ImageValue,
        val dailyReward: String,
        val dailyUnlock: String,
        val dailyConsumption: String,
        val isHealthy: Boolean,
        val onRepairClicked: () -> Unit,
    ) : CollectionItemState()

    data class MysteryBox(
        val image: ImageValue,
    ) : CollectionItemState()

    data class AddItem(val onClick: () -> Unit) : CollectionItemState()

    object Shimmer : CollectionItemState()

    data class Error(val onClick: () -> Unit) : CollectionItemState()

    @Composable
    override fun Content(modifier: Modifier) {
        CollectionItem(modifier = modifier, data = this)
    }
}

@Composable
private fun CollectionItem(
    modifier: Modifier,
    data: CollectionItemState,
) {
    when (data) {
        is CollectionItemState.Nft -> Nft(modifier, data)
        is CollectionItemState.Shimmer -> Shimmer(modifier = modifier)
        is CollectionItemState.Error -> MessageBannerState
            .defaultState(onClick = data.onClick)
            .Content(modifier = modifier)
        is CollectionItemState.AddItem -> AddItem(modifier = modifier, onClick = data.onClick)
        is CollectionItemState.MysteryBox -> MysteryBox(modifier, data)
    }
}

@Composable
private fun Nft(
    modifier: Modifier,
    data: CollectionItemState.Nft,
) {
    Column(modifier) {
        Container(Modifier) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        color = AppTheme.specificColorScheme.uiContentBg,
                        shape = AppTheme.shapes.small,
                    ),
            ) {
                Image(
                    painter = data.image.get(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    contentScale = ContentScale.Crop,
                )
                if (!data.isHealthy) NeedToRepairMessage()
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = data.type.name,
                style = AppTheme.specificTypography.labelMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Line(
                name = LocalStringHolder.current(StringKey.RankSelectionTitleDailyReward),
                value = data.dailyReward,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Line(
                name = LocalStringHolder.current(StringKey.RankSelectionTitleDailyUnlock),
                value = data.dailyUnlock,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Line(
                name = LocalStringHolder.current(StringKey.RankSelectionTitleDailyConsumption),
                value = data.dailyConsumption,
            )
        }
        if (!data.isHealthy) RepairButton(onClick = data.onRepairClicked)
    }
}

@Composable
private fun NeedToRepairMessage() {
    Text(
        text = StringKey.MyCollectionFieldNeedToRepair.textValue().get(),
        color = AppTheme.specificColorScheme.white,
        style = AppTheme.specificTypography.labelMedium,
        modifier = Modifier
            .padding(8.dp)
            .background(
                color = AppTheme.specificColorScheme.pink,
                shape = AppTheme.shapes.medium,
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
    )
}

@Composable
private fun RepairButton(
    onClick: () -> Unit,
) {
    SimpleCard(
        color = AppTheme.specificColorScheme.white,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .padding(top = 12.dp)
            .defaultTileRipple(onClick = onClick)
    ) {
        Text(
            text = StringKey.MyCollectionActionRepairGlasses.textValue().get(),
            color = AppTheme.specificColorScheme.pink,
            style = AppTheme.specificTypography.labelMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun MysteryBox(
    modifier: Modifier,
    data: CollectionItemState.MysteryBox,
) {
    Container(modifier) {
        Card(
            shape = AppTheme.shapes.small,
            colors = CardDefaults.cardColors(
                containerColor = AppTheme.specificColorScheme.uiContentBg,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(156.dp),
        ) {
            Image(
                painter = data.image.get(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                contentScale = ContentScale.Crop,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = LocalStringHolder.current(StringKey.MyCollectionTitleMysteryBox),
            style = AppTheme.specificTypography.labelMedium,
            modifier = modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun Shimmer(
    modifier: Modifier,
) {
    Container(modifier) {
        ShimmerTile(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            shape = AppTheme.shapes.small,
        )
        Spacer(modifier = Modifier.height(8.dp))
        repeat(3) {
            MiddlePart.Shimmer(needValueLine = true).Content(modifier = Modifier)
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun Line(name: String, value: String) {
    Row {
        Text(
            text = name,
            color = AppTheme.specificColorScheme.textSecondary,
            style = AppTheme.specificTypography.bodySmall,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            modifier = Modifier.padding(start = 4.dp),
            style = AppTheme.specificTypography.bodySmall,
        )
    }
}

@Composable
private fun Container(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .heightIn(min = 220.dp)
            .background(
                color = AppTheme.specificColorScheme.white,
                shape = AppTheme.shapes.medium,
            )
            .border(
                width = 1.dp,
                color = AppTheme.specificColorScheme.darkGrey.copy(alpha = 0.5f),
                shape = AppTheme.shapes.medium,
            )
            .padding(12.dp),
        content = content,
    )
}

@Composable
private fun AddItem(
    modifier: Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .height(220.dp)
            .background(
                color = Color.Transparent,
                shape = AppTheme.shapes.medium,
            )
            .border(
                width = 1.dp,
                color = AppTheme.specificColorScheme.darkGrey.copy(alpha = 0.5f),
                shape = AppTheme.shapes.medium,
            )
            .defaultTileRipple(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = AppTheme.specificIcons.add.get(),
            contentDescription = null,
            tint = AppTheme.specificColorScheme.uiAccent,
            modifier = Modifier.size(32.dp),
        )
    }
}