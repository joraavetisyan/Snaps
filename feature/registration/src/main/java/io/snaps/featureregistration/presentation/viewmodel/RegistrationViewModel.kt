package io.snaps.featureregistration.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuthException
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.baseauth.data.AuthRepository
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.basesession.data.SessionRepository
import io.snaps.basesources.NotificationsSource
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.FullUrl
import io.snaps.corecommon.strings.StringKey
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.network.Action
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val authRepository: AuthRepository,
    @Bridged private val profileRepository: ProfileRepository,
    private val action: Action,
    private val notificationsSource: NotificationsSource,
) : SimpleViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    fun onPrivacyPolicyClicked() {
        viewModelScope.launch {
            _command publish Command.OpenLink("https://snaps-docs.gitbook.io/privacy-policy-snaps/")
        }
    }

    fun onTermsOfUserClicked() {
        viewModelScope.launch {
            _command publish Command.OpenLink("https://snaps-docs.gitbook.io/terms-of-service/")
        }
    }

    fun onLoginWithEmailClicked() = viewModelScope.launch {
        if (authRepository.getCurrentUser() != null && !authRepository.isEmailVerified()) {
            _uiState.update {
                it.copy(dialog = Dialog.EmailVerification)
            }
        } else {
            _uiState.update {
                it.copy(
                    bottomDialog = BottomDialog.SignIn,
                    confirmPasswordValue = "",
                    passwordValue = "",
                    emailAddressValue = "",
                )
            }
            _command publish Command.ShowBottomDialog
        }
    }

    fun onOneTapSignInStarted() {
        _uiState.update { it.copy(isLoading = true) }
    }

    fun onOneTapSignInCompleted(isSuccess: Boolean) {
        _uiState.update { it.copy(isLoading = false) }
        if (!isSuccess) {
            viewModelScope.launch {
                notificationsSource.sendError(StringKey.ErrorUnknown.textValue())
            }
        }
    }

    fun onLoginWithTwitterClicked() {
        /*TODO*/
    }

    fun onForgotPasswordClicked() = viewModelScope.launch {
        _uiState.update {
            it.copy(
                bottomDialog = BottomDialog.ResetPassword,
                passwordResetEmailValue = "",
            )
        }
        _command publish Command.ShowBottomDialog
    }

    fun onResetPasswordClicked() = viewModelScope.launch {
        action.execute {
            authRepository.resetPassword(
                email = uiState.value.passwordResetEmailValue.trim(),
            )
        }.doOnSuccess {
            _command publish Command.HideBottomDialog
            _uiState.update { it.copy(dialog = Dialog.ResetPasswordInstructions) }
        }.doOnError { error, _ ->
            if (error.cause is FirebaseAuthException) {
                notificationsSource.sendError(error)
            }
        }
    }

    fun onResetPasswordEmailValueChanged(email: String) {
        _uiState.update {
            it.copy(
                passwordResetEmailValue = email,
            )
        }
    }

    fun showSignInBottomDialog() = viewModelScope.launch {
        _uiState.update {
            it.copy(
                bottomDialog = BottomDialog.SignIn,
                confirmPasswordValue = "",
                passwordValue = "",
                emailAddressValue = "",
            )
        }
        _command publish Command.ShowBottomDialog
    }

    fun showSignUpBottomDialog() = viewModelScope.launch {
        _uiState.update {
            it.copy(
                bottomDialog = BottomDialog.SignUp,
                confirmPasswordValue = "",
                passwordValue = "",
                emailAddressValue = "",
            )
        }
        _command publish Command.ShowBottomDialog
    }

    fun onEmailAddressValueChanged(emailAddress: String) {
        _uiState.update {
            it.copy(emailAddressValue = emailAddress)
        }
    }

    fun onPasswordValueChanged(password: String) {
        _uiState.update {
            it.copy(passwordValue = password)
        }
    }

    fun onConfirmPasswordValueChanged(password: String) {
        _uiState.update {
            it.copy(confirmPasswordValue = password)
        }
    }

    fun signInWithGoogle(authCredential: AuthCredential) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        action.execute {
            authRepository.signInWithCredential(authCredential)
        }.doOnSuccess {
            handleAuth()
        }.doOnComplete {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun signInWithFacebook(authCredential: AuthCredential) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        action.execute {
            authRepository.signInWithCredential(authCredential)
        }.doOnSuccess {
            handleAuth()
        }.doOnComplete {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun signInWithEmail() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        action.execute {
            authRepository.signInWithEmail(
                email = uiState.value.emailAddressValue,
                password = uiState.value.passwordValue,
            )
        }.doOnSuccess {
            handleAuth()
        }.doOnError { error, _ ->
            if (error.cause is FirebaseAuthException) {
                notificationsSource.sendError(error)
            }
        }.doOnComplete {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun handleAuth() {
        action.execute {
            sessionRepository.tryLogin()
        }.doOnComplete {
            _command publish Command.HideBottomDialog
        }
    }

    fun signUpWithEmail() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        action.execute {
            authRepository.signUpWithEmail(
                email = uiState.value.emailAddressValue,
                password = uiState.value.confirmPasswordValue,
            )
        }.doOnSuccess { // todo flatmap in execute
            authRepository.sendEmailVerification()
        }.doOnError { error, data ->
            if (error.cause is FirebaseAuthException) {
                notificationsSource.sendError(error)
            }
        }.doOnComplete {
            _uiState.update { it.copy(isLoading = false) }
        }
        if (authRepository.getCurrentUser() != null && !authRepository.isEmailVerified()) {
            _command publish Command.HideBottomDialog
            _uiState.update {
                it.copy(dialog = Dialog.EmailVerification)
            }
        }
    }

    fun onEmailVerificationDialogDismissRequest() {
        _uiState.update {
            it.copy(dialog = null)
        }
    }

    fun onResetPasswordInstructionsDialogDismissRequest() {
        _uiState.update {
            it.copy(dialog = null)
        }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val bottomDialog: BottomDialog = BottomDialog.SignIn,
        val emailAddressValue: String = "",
        val passwordResetEmailValue: String = "",
        val passwordValue: String = "",
        val confirmPasswordValue: String = "",
        val dialog: Dialog? = null,
    ) {

        val isSignInButtonEnabled
            get() = emailAddressValue.isNotBlank()
                    && passwordValue.isNotBlank()

        val isSignUpButtonEnabled
            get() = emailAddressValue.isNotBlank()
                    && passwordValue.isNotBlank()
                    && confirmPasswordValue.isNotBlank()
                    && passwordValue == confirmPasswordValue

        val isResetPasswordButtonEnabled get() = passwordResetEmailValue.isNotBlank()
    }

    enum class BottomDialog {
        SignIn, SignUp, ResetPassword,
    }

    sealed class Dialog {
        object EmailVerification : Dialog()
        object ResetPasswordInstructions : Dialog()
    }

    sealed class Command {
        object ShowBottomDialog : Command()
        object HideBottomDialog : Command()
        object OpenConnectWalletScreen : Command()
        data class OpenLink(val link: FullUrl) : Command()
    }
}