package io.snaps.featureregistration.presentation.screen

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.R
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.coreui.viewmodel.collectAsCommand
import io.snaps.coreuicompose.tools.get
import io.snaps.coreuicompose.tools.inset
import io.snaps.coreuicompose.tools.insetAll
import io.snaps.coreuicompose.uikit.button.SimpleButtonActionM
import io.snaps.coreuicompose.uikit.button.SimpleButtonContent
import io.snaps.coreuicompose.uikit.button.SimpleButtonDefaultM
import io.snaps.coreuicompose.uikit.button.SimpleButtonInlineM
import io.snaps.coreuicompose.uikit.input.SimpleTextField
import io.snaps.coreuicompose.uikit.other.LinkText
import io.snaps.coreuicompose.uikit.other.LinkTextData
import io.snaps.coreuicompose.uikit.status.SimpleBottomDialogUI
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.LocalStringHolder
import io.snaps.featureregistration.presentation.GoogleSignInContract
import io.snaps.featureregistration.presentation.ScreenNavigator
import io.snaps.featureregistration.presentation.viewmodel.RegistrationViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RegistrationScreen(
    navHostController: NavHostController,
) {
    val router = remember(navHostController) { ScreenNavigator(navHostController) }
    val viewModel = hiltViewModel<RegistrationViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )
    val coroutineScope = rememberCoroutineScope()

    val googleSignInActivityResultLauncher = rememberLauncherForActivityResult(
        contract = GoogleSignInContract(),
    ) {
        it?.let {
            viewModel.onAuthTokenReceived(it)
        }
    }

    viewModel.command.collectAsCommand {
        when (it) {
            RegistrationViewModel.Command.ShowBottomDialog -> coroutineScope.launch { sheetState.show() }
            RegistrationViewModel.Command.HideBottomDialog -> coroutineScope.launch { sheetState.hide() }
            RegistrationViewModel.Command.OpenConnectWalletScreen -> router.toConnectWalletScreen()
        }
    }

    BackHandler(enabled = sheetState.isVisible) {
        coroutineScope.launch { sheetState.hide() }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            when (uiState.bottomDialogType) {
                RegistrationViewModel.BottomDialogType.SignIn -> LoginWithEmailDialog(
                    uiState = uiState,
                    onLoginWithEmailClicked = viewModel::onLoginWithEmailClicked,
                    onEmailAddressValueChanged = viewModel::onEmailAddressValueChanged,
                    onPasswordValueChanged = viewModel::onPasswordValueChanged,
                    onSignUpClicked = viewModel::showSignUpBottomDialog,
                )
                RegistrationViewModel.BottomDialogType.SignUp -> RegistrationWithEmailDialog(
                    uiState = uiState,
                    onEmailAddressValueChanged = viewModel::onEmailAddressValueChanged,
                    onPasswordValueChanged = viewModel::onPasswordValueChanged,
                    onConfirmPasswordValueChanged = viewModel::onConfirmPasswordValueChanged,
                    onSignInClicked = viewModel::showSignInBottomDialog,
                    onSignUpClicked = viewModel::onSignUpWithEmailClicked,
                    onPrivacyPolicyClicked = viewModel::onPrivacyPolicyClicked,
                    onTermsOfUserClicked = viewModel::onTermsOfUserClicked,
                )
            }
        },
    ) {
        RegistrationScreen(
            onLoginWithTwitterClicked = viewModel::onLoginWithTwitterClicked,
            onLoginWithGoogleClicked = { googleSignInActivityResultLauncher.launch(null) },
            onLoginWithEmailClicked = viewModel::onLoginWithEmailClicked,
            onLoginWithFacebookClicked = viewModel::onLoginWithFacebookClicked,
            onPrivacyPolicyClicked = viewModel::onPrivacyPolicyClicked,
            onTermsOfUserClicked = viewModel::onTermsOfUserClicked,
        )
    }
}

@Composable
private fun RegistrationScreen(
    onPrivacyPolicyClicked: () -> Unit,
    onLoginWithEmailClicked: () -> Unit,
    onTermsOfUserClicked: () -> Unit,
    onLoginWithGoogleClicked: () -> Unit,
    onLoginWithTwitterClicked: () -> Unit,
    onLoginWithFacebookClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.specificColorScheme.uiContentBg)
            .inset(insetAll())
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = ImageValue.ResImage(R.drawable.img_welcome).get(),
            modifier = Modifier
                .align(Alignment.End)
                .padding(start = 12.dp),
            contentDescription = null
        )
        Login(
            onLoginWithFacebookClicked = onLoginWithFacebookClicked,
            onLoginWithGoogleClicked = onLoginWithGoogleClicked,
            onLoginWithTwitterClicked = onLoginWithTwitterClicked,
        )
        HorizontalDivider()
        PrivacyPolicy(
            onPrivacyPolicyClicked = onPrivacyPolicyClicked,
            onTermsOfUserClicked = onTermsOfUserClicked,
        )
        SimpleButtonActionM(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .shadow(elevation = 16.dp, shape = CircleShape),
            onClick = onLoginWithEmailClicked,
        ) {
            SimpleButtonContent(text = StringKey.RegistrationActionLoginWithEmail.textValue())
        }
    }
}

