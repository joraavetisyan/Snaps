package io.snaps.featureregistration.presentation.screen

import android.app.Activity
import android.app.Activity.RESULT_OK
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import io.snaps.corecommon.R
import io.snaps.corecommon.container.imageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.ext.log
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
import io.snaps.coreuicompose.uikit.status.FullScreenLoaderUi
import io.snaps.coreuicompose.uikit.dialog.SimpleAlertDialogUi
import io.snaps.coreuicompose.uikit.bottomsheetdialog.SimpleBottomDialogUI
import io.snaps.coreuicompose.uikit.text.LinkText
import io.snaps.coreuicompose.uikit.text.LinkTextData
import io.snaps.coreuitheme.compose.AppTheme
import io.snaps.coreuitheme.compose.LocalStringHolder
import io.snaps.featureregistration.presentation.ScreenNavigator
import io.snaps.featureregistration.presentation.viewmodel.RegistrationViewModel
import kotlinx.coroutines.launch

private const val SERVER_CLIENT_ID =
    "132799039711-rd59jfaphbpinbmhrp647hqapp2b6aiu.apps.googleusercontent.com"

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
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
    val context = LocalContext.current

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(key1 = sheetState.currentValue) {
        if (sheetState.currentValue == ModalBottomSheetValue.Hidden) {
            focusRequester.freeFocus()
            keyboardController?.hide()
        }
    }

    val googleSignInRequest = BeginSignInRequest.Builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(SERVER_CLIENT_ID)
                .setFilterByAuthorizedAccounts(false)
                .build()
        )
        .setAutoSelectEnabled(true)
        .build()
    val oneTapClient = Identity.getSignInClient(context)
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val credentials = oneTapClient.getSignInCredentialFromIntent(result.data)
            credentials.googleIdToken?.let {
                val authCredential = GoogleAuthProvider.getCredential(it, null)
                viewModel.signInWithGoogle(authCredential)
            }
        }
    }

    val callbackManager = CallbackManager.Factory.create()
    val loginManager = LoginManager.getInstance()
    val facebookSignInLauncher = rememberLauncherForActivityResult(
        contract = loginManager.createLogInActivityResultContract(callbackManager)
    ) { result ->
        loginManager.onActivityResult(
            resultCode = result.resultCode,
            data = result.data,
            callback = object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    val token = result.accessToken.token
                    val credential = FacebookAuthProvider.getCredential(token)
                    viewModel.signInWithFacebook(credential)
                }

                override fun onCancel() = Unit
                override fun onError(error: FacebookException) = Unit
            },
        )
    }

    viewModel.command.collectAsCommand {
        when (it) {
            RegistrationViewModel.Command.ShowBottomDialog -> coroutineScope.launch { sheetState.show() }
            RegistrationViewModel.Command.HideBottomDialog -> coroutineScope.launch {
                focusRequester.freeFocus()
                keyboardController?.hide()
                sheetState.hide()
            }
            RegistrationViewModel.Command.OpenConnectWalletScreen -> router.toConnectWalletScreen()
        }
    }

    BackHandler(enabled = sheetState.isVisible) {
        coroutineScope.launch { sheetState.hide() }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            when (uiState.bottomDialog) {
                RegistrationViewModel.BottomDialog.SignIn -> LoginWithEmailDialog(
                    uiState = uiState,
                    focusRequester = focusRequester,
                    onLoginWithEmailClicked = viewModel::signInWithEmail,
                    onEmailAddressValueChanged = viewModel::onEmailAddressValueChanged,
                    onPasswordValueChanged = viewModel::onPasswordValueChanged,
                    onSignUpClicked = viewModel::showSignUpBottomDialog,
                    onForgotPasswordClicked = viewModel::onForgotPasswordClicked,
                )

                RegistrationViewModel.BottomDialog.SignUp -> RegistrationWithEmailDialog(
                    uiState = uiState,
                    focusRequester = focusRequester,
                    onEmailAddressValueChanged = viewModel::onEmailAddressValueChanged,
                    onPasswordValueChanged = viewModel::onPasswordValueChanged,
                    onConfirmPasswordValueChanged = viewModel::onConfirmPasswordValueChanged,
                    onSignInClicked = viewModel::showSignInBottomDialog,
                    onSignUpClicked = viewModel::signUpWithEmail,
                    onPrivacyPolicyClicked = viewModel::onPrivacyPolicyClicked,
                    onTermsOfUserClicked = viewModel::onTermsOfUserClicked,
                )

                RegistrationViewModel.BottomDialog.ResetPassword -> ResetPasswordDialog(
                    passwordResetEmail = uiState.passwordResetEmailValue,
                    focusRequester = focusRequester,
                    isResetPasswordButtonEnabled = uiState.isResetPasswordButtonEnabled,
                    onResetPasswordEmailValueChanged = viewModel::onResetPasswordEmailValueChanged,
                    onResetPasswordClicked = viewModel::onResetPasswordClicked,
                )
            }
        },
    ) {
        RegistrationScreen(
            uiState = uiState,
            onLoginWithTwitterClicked = viewModel::onLoginWithTwitterClicked,
            onLoginWithGoogleClicked = {
                oneTapClient.beginSignIn(googleSignInRequest)
                    .addOnSuccessListener(context as Activity) { result ->
                        val intentSenderRequest = IntentSenderRequest.Builder(
                            result.pendingIntent.intentSender
                        ).build()
                        googleSignInLauncher.launch(intentSenderRequest)
                    }.addOnFailureListener {
                        log("""
                            If this is "com.google.android.gms.common.api.ApiException: 10: Developer console is not set up correctly." add your machine's sha1 to the firebase console
                        """.trimIndent())
                        log(it)
                    }
            },
            onLoginWithEmailClicked = viewModel::onLoginWithEmailClicked,
            onLoginWithFacebookClicked = {
                facebookSignInLauncher.launch(
                    listOf("email", "public_profile")
                )
            },
            onPrivacyPolicyClicked = viewModel::onPrivacyPolicyClicked,
            onTermsOfUserClicked = viewModel::onTermsOfUserClicked,
            onEmailVerificationDialogDismissRequest = viewModel::onEmailVerificationDialogDismissRequest,
            onResetPasswordInstructionsDialogDismissRequest = viewModel::onResetPasswordInstructionsDialogDismissRequest,
        )
    }
    FullScreenLoaderUi(isLoading = uiState.isLoading)
}

