package io.snaps.featureprofile.presentation.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.button.SimpleButtonGreyM
import io.snaps.coreuicompose.uikit.button.SimpleButtonRedInlineM
import io.snaps.coreuicompose.uikit.dialog.SimpleConfirmDialogUi
import io.snaps.coreuicompose.uikit.duplicate.SimpleTopAppBar
import io.snaps.coreuicompose.uikit.status.FullScreenLoaderUi
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.LocalStringHolder
import io.snaps.featureprofile.ScreenNavigator
import io.snaps.featureprofile.presentation.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<SettingsViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    viewModel.command.collectAsCommand {
        when (it) {
            SettingsViewModel.Command.OpenAboutProjectScreen -> router.toAboutProjectScreen()
            SettingsViewModel.Command.OpenWalletSettingsScreen -> router.toWalletSettingsScreen()
            SettingsViewModel.Command.OpenSocialNetworksScreen -> router.toSocialNetworksScreen()
            SettingsViewModel.Command.OpenReferralProgramScreen -> router.toReferralProgramScreen()
            SettingsViewModel.Command.OpenEditProfileScreen -> router.toEditProfileScreen()
        }
    }

    SettingsScreen(
        uiState = uiState,
        onBackClicked = router::back,
        onLogoutClicked = viewModel::onLogoutClicked,
        onDeleteAccountClicked = viewModel::onDeleteAccountClicked,
        onLogoutConfirmed = viewModel::onLogoutConfirmed,
        onDialogDismissRequest = viewModel::onDialogDismissRequest,
    )
    FullScreenLoaderUi(isLoading = uiState.isLoading)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    uiState: SettingsViewModel.UiState,
    onLogoutClicked: () -> Unit,
    onDeleteAccountClicked: () -> Unit,
    onBackClicked: () -> Boolean,
    onLogoutConfirmed: () -> Unit,
    onDialogDismissRequest: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = AppTheme.specificColorScheme.uiContentBg,
        topBar = {
            SimpleTopAppBar(
                title = {
                    Text(text = LocalStringHolder.current(StringKey.SettingsTitle))
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
                        .shadow(
                            shape = AppTheme.shapes.medium,
                            elevation = 16.dp
                        )
                        .background(
                            color = AppTheme.specificColorScheme.white,
                            shape = AppTheme.shapes.medium,
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                )
            }
            item {
                Spacer(Modifier.height(16.dp))
                SimpleButtonGreyM(
                    onClick = onLogoutClicked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                ) {
                    SimpleButtonContent(text = StringKey.SettingsActionLogout.textValue())
                }
                SimpleButtonRedInlineM(
                    onClick = onDeleteAccountClicked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                ) {
                    SimpleButtonContent(
                        text = StringKey.SettingsActionDeleteAccount.textValue(),
                        iconLeft = AppTheme.specificIcons.delete,
                        iconTint = AppTheme.specificColorScheme.uiSystemRed,
                    )
                }
            }
        }
    }
    when (uiState.dialog) {
        is SettingsViewModel.Dialog.ConfirmLogout -> SimpleConfirmDialogUi(
            title = StringKey.SettingsDialogLogoutTitle.textValue(),
            text = StringKey.SettingsDialogLogoutMessage.textValue(),
            onDismissRequest = onDialogDismissRequest,
            onConfirmRequest = onLogoutConfirmed,
        )

        null -> Unit
    }
}