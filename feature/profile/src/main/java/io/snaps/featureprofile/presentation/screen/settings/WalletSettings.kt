package io.snaps.featureprofile.presentation.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAllExcludeTop
import io.snaps.coreuicompose.uikit.dialog.DiamondDialog
import io.snaps.coreuicompose.uikit.dialog.DiamondDialogButtonData
import io.snaps.coreuicompose.uikit.duplicate.SimpleTopAppBar
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.LocalStringHolder
import io.snaps.featureprofile.ScreenNavigator
import io.snaps.featureprofile.presentation.viewmodel.WalletSettingsViewModel

@Composable
fun WalletSettingsScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<WalletSettingsViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    viewModel.command.collectAsCommand {
        when (it) {
            WalletSettingsViewModel.Command.OpenBackupWalletKeyScreen -> router.toBackupWalletKeyScreen()
        }
    }

    WalletSettingsScreen(
        uiState = uiState,
        onBackClicked = router::back,
        onDismissRequest = viewModel::onDismissRequest,
        onCloseDialogButtonClicked = viewModel::onCloseDialogButtonClicked,
        onLookButtonClicked = viewModel::onLookButtonClicked,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WalletSettingsScreen(
    uiState: WalletSettingsViewModel.UiState,
    onBackClicked: () -> Boolean,
    onDismissRequest: () -> Unit,
    onCloseDialogButtonClicked: () -> Unit,
    onLookButtonClicked: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = AppTheme.specificColorScheme.uiContentBg,
        topBar = {
            SimpleTopAppBar(
                title = {
                    Text(text = LocalStringHolder.current(StringKey.WalletSettingsTitle))
                },
                navigationIcon = AppTheme.specificIcons.back to onBackClicked,
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp),
            modifier = Modifier
                .padding(paddingValues)
                .inset(insetAllExcludeTop()),
        ) {
            items(uiState.items) { item ->
                item.Content(
                    modifier = Modifier
                        .shadow(shape = AppTheme.shapes.medium, elevation = 16.dp)
                        .background(
                            color = AppTheme.specificColorScheme.white,
                            shape = AppTheme.shapes.medium,
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                )
            }
        }
        if (uiState.isDialogVisibility) {
            DiamondDialog(
                title = StringKey.WalletSettingsBackupDialogTitle.textValue(),
                message = StringKey.WalletSettingsBackupDialogMessage.textValue(),
                onDismissRequest = onDismissRequest,
                primaryButton = DiamondDialogButtonData(
                    text = StringKey.WalletSettingsBackupDialogAction.textValue(),
                    onClick = onLookButtonClicked,
                ),
                secondaryButton = DiamondDialogButtonData(
                    text = StringKey.ReferralProgramDialogActionClose.textValue(),
                    onClick = onCloseDialogButtonClicked,
                ),
                content = {},
            )
        }
    }
}