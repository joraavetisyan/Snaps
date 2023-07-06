package io.snaps.featurequests.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.unit.dp
import io.snaps.baseprofile.ui.EnergyWidget
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.tools.TileState
import io.snaps.coreuicompose.tools.addIf
import io.snaps.coreuicompose.tools.defaultTileRipple
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.listtile.MessageBannerState
import io.snaps.coreuicompose.uikit.listtile.MiddlePart
import io.snaps.coreuicompose.uikit.other.SimpleCard
import io.snaps.coreuitheme.compose.AppTheme

enum class QuestStatus {
    Credited,
    InProgress,
    Rejected,
    NotPosted,
    WaitForVerification,
    NotSendToVerify,
}

sealed class QuestTileState : TileState {

    data class Data(
        val title: TextValue,
        val description: TextValue,
        val energy: Int,
        val energyProgress: Int,
        val status: QuestStatus?,
        val clickListener: () -> Unit,
    ) : QuestTileState()

    object Shimmer : QuestTileState()

    data class Error(val clickListener: () -> Unit) : QuestTileState()

    @Composable
    override fun Content(modifier: Modifier) {
        QuestTile(modifier, this)
    }
}

@Composable
fun QuestTile(
    modifier: Modifier = Modifier,
    data: QuestTileState,
) {
    when (data) {
        is QuestTileState.Data -> Data(modifier, data)
        QuestTileState.Shimmer -> Shimmer(modifier)
        is QuestTileState.Error -> MessageBannerState
            .defaultState(onClick = data.clickListener)
            .Content(modifier = modifier)
    }
}

@Composable
private fun Data(
    modifier: Modifier = Modifier,
    data: QuestTileState.Data,
) {
    Container(
        modifier = modifier
            .defaultTileRipple(onClick = data.clickListener)
            .addIf(data.status == QuestStatus.Credited) {
                drawWithCache {
                    onDrawWithContent {
                        drawContent()
//                        drawRect(color = Color.White.copy(alpha = 0.1f))
                    }
                }
            },
    ) {
        Row(
            modifier = modifier
                .padding(horizontal = 12.dp)
                .padding(top = 12.dp),
        ) {
            Column(
                modifier = modifier
                    .weight(1f)
                    .padding(end = 8.dp),
            ) {
                Text(
                    text = data.title.get(),
                    color = AppTheme.specificColorScheme.textPrimary,
                    style = AppTheme.specificTypography.labelMedium,
                )
                Text(
                    text = data.description.get(),
                    color = AppTheme.specificColorScheme.textSecondary,
                    style = AppTheme.specificTypography.labelSmall,
                )
            }
            EnergyWidget(
                value = "${data.energyProgress}/${data.energy}",
                isFull = data.energyProgress == data.energy,
            )
        }
        data.status?.let {
            Spacer(modifier = Modifier.height(12.dp))
            QuestStatusMessage(taskStatus = data.status)
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun QuestStatusMessage(
    taskStatus: QuestStatus,
) {
    val color = when (taskStatus) {
        QuestStatus.Credited -> AppTheme.specificColorScheme.uiSystemGreen
        QuestStatus.Rejected -> AppTheme.specificColorScheme.uiSystemRed
        else -> AppTheme.specificColorScheme.uiSystemYellow
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .background(
                color = color.copy(alpha = 0.1f),
                shape = AppTheme.shapes.medium,
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            painter = when (taskStatus) {
                QuestStatus.Credited -> AppTheme.specificIcons.checkCircle
                else -> AppTheme.specificIcons.infoRounded
            }.get(),
            contentDescription = null,
            tint = color,
        )
        Text(
            text = when (taskStatus) {
                QuestStatus.Credited -> StringKey.TasksMessageTaskCounted
                QuestStatus.Rejected -> StringKey.TasksMessageSocialPostRejected
                QuestStatus.InProgress -> StringKey.TasksMessageTaskInProgress
                QuestStatus.NotPosted -> StringKey.TasksMessageSocialPostNotPosted
                QuestStatus.WaitForVerification -> StringKey.TasksMessageSocialPostReview
                QuestStatus.NotSendToVerify -> StringKey.TasksMessageSocialPostNotSendToVerify
            }.textValue().get(),
            color = color,
            style = AppTheme.specificTypography.bodySmall,
        )
    }
}

@Composable
private fun Shimmer(
    modifier: Modifier = Modifier,
) {
    Container(modifier = modifier) {
        MiddlePart.Shimmer(
            needValueLine = true,
            needDescriptionLine = true,
        ).Content(modifier = Modifier.padding(12.dp))
    }
}

@Composable
private fun Container(
    modifier: Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    SimpleCard(
        modifier = modifier.fillMaxWidth(),
        color = AppTheme.specificColorScheme.white,
        content = content,
    )
}