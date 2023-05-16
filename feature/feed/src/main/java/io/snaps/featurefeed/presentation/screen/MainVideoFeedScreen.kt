package io.snaps.featurefeed.presentation.screen

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.TabRowDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.basefeed.ui.VideoClipScreen
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.baseprofile.ui.MainHeader
import io.snaps.baseprofile.ui.MainHeaderState
import io.snaps.corecommon.model.Uuid
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.defaultTileRipple
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featurefeed.ScreenNavigator
import io.snaps.featurefeed.presentation.viewmodel.MainVideoFeedViewModel
import io.snaps.featurefeed.presentation.viewmodel.SubscriptionsVideoFeedViewModel

@Composable
fun MainVideoFeedScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<MainVideoFeedViewModel>()
    val subscriptionsViewModel = hiltViewModel<SubscriptionsVideoFeedViewModel>()
    val mainHeaderUiState by viewModel.headerUiState.collectAsState()
    val screenState by viewModel.screenState.collectAsState()

    viewModel.headerCommand.collectAsCommand {
        when (it) {
            MainHeaderHandler.Command.OpenProfileScreen -> router.toProfileScreen()
            MainHeaderHandler.Command.OpenWalletScreen -> router.toWalletScreen()
        }
    }

    MainVideoFeedScreen(
        screenState = screenState,
        mainViewModel = viewModel,
        subscriptionsViewModel = subscriptionsViewModel,
        onAuthorClicked = router::toProfileScreen,
        onCreateVideoClicked = router::toCreateVideoScreen,
        mainHeaderState = mainHeaderUiState.value,
        onTabRowClicked = viewModel::onTabRowClicked,
    )
}

@Composable
private fun MainVideoFeedScreen(
    screenState: MainVideoFeedViewModel.UiState,
    mainViewModel: MainVideoFeedViewModel,
    subscriptionsViewModel: SubscriptionsVideoFeedViewModel,
    onAuthorClicked: (Uuid) -> Unit,
    onCreateVideoClicked: () -> Unit,
    mainHeaderState: MainHeaderState,
    onTabRowClicked: (Int) -> Unit,
) {
    @Composable
    fun BoxScope.Header(paddingValues: PaddingValues) {
        Column(
            Modifier
                .align(Alignment.TopCenter)
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            MainHeader(state = mainHeaderState)
            screenState.screen?.let { screen ->
                CustomTabRow(
                    tabs = MainVideoFeedViewModel.VideoClipScreen.values().map { item ->
                        item.label.get().text
                    },
                    selectedTabIndex = screen.ordinal,
                    onClick = { onTabRowClicked(it) },
                )
            }
        }
    }

    when (screenState.screen) {
        MainVideoFeedViewModel.VideoClipScreen.Subscriptions -> VideoClipScreen(
            viewModel = subscriptionsViewModel,
            onAuthorClicked = onAuthorClicked,
            onCreateVideoClicked = onCreateVideoClicked,
            content = { Header(paddingValues = it) }
        )

        MainVideoFeedViewModel.VideoClipScreen.Main -> VideoClipScreen(
            viewModel = mainViewModel,
            onAuthorClicked = onAuthorClicked,
            onCreateVideoClicked = onCreateVideoClicked,
            content = { Header(paddingValues = it) }
        )

        null -> VideoClipScreen(
            viewModel = mainViewModel,
            onAuthorClicked = onAuthorClicked,
            onCreateVideoClicked = onCreateVideoClicked,
            content = { Header(paddingValues = it) }
        )
    }
}

@Composable
private fun CustomTabRow(
    tabs: List<String>,
    selectedTabIndex: Int,
    onClick: (Int) -> Unit,
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val tabWidths = remember {
        val tabWidthStateList = mutableStateListOf<Dp>()
        repeat(tabs.size) {
            tabWidthStateList.add(0.dp)
        }
        tabWidthStateList
    }

    val horizontalPadding = screenWidth - tabWidths.sumOf { it.value.toDouble() }.dp - 20.dp
    val paddingBetweenTabs = 20.dp * selectedTabIndex
    val prevTabsWidth = tabWidths.take(selectedTabIndex).sumOf { it.value.toDouble() }.dp
    val left = horizontalPadding / 2 + prevTabsWidth + paddingBetweenTabs

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            tabs.mapIndexed { index, text ->
                TabItem(
                    isSelected = index == selectedTabIndex,
                    onClick = { onClick(index) },
                    text = text,
                    onTextLayout = {
                        tabWidths[index] = with(density) { it.size.width.toDp() }
                    }
                )
            }
        }
        TabRowDefaults.Indicator(
            modifier = Modifier.customTabIndicatorOffset(
                left = left,
                tabWidth = tabWidths[selectedTabIndex],
            ),
            color = AppTheme.specificColorScheme.white,
        )
    }
}

@Composable
private fun TabItem(
    isSelected: Boolean,
    onClick: () -> Unit,
    text: String,
    onTextLayout: (TextLayoutResult) -> Unit,
) {
    Text(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .defaultTileRipple(onClick = onClick),
        text = text,
        color = if (isSelected) {
            AppTheme.specificColorScheme.white
        } else AppTheme.specificColorScheme.white_70,
        style = AppTheme.specificTypography.labelMedium,
        onTextLayout = { textLayoutResult -> onTextLayout(textLayoutResult) },
    )
}

private fun Modifier.customTabIndicatorOffset(
    left: Dp,
    tabWidth: Dp
): Modifier = composed {
    val padding = 8.dp
    val currentTabWidth by animateDpAsState(
        targetValue = tabWidth - padding * 2,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
    )
    val indicatorOffset by animateDpAsState(
        targetValue = left + padding,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
    )
    fillMaxWidth()
        .wrapContentSize(Alignment.BottomStart)
        .offset(x = indicatorOffset)
        .width(currentTabWidth)
        .height(2.dp)
        .clip(CircleShape)
}