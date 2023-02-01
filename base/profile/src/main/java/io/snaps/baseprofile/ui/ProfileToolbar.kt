@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package io.snaps.baseprofile.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.snaps.corecommon.container.IconValue
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.State
import io.snaps.coreuicompose.tools.defaultTileRipple
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.insetAllExcludeBottom
import io.snaps.coreuicompose.uikit.duplicate.ActionIconData
import io.snaps.coreuicompose.uikit.duplicate.SimpleTopAppBarColors
import io.snaps.coreuicompose.uikit.duplicate.SimpleTopAppBarConfig
import io.snaps.coreuicompose.uikit.duplicate.SingleRowTopAppBar
import io.snaps.coreuicompose.uikit.duplicate.TopAppBarActionIcon
import io.snaps.coreuicompose.uikit.other.ShimmerTileCircle
import io.snaps.coreuicompose.uikit.other.ShimmerTileConfig
import io.snaps.coreuicompose.uikit.other.ShimmerTileLine
import io.snaps.coreuitheme.compose.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileToolbar(
    name: State<String>,
    navigateIcon: IconValue = AppTheme.specificIcons.navigateNext,
    scrollBehavior: TopAppBarScrollBehavior,
    colors: SimpleTopAppBarColors = SimpleTopAppBarConfig.transparentColors(),
    onNameClicked: (() -> Unit)? = null,
    firstAction: ActionIconData? = null,
    secondAction: ActionIconData? = null,
) {
    val nameValue = (name as? Effect)?.dataOrCache
    ProfileToolbarContainer(scrollBehavior = scrollBehavior, colors = colors) {
        Row(
            modifier = Modifier
                .defaultTileRipple(enable = nameValue != null, onClick = onNameClicked)
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
//            ProfileToolbarAvatar(nameValue)
            ProfileToolbarName(nameValue)
            if (onNameClicked != null && nameValue != null) ProfileToolbarNavigateIcon(navigateIcon)
        }
        if (nameValue != null && (firstAction != null || secondAction != null)) {
            firstAction?.let { TopAppBarActionIcon(it) }
            secondAction?.let { TopAppBarActionIcon(it) }
        }
    }
}

@Composable
fun ProfileToolbar(
    name: State<String>,
    scrollBehavior: TopAppBarScrollBehavior,
    colors: SimpleTopAppBarColors = SimpleTopAppBarConfig.transparentColors(),
    onNameClicked: (() -> Unit)? = null,
    firstAction: ActionIconData? = null,
    secondAction: ActionIconData? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val nameValue = (name as? Effect)?.dataOrCache
    val (leftWidth, rightWidth) = when {
        firstAction == null && secondAction == null -> when {
            nameValue != null -> 0.dp to ProfileToolbarConfig.AvatarSize
            else -> 0.dp to 0.dp
        }
        firstAction != null && secondAction != null -> {
            (SimpleTopAppBarConfig.ActionSize * 2 - ProfileToolbarConfig.AvatarSize) to 0.dp
        }
        else -> {
            0.dp to 0.dp
        }
    }
    ProfileToolbarContainer(scrollBehavior = scrollBehavior, colors = colors) {
        Box(
            modifier = Modifier.defaultTileRipple(
                enable = nameValue != null,
                onClick = onNameClicked
            )
        ) {
            ProfileToolbarAvatar(nameValue)
        }
        Spacer(modifier = Modifier.width(leftWidth))
        content()
        Spacer(modifier = Modifier.width(rightWidth))
        firstAction?.let { TopAppBarActionIcon(it) }
        secondAction?.let { TopAppBarActionIcon(it) }
    }
}

@Composable
fun ProfileToolbarContainer(
    colors: SimpleTopAppBarColors = SimpleTopAppBarConfig.surfaceColors(),
    scrollBehavior: TopAppBarScrollBehavior,
    windowInsets: WindowInsets = insetAllExcludeBottom(),
    content: @Composable RowScope.() -> Unit,
) {
    SingleRowTopAppBar(
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = content,
            )
        },
        windowInsets = windowInsets,
        colors = colors,
        scrollBehavior = scrollBehavior,
        additional = {},
    )
}

@Composable
private fun ProfileToolbarAvatar(name: String?) {
    when (name) {
        null -> ShimmerTileCircle(size = ProfileToolbarConfig.AvatarSize)
        else -> Text(
            text = name,
            modifier = Modifier.size(ProfileToolbarConfig.AvatarSize),
        )
    }
}

@Composable
private fun ProfileToolbarName(name: String?) {
    val modifier = Modifier.padding(start = 12.dp)

    when (name) {
        null -> Box(modifier = modifier) {
            ShimmerTileLine(width = ShimmerTileConfig.WidthMedium)
        }
        else -> Text(
            text = name,
            modifier = modifier,
            style = AppTheme.specificTypography.bodyMedium,
            color = AppTheme.specificColorScheme.textPrimary,
        )
    }
}

@Composable
private fun ProfileToolbarNavigateIcon(navigateIcon: IconValue) {
    Icon(
        painter = navigateIcon.get(),
        contentDescription = null,
        tint = AppTheme.specificColorScheme.textSecondary,
        modifier = Modifier.padding(top = 2.dp, start = 12.dp),
    )
}

object ProfileToolbarConfig {
    val AvatarSize = 40.dp
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun Preview() {
}