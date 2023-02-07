package io.snaps.featureregistration.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import io.snaps.basesession.data.SessionRepository
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.coredata.database.TokenStorage
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
    private val tokenStorage: TokenStorage,
) : SimpleViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    fun onPrivacyPolicyClicked() { /*TODO*/ }

    fun onTermsOfUserClicked() { /*TODO*/ }

    fun onLoginWithEmailClicked() = viewModelScope.launch {
        _uiState.update {
            it.copy(
                bottomDialogType = BottomDialogType.SignIn,
                confirmPasswordValue = "",
                passwordValue = "",
                emailAddressValue = "",
            )
        }
        _command publish Command.ShowBottomDialog
    }

    fun onLoginWithTwitterClicked() { sessionRepository.onLogin() }

    fun onLoginWithFacebookClicked() { sessionRepository.onLogin() }

    fun onSignUpWithEmailClicked() { sessionRepository.onLogin() }

    fun showSignInBottomDialog() = viewModelScope.launch {
        _uiState.update {
            it.copy(
                bottomDialogType = BottomDialogType.SignIn,
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
                bottomDialogType = BottomDialogType.SignUp,
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

    fun onAuthTokenReceived(token: String) { // todo
        sessionRepository.onLogin()
    }

    data class UiState(
        val bottomDialogType: BottomDialogType = BottomDialogType.SignIn,
        val emailAddressValue: String = "",
        val passwordValue: String = "",
        val confirmPasswordValue: String = "",
    ) {
        val isSignInButtonEnabled get() = emailAddressValue.isNotBlank()
                && passwordValue.isNotBlank()

        val isSignUpButtonEnabled get() = emailAddressValue.isNotBlank()
                && passwordValue.isNotBlank()
                && confirmPasswordValue.isNotBlank()
                && passwordValue == confirmPasswordValue
    }

    enum class BottomDialogType {
        SignIn, SignUp
    }

    sealed class Command {
        object ShowBottomDialog : Command()
        object HideBottomDialog : Command()
        object OpenConnectWalletScreen : Command()
    }
}