@Composable
private fun RegistrationScreen(
    uiState: RegistrationViewModel.UiState,
    onPrivacyPolicyClicked: () -> Unit,
    onLoginWithEmailClicked: () -> Unit,
    onTermsOfUserClicked: () -> Unit,
    onLoginWithGoogleClicked: () -> Unit,
    onLoginWithTwitterClicked: () -> Unit,
    onLoginWithFacebookClicked: () -> Unit,
    onEmailVerificationDialogDismissRequest: () -> Unit,
    onResetPasswordInstructionsDialogDismissRequest: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.specificColorScheme.uiContentBg)
            .inset(insetAll())
            .padding(vertical = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = R.drawable.img_guy_welcoming.imageValue().get(),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.End)
                .heightIn(296.dp)
                .padding(start = 12.dp),
            contentDescription = null,
            contentScale = ContentScale.Crop,
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
                .padding(horizontal = 12.dp),
            onClick = onLoginWithEmailClicked,
        ) {
            SimpleButtonContent(text = StringKey.RegistrationActionLoginWithEmail.textValue())
        }
    }

    uiState.dialog?.let {
        when (it) {
            RegistrationViewModel.Dialog.EmailVerification -> SimpleAlertDialogUi(
                text = StringKey.RegistrationDialogVerificationMessage.textValue(),
                title = StringKey.RegistrationDialogVerificationTitle.textValue(),
                buttonText = StringKey.ActionOk.textValue(),
                onClickRequest = onEmailVerificationDialogDismissRequest,
            )

            RegistrationViewModel.Dialog.ResetPasswordInstructions -> SimpleAlertDialogUi(
                text = StringKey.RegistrationDialogResetPasswordInstructionsMessage.textValue(),
                buttonText = StringKey.ActionOk.textValue(),
                onClickRequest = onResetPasswordInstructionsDialogDismissRequest,
            )
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
    /*SimpleButtonDefaultM(
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
    }*/
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
    focusRequester: FocusRequester,
    onEmailAddressValueChanged: (String) -> Unit,
    onPasswordValueChanged: (String) -> Unit,
    onLoginWithEmailClicked: () -> Unit,
    onSignUpClicked: () -> Unit,
    onForgotPasswordClicked: () -> Unit,
) {
    SimpleBottomDialogUI(header = StringKey.RegistrationDialogSignInTitle.textValue()) {
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
                    .padding(horizontal = 16.dp)
                    .focusRequester(focusRequester),
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
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .focusRequester(focusRequester),
                onValueChange = onPasswordValueChanged,
                value = uiState.passwordValue,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
                visualTransformation = PasswordVisualTransformation(),
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
                    .padding(horizontal = 16.dp, vertical = 8.dp),
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
            SimpleButtonInlineM(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onClick = onForgotPasswordClicked,
            ) {
                SimpleButtonContent(text = StringKey.RegistrationDialogSignInActionForgotPassword.textValue())
            }
        }
    }
}

