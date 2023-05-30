package io.snaps.featureprofile.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreuicompose.tools.TileState
import io.snaps.coreuicompose.tools.defaultTileRipple
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.uikit.other.ShimmerTileCircle
import io.snaps.coreuicompose.uikit.other.ShimmerTileLine
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.LocalStringHolder

sealed class UserInfoTileState : TileState {

    data class Data(
        val profileImage: ImageValue?,
        val name: String,
        val likes: String,
        val subscribers: Int,
        val subscriptions: Int,
        val publication: String?,
        val isCurrentUser: Boolean,
        val onSubscribersClick: () -> Unit,
        val onSubscriptionsClick: () -> Unit,
    ) : UserInfoTileState()

    object Shimmer : UserInfoTileState()

    @Composable
    override fun Content(modifier: Modifier) {
        UserInfoTile(modifier = modifier, data = this)
    }
}

@Composable
fun UserInfoTile(
    modifier: Modifier = Modifier,
    data: UserInfoTileState,
) {
    when (data) {
        is UserInfoTileState.Data -> Data(modifier, data)
        is UserInfoTileState.Shimmer -> Shimmer(modifier)
    }
}

@Composable
private fun Data(
    modifier: Modifier = Modifier,
    data: UserInfoTileState.Data,
) {
    Container(modifier) {
        if (data.profileImage != null) {
            Image(
                painter = data.profileImage.get(),
                contentDescription = null,
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
        } else {
            ShimmerTileCircle(size = 76.dp)
        }
        if (data.isCurrentUser) {
            Text(
                text = data.name,
                style = AppTheme.specificTypography.titleSmall,
                color = AppTheme.specificColorScheme.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
            )
            Text(
                text = StringKey.OnboardingRankTitle.textValue().get(),
                style = AppTheme.specificTypography.titleSmall,
                color = AppTheme.specificColorScheme.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
            )
        }
        InfoContainer {
            StatsLine(data.likes, LocalStringHolder.current(StringKey.ProfileTitleLikes))
            VerticalDivider()
            StatsLine(
                value = data.subscriptions.toString(),
                name = LocalStringHolder.current(StringKey.ProfileTitleSubscriptions),
                onClick = data.onSubscriptionsClick
            )
            VerticalDivider()
            StatsLine(
                value = data.subscribers.toString(),
                name = LocalStringHolder.current(StringKey.ProfileTitleSubscribers),
                onClick = data.onSubscribersClick
            )
            data.publication?.let {
                VerticalDivider()
                StatsLine(it, LocalStringHolder.current(StringKey.ProfileTitlePublication))
            }
        }
    }
}

@Composable
private fun Shimmer(
    modifier: Modifier = Modifier,
) {
    Container(modifier) {
        ShimmerTileCircle(
            size = 76.dp,
            modifier = Modifier.padding(bottom = 16.dp),
        )
        InfoContainer {
            repeat(4) { index ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ShimmerTileLine(
                        height = AppTheme.specificTypography.titleMedium.lineHeight.value.dp,
                        width = 40.dp,
                    )
                    ShimmerTileLine(
                        height = AppTheme.specificTypography.bodySmall.lineHeight.value.dp,
                        width = 64.dp,
                    )
                }
                if (index < 3) {
                    VerticalDivider()
                }
            }
        }
    }
}

@Composable
private fun Container(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier
            .background(color = AppTheme.specificColorScheme.white, shape = AppTheme.shapes.medium)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content,
    )
}

@Composable
private fun InfoContainer(
    content: @Composable RowScope.() -> Unit,
) {
    Card(
        shape = AppTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppTheme.specificColorScheme.white)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(vertical = 16.dp)
                .height(IntrinsicSize.Min)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            content = content,
        )
    }
}

@Composable
private fun RowScope.StatsLine(
    value: String,
    name: String,
    onClick: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .defaultTileRipple(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = value, style = AppTheme.specificTypography.titleSmall)
        Text(
            text = name,
            style = AppTheme.specificTypography.bodySmall,
            color = AppTheme.specificColorScheme.textPrimary.copy(alpha = 0.5f),
        )
    }
}

@Composable
private fun VerticalDivider() {
    Divider(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxHeight()
            .width(1.dp)
    )
}