package io.snaps.coreuicompose.uikit.duplicate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import io.snaps.corecommon.container.IconValue
import io.snaps.corecommon.container.TextValue
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.insetAllExcludeBottom
import io.snaps.coreuitheme.compose.AppTheme

object SimpleTopAppBarConfig {

    private const val ContainerColorAlpha = .9f
    val ActionSize = 44.dp

    @Composable
    fun surfaceColors() = SimpleTopAppBarColors(
        containerColor = AppTheme.specificColorScheme.uiContentBg,
        scrolledContainerColor = AppTheme.specificColorScheme.uiContentBg,
        navigationIconContentColor = AppTheme.specificColorScheme.textPrimary,
        titleContentColor = AppTheme.specificColorScheme.textPrimary,
        actionIconContentColor = AppTheme.specificColorScheme.textPrimary,
    )

    @Composable
    fun transparentSurfaceColors() = SimpleTopAppBarColors(
        containerColor = AppTheme.specificColorScheme.uiContentBg.copy(alpha = ContainerColorAlpha),
        scrolledContainerColor = AppTheme.specificColorScheme.uiContentBg,
        navigationIconContentColor = AppTheme.specificColorScheme.textPrimary,
        titleContentColor = AppTheme.specificColorScheme.textPrimary,
        actionIconContentColor = AppTheme.specificColorScheme.textPrimary,
    )

    @Composable
    fun surfaceVariantColors() = SimpleTopAppBarColors(
        containerColor = AppTheme.specificColorScheme.uiContentBg,
        scrolledContainerColor = AppTheme.specificColorScheme.uiContentBg,
        navigationIconContentColor = AppTheme.specificColorScheme.textPrimary,
        titleContentColor = AppTheme.specificColorScheme.textPrimary,
        actionIconContentColor = AppTheme.specificColorScheme.textPrimary,
    )

    @Composable
    fun primaryContainerColors() = SimpleTopAppBarColors(
        containerColor = AppTheme.specificColorScheme.actionBase,
        scrolledContainerColor = AppTheme.specificColorScheme.actionBase,
        navigationIconContentColor = AppTheme.specificColorScheme.actionLabel,
        titleContentColor = AppTheme.specificColorScheme.actionLabel,
        actionIconContentColor = AppTheme.specificColorScheme.actionLabel,
    )

    @Composable
    fun transparentColors() = SimpleTopAppBarColors(
        containerColor = AppTheme.specificColorScheme.transparent,
        scrolledContainerColor = AppTheme.specificColorScheme.transparent,
        navigationIconContentColor = AppTheme.specificColorScheme.textPrimary,
        titleContentColor = AppTheme.specificColorScheme.textPrimary,
        actionIconContentColor = AppTheme.specificColorScheme.darkGrey,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTopAppBar(
    title: TextValue?,
    titleTextStyle: TextStyle = AppTheme.specificTypography.titleMedium,
    titleHorizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
    navigationIcon: Pair<IconValue, OnBackIconClick>? = null,
    actions: List<ActionIconData> = emptyList(),
    windowInsets: WindowInsets = insetAllExcludeBottom(),
    colors: SimpleTopAppBarColors = SimpleTopAppBarConfig.transparentSurfaceColors(),
    scrollBehavior: TopAppBarScrollBehavior,
    additional: @Composable () -> Unit = {},
) {
    SimpleTopAppBar(
        title = { title?.get()?.let { Text(it) } },
        titleHorizontalArrangement = titleHorizontalArrangement,
        titleTextStyle = titleTextStyle,
        navigationIcon = navigationIcon,
        actions = actions,
        windowInsets = windowInsets,
        colors = colors,
        scrollBehavior = scrollBehavior,
        additional = additional,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTopAppBar(
    title: @Composable () -> Unit,
    titleHorizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
    titleTextStyle: TextStyle = AppTheme.specificTypography.titleMedium,
    navigationIcon: Pair<IconValue, OnBackIconClick>? = null,
    actions: List<ActionIconData> = emptyList(),
    windowInsets: WindowInsets = insetAllExcludeBottom(),
    colors: SimpleTopAppBarColors = SimpleTopAppBarConfig.transparentSurfaceColors(),
    scrollBehavior: TopAppBarScrollBehavior,
    additional: @Composable () -> Unit = {},
) {
    SingleRowTopAppBar(
        title = title,
        titleHorizontalArrangement = titleHorizontalArrangement,
        titleTextStyle = titleTextStyle,
        navigationIcon = {
            if (navigationIcon != null) {
                TopAppBarNavIcon(
                    iconValue = navigationIcon.first,
                    onClick = { navigationIcon.second() },
                )
            }
        },
        actions = {
            actions.toList().forEach { TopAppBarActionIcon(data = it) }
        },
        windowInsets = windowInsets,
        colors = colors,
        scrollBehavior = scrollBehavior,
        additional = additional,
    )
}