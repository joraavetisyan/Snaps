package io.snaps.featurecollection.presentation.screen

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.baseprofile.ui.ValueWidget
import io.snaps.corecommon.R
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.ext.toPercentageFormat
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAllExcludeTop
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionM
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.duplicate.SimpleTopAppBar
import io.snaps.coreuicompose.uikit.status.FullScreenLoaderUi
import io.snaps.coreuicompose.uikit.status.InfoBlock
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featurecollection.ScreenNavigator
import io.snaps.featurecollection.presentation.viewmodel.PurchaseViewModel

@Composable
fun PurchaseScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<PurchaseViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    viewModel.command.collectAsCommand {
        when (it) {
            PurchaseViewModel.Command.ClosePurchaseScreen -> router.back()
        }
    }

    PurchaseScreen(
        uiState = uiState,
        onBackClicked = router::back,
        onBuyClicked = { viewModel.onBuyClicked(context as Activity) },
    )

    FullScreenLoaderUi(isLoading = uiState.isLoading)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PurchaseScreen(
    uiState: PurchaseViewModel.UiState,
    onBuyClicked: () -> Unit,
    onBackClicked: () -> Boolean,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SimpleTopAppBar(
                title = StringKey.PurchaseTitle.textValue(),
                scrollBehavior = scrollBehavior,
                navigationIcon = AppTheme.specificIcons.back to onBackClicked,
            )
        },
        floatingActionButton = {
            if (uiState.isAvailableToPurchase) {
                SimpleButtonActionM(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    onClick = onBuyClicked,
                ) {
                    SimpleButtonContent(text = StringKey.PurchaseAction.textValue())
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .inset(insetAllExcludeTop())
                .padding(horizontal = 12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Image(
                    painter = uiState.nftImage.get(),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = uiState.nftType.name,
                        style = AppTheme.specificTypography.labelMedium,
                    )
                    if (uiState.isAvailableToPurchase) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = StringKey.PurchaseTitlePrice.textValue().get(),
                                style = AppTheme.specificTypography.labelSmall,
                                color = AppTheme.specificColorScheme.textSecondary,
                            )
                            ValueWidget(ImageValue.ResImage(R.drawable.img_coin_silver) to uiState.cost)
                        }
                    } else {
                        Text(
                            text = StringKey.PurchaseTitleNotAvailable.textValue().get(),
                            style = AppTheme.specificTypography.labelSmall,
                            color = AppTheme.specificColorScheme.textSecondary,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            CardBlock(
                title = StringKey.PurchaseTitleDailyReward.textValue(),
                description = StringKey.PurchaseDescriptionDailyReward.textValue(),
                message = StringKey.PurchaseMessageDailyReward.textValue(),
            )
            Spacer(modifier = Modifier.height(12.dp))
            CardBlock(
                title = StringKey.PurchaseTitleDailyUnlock.textValue(),
                description = StringKey.PurchaseDescriptionDailyUnlock.textValue(
                    uiState.dailyUnlock.toPercentageFormat()
                ),
                message = StringKey.PurchaseMessageDailyUnlock.textValue(),
            )
        }
    }
}

@Composable
fun CardBlock(
    title: TextValue,
    description: TextValue,
    message: TextValue,
) {
    Card(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = AppTheme.specificColorScheme.darkGrey.copy(alpha = 0.5f),
                shape = AppTheme.shapes.medium,
            )
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .background(
                color = AppTheme.specificColorScheme.white,
                shape = AppTheme.shapes.medium,
            ),
    ) {
        Text(
            text = title.get(),
            style = AppTheme.specificTypography.labelMedium,
        )
        Text(
            text = description.get(),
            style = AppTheme.specificTypography.labelSmall,
            color = AppTheme.specificColorScheme.textSecondary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 12.dp)
        )
        InfoBlock(message = message)
    }
}