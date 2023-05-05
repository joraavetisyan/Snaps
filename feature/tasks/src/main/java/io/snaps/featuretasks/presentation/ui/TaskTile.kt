package io.snaps.featuretasks.presentation.ui

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
import androidx.compose.ui.unit.dp
import io.snaps.baseprofile.ui.EnergyWidget
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.tools.TileState
import io.snaps.coreuicompose.tools.defaultTileRipple
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.listtile.MessageBannerState
import io.snaps.coreuicompose.uikit.listtile.MiddlePart
import io.snaps.coreuicompose.uikit.other.SimpleCard
import io.snaps.coreuitheme.compose.AppTheme

enum class TaskStatus {
    Credited,
    InProgress,
    Rejected,
    NotPosted,
    WaitForVerification,
    NotSendToVerify,
}

sealed class TaskTileState : TileState {

    data class Data(
        val title: TextValue,
        val description: TextValue,
        val energy: Int,
        val energyProgress: Int,
        val status: TaskStatus?,
        val clickListener: () -> Unit,
    ) : TaskTileState()

    object Shimmer : TaskTileState()

    data class Error(val clickListener: () -> Unit) : TaskTileState()

    @Composable
    override fun Content(modifier: Modifier) {
        TaskTile(modifier, this)
    }
}

@Composable
fun TaskTile(
    modifier: Modifier = Modifier,
    data: TaskTileState,
) {
    when (data) {
        is TaskTileState.Data -> Data(modifier, data)
        TaskTileState.Shimmer -> Shimmer(modifier)
        is TaskTileState.Error -> MessageBannerState
            .defaultState(onClick = data.clickListener)
            .Content(modifier = modifier)
    }
}

@Composable
private fun Data(
    modifier: Modifier = Modifier,
    data: TaskTileState.Data,
) {
    Container(
        modifier = modifier.defaultTileRipple(onClick = data.clickListener),
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
            TaskStatusMessage(taskStatus = data.status)
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun TaskStatusMessage(
    taskStatus: TaskStatus,
) {
    val color = when (taskStatus) {
        TaskStatus.Credited -> AppTheme.specificColorScheme.uiSystemGreen
        TaskStatus.Rejected -> AppTheme.specificColorScheme.uiSystemRed
        else -> AppTheme.specificColorScheme.uiSystemYellow
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .background(
                color = color.copy(alpha = 0.2f),
                shape = AppTheme.shapes.medium,
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            painter = when (taskStatus) {
                TaskStatus.Credited -> AppTheme.specificIcons.checkCircle
                else -> AppTheme.specificIcons.infoRounded
            }.get(),
            contentDescription = null,
            tint = color,
        )
        Text(
            text = when (taskStatus) {
                TaskStatus.Credited -> StringKey.TasksMessageTaskCounted
                TaskStatus.Rejected -> StringKey.TasksMessageSocialPostRejected
                TaskStatus.InProgress -> StringKey.TasksMessageTaskInProgress
                TaskStatus.NotPosted -> StringKey.TasksMessageSocialPostNotPosted
                TaskStatus.WaitForVerification -> StringKey.TasksMessageSocialPostReview
                TaskStatus.NotSendToVerify -> StringKey.TasksMessageSocialPostNotSendToVerify
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