@Composable
private fun Login(
    onLoginWithGoogleClicked: () -> Unit,
    onLoginWithTwitterClicked: () -> Unit,
    onLoginWithFacebookClicked: () -> Unit,
) {
    SimpleButtonDefaultM(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .shadow(elevation = 16.dp, shape = CircleShape),
        onClick = onLoginWithGoogleClicked,
    ) {
        SimpleButtonContent(
            text = StringKey.RegistrationActionLoginWithGoogle.textValue(),
            iconLeft = AppTheme.specificIcons.google,
        )
    }
    Spacer(modifier = Modifier.height(12.dp))
    SimpleButtonDefaultM(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .shadow(elevation = 16.dp, shape = CircleShape),
        onClick = onLoginWithFacebookClicked,
    ) {
        SimpleButtonContent(
            text = StringKey.RegistrationActionLoginWithFacebook.textValue(),
            iconLeft = AppTheme.specificIcons.facebook,
        )
    }
    Spacer(modifier = Modifier.height(12.dp))
    SimpleButtonDefaultM(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .shadow(elevation = 16.dp, shape = CircleShape),
        onClick = onLoginWithTwitterClicked,
    ) {
        SimpleButtonContent(
            text = StringKey.RegistrationActionLoginWithTwitter.textValue(),
            iconLeft = AppTheme.specificIcons.twitter,
        )
    }
}

@Composable
private fun HorizontalDivider() {
    Box(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .padding(top = 32.dp, bottom = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Divider(color = AppTheme.specificColorScheme.lightGrey, thickness = 2.dp)
        Text(
            text = LocalStringHolder.current(StringKey.RegistrationFieldOr),
            color = AppTheme.specificColorScheme.textSecondary,
            style = AppTheme.specificTypography.titleSmall,
            modifier = Modifier
                .background(AppTheme.specificColorScheme.uiContentBg)
                .padding(8.dp),
        )
    }
}

@Composable
private fun PrivacyPolicy(
    onPrivacyPolicyClicked: () -> Unit,
    onTermsOfUserClicked: () -> Unit,
) {
    val privacy = StringKey.RegistrationActionPrivacyPolicy.textValue()
    val termsOfUse = StringKey.RegistrationActionTermsOfUse.textValue()

    LinkText(
        text = StringKey.RegistrationMessagePrivacyPolicy.textValue(),
        linkTextData = listOf(
            LinkTextData(text = privacy, clickListener = onPrivacyPolicyClicked),
            LinkTextData(text = termsOfUse, clickListener = onTermsOfUserClicked),
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 32.dp)
    )
}

@Composable
private fun LoginWithEmailDialog(
    uiState: RegistrationViewModel.UiState,
    onEmailAddressValueChanged: (String) -> Unit,
    onPasswordValueChanged: (String) -> Unit,
    onLoginWithEmailClicked: () -> Unit,
    onPrivacyPolicyClicked: () -> Unit,
    onTermsOfUserClicked: () -> Unit,
) {
    SimpleBottomDialogUI(StringKey.RegistrationDialogSignInTitle.textValue()) {
        item {
            Text(
                text = LocalStringHolder.current(StringKey.RegistrationDialogSignInMessage),
                color = AppTheme.specificColorScheme.textSecondary,
                style = AppTheme.specificTypography.titleSmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 32.dp),
                textAlign = TextAlign.Center,
            )
            SimpleTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onValueChange = onEmailAddressValueChanged,
                value = uiState.emailAddressValue,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done,
                ),
                placeholder = {
                    Text(
                        text = LocalStringHolder.current(StringKey.RegistrationDialogSignInHintEmail),
                        style = AppTheme.specificTypography.titleSmall,
                    )
                },
                maxLines = 1,
            )
            SimpleTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                onValueChange = onPasswordValueChanged,
                value = uiState.passwordValue,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                ),
                placeholder = {
                    Text(
                        text = LocalStringHolder.current(StringKey.RegistrationDialogSignInHintPassword),
                        style = AppTheme.specificTypography.titleSmall,
                    )
                },
                maxLines = 1,
            )
            SimpleButtonActionM(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .shadow(elevation = 16.dp, shape = CircleShape),
                onClick = onLoginWithEmailClicked,
            ) {
                SimpleButtonContent(text = StringKey.RegistrationActionLoginWithEmail.textValue())
            }
            PrivacyPolicy(
                onPrivacyPolicyClicked = onPrivacyPolicyClicked,
                onTermsOfUserClicked = onTermsOfUserClicked,
            )
        }
    }
}

