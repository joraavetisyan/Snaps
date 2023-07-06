package io.snaps.featurequests.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.snaps.baseprofile.ui.EnergyWidget
import io.snaps.corecommon.container.IconValue
import io.snaps.corecommon.container.TextValue
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.insetAllExcludeBottom
import io.snaps.coreuicompose.uikit.duplicate.OnBackIconClick
import io.snaps.coreuicompose.uikit.duplicate.SimpleTopAppBarConfig
import io.snaps.coreuicompose.uikit.duplicate.SingleRowTopAppBar
import io.snaps.coreuicompose.uikit.duplicate.TopAppBarNavIcon
import io.snaps.coreuitheme.compose.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestToolbar(
    title: TextValue,
    progress: Int?,
    navigationIcon: Pair<IconValue, OnBackIconClick>? = null,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    SingleRowTopAppBar(
        modifier = Modifier.padding(end = 12.dp),
        title = { Text(title.get()) },
        titleHorizontalArrangement = Arrangement.Center,
        titleTextStyle = AppTheme.specificTypography.headlineSmall,
        navigationIcon = {
            if (navigationIcon != null) {
                TopAppBarNavIcon(
                    iconValue = navigationIcon.first,
                    onClick = { navigationIcon.second() },
                )
            }
        },
        actions = {
            progress?.let {
                EnergyWidget(
                    value = "$it",
                    isFull = false,
                )
            }
        },
        windowInsets = insetAllExcludeBottom(),
        colors = SimpleTopAppBarConfig.transparentSurfaceColors(),
        scrollBehavior = scrollBehavior,
    )
}