package io.snaps.featureprofile.presentation.screen.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAllExcludeTop
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionM
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.duplicate.SimpleTopAppBar
import io.snaps.coreuicompose.uikit.input.SimpleTextField
import io.snaps.coreuicompose.uikit.status.FullScreenLoaderUi
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.featureprofile.ScreenNavigator
import io.snaps.featureprofile.presentation.viewmodel.EditProfileViewModel

@Composable
fun EditNameScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<EditProfileViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    viewModel.command.collectAsCommand {
        when (it) {
            EditProfileViewModel.Command.CloseScreen -> router.back()
        }
    }

    EditNameScreen(
        uiState = uiState,
        onBackClicked = router::back,
        onNameValueChanged = viewModel::onNameValueChanged,
        onSavedClicked = viewModel::editName,
    )
    FullScreenLoaderUi(isLoading = uiState.isLoading)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditNameScreen(
    uiState: EditProfileViewModel.UiState,
    onNameValueChanged: (String) -> Unit,
    onBackClicked: () -> Boolean,
    onSavedClicked: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = AppTheme.specificColorScheme.uiContentBg,
        topBar = {
            SimpleTopAppBar(
                title = {
                    Text(text = StringKey.EditNameTitle.textValue().get())
                },
                navigationIcon = AppTheme.specificIcons.back to onBackClicked,
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            SimpleButtonActionM(
                enabled = uiState.isNameValid,
                modifier = Modifier
                    .imePadding()
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = onSavedClicked,
            ) {
                SimpleButtonContent(text = StringKey.EditNameAction.textValue())
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 12.dp)
                .inset(insetAllExcludeTop()),
        ) {
            SimpleTextField(
                modifier = Modifier.fillMaxWidth(),
                onValueChange = onNameValueChanged,
                value = uiState.editNameValue,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                ),
                maxLines = 1,
            )
            Text(
                text = StringKey.EditNameMessage.textValue().get(),
                style = AppTheme.specificTypography.bodySmall,
                modifier = Modifier,
                color = AppTheme.specificColorScheme.textSecondary,
            )
        }
    }
}