@Composable
private fun LoginWithEmailDialog(
    uiState: RegistrationViewModel.UiState,
    onEmailAddressValueChanged: (String) -> Unit,
    onPasswordValueChanged: (String) -> Unit,
    onLoginWithEmailClicked: () -> Unit,
    onSignUpClicked: () -> Unit,
) {
    SimpleBottomDialogUI(StringKey.RegistrationDialogSignInTitle.textValue()) {
        item {
            Text(
                text = LocalStringHolder.current(StringKey.RegistrationDialogSignInMessage),
                color = AppTheme.specificColorScheme.textSecondary,
                style = AppTheme.specificTypography.titleSmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 32.dp),
                textAlign = TextAlign.Center,
            )
            SimpleTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onValueChange = onEmailAddressValueChanged,
                value = uiState.emailAddressValue,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
                placeholder = {
                    Text(
                        text = LocalStringHolder.current(StringKey.RegistrationDialogSignInHintEmail),
                        style = AppTheme.specificTypography.titleSmall,
                    )
                },
                maxLines = 1,
            )
            SimpleTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                onValueChange = onPasswordValueChanged,
                value = uiState.passwordValue,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                ),
                placeholder = {
                    Text(
                        text = LocalStringHolder.current(StringKey.RegistrationDialogSignInHintPassword),
                        style = AppTheme.specificTypography.titleSmall,
                    )
                },
                maxLines = 1,
            )
            SimpleButtonActionM(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .shadow(elevation = 16.dp, shape = CircleShape),
                enabled = uiState.isSignInButtonEnabled,
                onClick = onLoginWithEmailClicked,
            ) {
                SimpleButtonContent(text = StringKey.RegistrationDialogSignInActionLogin.textValue())
            }
            SimpleButtonInlineM(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                onClick = onSignUpClicked,
            ) {
                SimpleButtonContent(text = StringKey.RegistrationDialogSignInActionRegistration.textValue())
            }
        }
    }
}

@Composable
private fun RegistrationWithEmailDialog(
    uiState: RegistrationViewModel.UiState,
    onEmailAddressValueChanged: (String) -> Unit,
    onPasswordValueChanged: (String) -> Unit,
    onConfirmPasswordValueChanged: (String) -> Unit,
    onSignInClicked: () -> Unit,
    onSignUpClicked: () -> Unit,
    onPrivacyPolicyClicked: () -> Unit,
    onTermsOfUserClicked: () -> Unit,
) {
    SimpleBottomDialogUI(StringKey.RegistrationDialogSignUpTitle.textValue()) {
        item {
            Text(
                text = LocalStringHolder.current(StringKey.RegistrationDialogSignUpMessage),
                color = AppTheme.specificColorScheme.textSecondary,
                style = AppTheme.specificTypography.titleSmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 32.dp),
                textAlign = TextAlign.Center,
            )
            SimpleTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onValueChange = onEmailAddressValueChanged,
                value = uiState.emailAddressValue,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
                placeholder = {
                    Text(
                        text = LocalStringHolder.current(StringKey.RegistrationDialogSignUpHintEmail),
                        style = AppTheme.specificTypography.titleSmall,
                    )
                },
                maxLines = 1,
            )
            SimpleTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                onValueChange = onPasswordValueChanged,
                value = uiState.passwordValue,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                ),
                placeholder = {
                    Text(
                        text = LocalStringHolder.current(StringKey.RegistrationDialogSignUpHintPassword),
                        style = AppTheme.specificTypography.titleSmall,
                    )
                },
                maxLines = 1,
            )
            SimpleTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onValueChange = onConfirmPasswordValueChanged,
                value = uiState.confirmPasswordValue,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                ),
                placeholder = {
                    Text(
                        text = LocalStringHolder.current(StringKey.RegistrationDialogSignUpHintConfirmPassword),
                        style = AppTheme.specificTypography.titleSmall,
                    )
                },
                maxLines = 1,
            )
            SimpleButtonActionM(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .shadow(elevation = 16.dp, shape = CircleShape),
                enabled = uiState.isSignUpButtonEnabled,
                onClick = onSignUpClicked,
            ) {
                SimpleButtonContent(text = StringKey.RegistrationDialogSignUpActionRegistration.textValue())
            }
            SimpleButtonInlineM(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onClick = onSignInClicked,
            ) {
                SimpleButtonContent(text = StringKey.RegistrationDialogSignUpActionLogin.textValue())
            }
            PrivacyPolicy(
                onPrivacyPolicyClicked = onPrivacyPolicyClicked,
                onTermsOfUserClicked = onTermsOfUserClicked,
            )
        }
    }
}