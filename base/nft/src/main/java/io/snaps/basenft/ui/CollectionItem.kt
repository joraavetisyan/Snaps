package io.snaps.basenft.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.FiatValue
import io.snaps.corecommon.model.NftType
import io.snaps.corecommon.strings.StringKey
import io.snaps.corecommon.strings.approximated
import io.snaps.coreuicompose.tools.TileState
import io.snaps.coreuicompose.tools.defaultTileRipple
import io.snaps.coreuicompose.tools.doOnClick
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.listtile.MessageBannerState
import io.snaps.coreuicompose.uikit.listtile.MiddlePart
import io.snaps.coreuicompose.uikit.other.Progress
import io.snaps.coreuicompose.uikit.other.ShimmerTile
import io.snaps.coreuicompose.uikit.other.SimpleCard
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.LocalStringHolder

sealed class CollectionItemState : TileState {

    data class Nft(
        val type: NftType,
        val image: ImageValue,
        val dailyReward: FiatValue,
        val dailyUnlock: String,
        val dailyConsumption: String,
        val isHealthBadgeVisible: Boolean,
        val isRepairable: Boolean,
        val level: Int,
        val experience: Int,
        val bonus: Int,
        val upperThreshold: Int,
        val lowerThreshold: Int,
        val isLevelVisible: Boolean,
        val onRepairClicked: () -> Unit,
        val onItemClicked: () -> Unit,
        val onHelpIconClicked: () -> Unit,
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
        Container(clickListener = data.onItemClicked) {
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
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop,
                )
                if (data.isHealthBadgeVisible) NeedToRepairMessage()
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = data.type.displayName,
                style = AppTheme.specificTypography.labelMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Line(
                name = LocalStringHolder.current(StringKey.RankSelectionTitleDailyReward),
                value = data.dailyReward.getFormatted().approximated,
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
        if (data.isLevelVisible) {
            LevelInfoBlock(
                experience = data.experience,
                upperThreshold = data.upperThreshold,
                level = data.level,
                lowerThreshold = data.lowerThreshold,
                bonus = data.bonus,
                onHelpIconClicked = data.onHelpIconClicked,
            )
        }
        if (data.isRepairable) {
            Button(
                text = StringKey.MyCollectionActionRepairGlasses.textValue(),
                textColor = AppTheme.specificColorScheme.pink,
                onClick = data.onRepairClicked,
            )
        }
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
private fun Button(
    text: TextValue,
    textColor: Color,
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
            text = text.get(),
            color = textColor,
            style = AppTheme.specificTypography.labelMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun LevelInfoBlock(
    experience: Int,
    upperThreshold: Int,
    level: Int,
    lowerThreshold: Int,
    bonus: Int,
    onHelpIconClicked: () -> Unit,
) {
    val upperThresholdText = buildAnnotatedString {
        val nextLevel = StringKey.MyCollectionFieldLevel.textValue(
            (level + 1).toString()
        ).get().text
        val text = StringKey.MyCollectionFieldUpperThreshold.textValue(
            (upperThreshold - experience).toString(),
            nextLevel,
        ).get().text
        append(text)
        val startIndex = text.indexOf(nextLevel)
        val endIndex = startIndex + nextLevel.length
        addStyle(
            style = SpanStyle(
                color = AppTheme.specificColorScheme.textLink,
                fontSize = AppTheme.specificTypography.labelSmall.fontSize,
            ),
            start = startIndex,
            end = endIndex,
        )
    }

    SimpleCard(
        color = AppTheme.specificColorScheme.white,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "XP",
                style = AppTheme.specificTypography.labelMedium,
                color = AppTheme.specificColorScheme.textLink,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .border(
                        width = 2.dp,
                        color = AppTheme.specificColorScheme.uiAccent,
                        shape = CircleShape,
                    )
                    .size(40.dp)
                    .wrapContentHeight(align = Alignment.CenterVertically),
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = StringKey.MyCollectionFieldExperience.textValue(experience).get(),
                    style = AppTheme.specificTypography.labelSmall,
                    color = AppTheme.specificColorScheme.textSecondary,
                )
                Progress(
                    modifier = Modifier.fillMaxWidth(),
                    height = 12.dp,
                    progress = experience / upperThreshold.toFloat(),
                    isDashed = false,
                    backColor = AppTheme.specificColorScheme.darkGrey,
                    fillColor = AppTheme.specificColorScheme.uiAccent,
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = upperThresholdText,
            style = AppTheme.specificTypography.labelSmall.copy(fontSize = 10.sp),
            color = AppTheme.specificColorScheme.textSecondary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            textAlign = TextAlign.End,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                painter = AppTheme.specificIcons.help.get(),
                contentDescription = null,
                tint = AppTheme.specificColorScheme.uiAccent,
                modifier = Modifier
                    .defaultTileRipple(shape = CircleShape, onClick = onHelpIconClicked)
                    .size(20.dp),
            )
            Text(
                text = StringKey.MyCollectionFieldBonus.textValue(bonus.toString()).get(),
                style = AppTheme.specificTypography.bodySmall,
                color = AppTheme.specificColorScheme.textLink,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp),
            )
        }
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
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = name,
            color = AppTheme.specificColorScheme.textSecondary,
            style = AppTheme.specificTypography.bodySmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = value,
            modifier = Modifier.padding(start = 4.dp),
            style = AppTheme.specificTypography.bodySmall,
            maxLines = 1,
        )
    }
}

@Composable
private fun Container(
    modifier: Modifier = Modifier,
    clickListener: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .heightIn(min = 220.dp)
            .shadow(elevation = 16.dp, shape = AppTheme.shapes.medium)
            .background(
                color = AppTheme.specificColorScheme.white,
                shape = AppTheme.shapes.medium,
            )
            .doOnClick(enable = clickListener != null, onClick = clickListener)
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