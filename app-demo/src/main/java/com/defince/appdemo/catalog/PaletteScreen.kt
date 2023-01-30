package com.defince.appdemo.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.android.material.color.MaterialColors
import com.defince.corecommon.container.textValue
import com.defince.coreuicompose.tools.TileState
import com.defince.coreuicompose.tools.add
import com.defince.coreuicompose.tools.insetAllExcludeTop
import com.defince.coreuicompose.uikit.listtile.HeaderTileState
import com.defince.coreuicompose.uikit.duplicate.SimpleTopAppBar
import com.defince.coreuitheme.compose.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaletteScreen(navController: NavHostController) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val items = items()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SimpleTopAppBar(
                title = "Palette".textValue(),
                navigationIcon = AppTheme.specificIcons.back to navController::popBackStack,
                scrollBehavior = scrollBehavior,
            )
        }
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxHeight(),
            contentPadding = insetAllExcludeTop().asPaddingValues().add(horizontal = 24.dp).add(it),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(
                items = items,
                span = { if (it is HeaderTileState) GridItemSpan(2) else GridItemSpan(1) },
            ) {
                it.Content(modifier = Modifier)
            }
        }
    }
}

@Composable
private fun items() = listOf(
    HeaderTileState.Data(value = "Ui".textValue(), type = HeaderTileState.Type.Small),
    ColorItem(AppTheme.specificColorScheme.uiAccent, "uiAccent"),
    ColorItem(AppTheme.specificColorScheme.uiContentBg, "uiContentBg"),
    ColorItem(AppTheme.specificColorScheme.uiSystemRed, "uiSystemRed"),
    ColorItem(AppTheme.specificColorScheme.uiSystemGreen, "uiSystemGreen"),
    ColorItem(AppTheme.specificColorScheme.uiDisabledLabel, "uiDisabledLabel"),

    HeaderTileState.Data(value = "Text".textValue(), type = HeaderTileState.Type.Small),
    ColorItem(AppTheme.specificColorScheme.textPrimary, "textPrimary"),
    ColorItem(AppTheme.specificColorScheme.textSecondary, "textSecondary"),
    ColorItem(AppTheme.specificColorScheme.textLink, "textLink"),

    HeaderTileState.Data(value = "Grey".textValue(), type = HeaderTileState.Type.Small),
    ColorItem(AppTheme.specificColorScheme.lightGrey, "lightGrey"),
    ColorItem(AppTheme.specificColorScheme.grey, "grey"),
    ColorItem(AppTheme.specificColorScheme.darkGrey, "darkGrey"),

    HeaderTileState.Data(value = "Action".textValue(), type = HeaderTileState.Type.Small),
    ColorItem(AppTheme.specificColorScheme.actionLabel, "actionLabel"),
    ColorItem(AppTheme.specificColorScheme.actionBase, "actionBase"),
    ColorItem(AppTheme.specificColorScheme.actionDisabled, "actionDisabled"),

    HeaderTileState.Data(value = "Light".textValue(), type = HeaderTileState.Type.Small),
    ColorItem(AppTheme.specificColorScheme.lightLabel, "lightLabel"),
    ColorItem(AppTheme.specificColorScheme.lightBase, "lightBase"),
    ColorItem(AppTheme.specificColorScheme.lightDisabled, "lightDisabled"),

    HeaderTileState.Data(value = "Default".textValue(), type = HeaderTileState.Type.Small),
    ColorItem(AppTheme.specificColorScheme.defaultLabel, "defaultLabel"),
    ColorItem(AppTheme.specificColorScheme.defaultBase, "defaultBase"),
    ColorItem(AppTheme.specificColorScheme.defaultDisabled, "defaultDisabled"),

    HeaderTileState.Data(value = "Outline".textValue(), type = HeaderTileState.Type.Small),
    ColorItem(AppTheme.specificColorScheme.outlineLabel, "outlineLabel"),
    ColorItem(AppTheme.specificColorScheme.outlineBorderBase, "outlineBase"),
    ColorItem(AppTheme.specificColorScheme.outlineDisabled, "outlineDisabled"),
)

@OptIn(ExperimentalMaterial3Api::class)
data class ColorItem(
    val color: Color,
    val name: String,
) : TileState {

    @Composable
    override fun Content(modifier: Modifier) {
        OutlinedCard(
            colors = CardDefaults.cardColors(
                containerColor = color,
                contentColor = if (MaterialColors.isColorLight(color.toArgb())) {
                    AppTheme.specificColorScheme.textPrimary
                } else {
                    AppTheme.specificColorScheme.textSecondary
                },
            ),
            modifier = modifier.heightIn(min = 100.dp),
            shape = AppTheme.shapes.extraLarge,
        ) {
            Text(
                text = name,
                style = AppTheme.specificTypography.titleMedium,
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                maxLines = 2,
            )
            Text(
                text = "#%s".format(Integer.toHexString(color.toArgb())).uppercase(),
                style = AppTheme.specificTypography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
            )
        }
    }
}