package io.snaps.featureprofile.presentation.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.baseprofile.ui.MainHeader
import io.snaps.baseprofile.ui.MainHeaderState
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAll
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featureprofile.ScreenNavigator
import io.snaps.featureprofile.presentation.screen.ButtonData
import io.snaps.featureprofile.presentation.screen.DialogUi
import io.snaps.featureprofile.presentation.viewmodel.WalletSettingsViewModel

@Composable
fun WalletSettingsScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<WalletSettingsViewModel>()

    val uiState by viewModel.uiState.collectAsState()
    val headerState by viewModel.headerUiState.collectAsState()

    viewModel.command.collectAsCommand {
        when (it) {
            WalletSettingsViewModel.Command.OpenBackupWalletKeyScreen -> router.toBackupWalletKeyScreen()
        }
    }

    WalletSettingsScreen(
        uiState = uiState,
        headerState = headerState.value,
        onBackClicked = router::back,
        onCloseDialogButtonClicked = viewModel::onCloseDialogButtonClicked,
        onDismissRequest = viewModel::onDismissRequest,
        onLookButtonClicked = viewModel::onLookButtonClicked,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WalletSettingsScreen(
    uiState: WalletSettingsViewModel.UiState,
    headerState: MainHeaderState,
    onBackClicked: () -> Boolean,
    onDismissRequest: () -> Unit,
    onCloseDialogButtonClicked: () -> Unit,
    onLookButtonClicked: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = AppTheme.specificColorScheme.uiContentBg,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .inset(insetAll()),
        ) {
            MainHeader(state = headerState)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = AppTheme.specificIcons.back.get(),
                    tint = AppTheme.specificColorScheme.darkGrey,
                    contentDescription = null,
                    modifier = Modifier.clickable { onBackClicked() }
                )
                Text(
                    text = StringKey.WalletSettingsTitle.textValue().get(),
                    style = AppTheme.specificTypography.titleLarge,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                )
            }
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp)
            ) {
                items(uiState.items) { item ->
                    item.Content(
                        modifier = Modifier
                            .background(
                                color = AppTheme.specificColorScheme.white,
                                shape = AppTheme.shapes.medium,
                            )
                            .border(
                                width = 1.dp,
                                color = AppTheme.specificColorScheme.darkGrey.copy(alpha = 0.5f),
                                shape = AppTheme.shapes.medium,
                            )
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                    )
                }
            }
        }
        if (uiState.isDialogVisibility) {
            DialogUi(
                title = StringKey.WalletSettingsBackupDialogTitle.textValue(),
                message = StringKey.WalletSettingsBackupDialogMessage.textValue(),
                onDismissRequest = onDismissRequest,
                primaryButton = ButtonData(
                    text = StringKey.WalletSettingsBackupDialogAction.textValue(),
                    onClick = onLookButtonClicked,
                ),
                secondaryButton = ButtonData(
                    text = StringKey.ReferralProgramDialogActionClose.textValue(),
                    onClick = onCloseDialogButtonClicked,
                ),
                content = {},
            )
        }
    }
}