@Composable
private fun RegistrationWithEmailDialog(
    focusRequester: FocusRequester,
    uiState: RegistrationViewModel.UiState,
    onEmailAddressValueChanged: (String) -> Unit,
    onPasswordValueChanged: (String) -> Unit,
    onConfirmPasswordValueChanged: (String) -> Unit,
    onSignInClicked: () -> Unit,
    onSignUpClicked: () -> Unit,
    onPrivacyPolicyClicked: () -> Unit,
    onTermsOfUserClicked: () -> Unit,
) {
    SimpleBottomDialogUI(header = StringKey.RegistrationDialogSignUpTitle.textValue()) {
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
                    .padding(horizontal = 16.dp)
                    .focusRequester(focusRequester),
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
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .focusRequester(focusRequester),
                onValueChange = onPasswordValueChanged,
                value = uiState.passwordValue,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next,
                ),
                visualTransformation = PasswordVisualTransformation(),
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
                    .padding(horizontal = 16.dp)
                    .focusRequester(focusRequester),
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
                    .padding(16.dp),
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

@Composable
private fun ResetPasswordDialog(
    focusRequester: FocusRequester,
    passwordResetEmail: String,
    isResetPasswordButtonEnabled: Boolean,
    onResetPasswordEmailValueChanged: (String) -> Unit,
    onResetPasswordClicked: () -> Unit,
) {
    SimpleBottomDialogUI(header = StringKey.RegistrationDialogResetPasswordTitle.textValue()) {
        item {
            Text(
                text = StringKey.RegistrationDialogResetPasswordFieldEnterEmail.textValue().get(),
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
                    .padding(horizontal = 16.dp)
                    .focusRequester(focusRequester),
                onValueChange = onResetPasswordEmailValueChanged,
                value = passwordResetEmail,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
                placeholder = {
                    Text(
                        text = StringKey.RegistrationDialogResetPasswordHintEmail.textValue().get(),
                        style = AppTheme.specificTypography.titleSmall,
                    )
                },
                maxLines = 1,
            )
            SimpleButtonActionM(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = isResetPasswordButtonEnabled,
                onClick = onResetPasswordClicked,
            ) {
                SimpleButtonContent(text = StringKey.RegistrationDialogResetPasswordAction.textValue())
            }
        }
    }
}