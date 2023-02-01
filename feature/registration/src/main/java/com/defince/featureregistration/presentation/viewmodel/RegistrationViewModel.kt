package com.defince.featureregistration.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.defince.coreui.viewmodel.SimpleViewModel
import com.defince.coreui.viewmodel.publish
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor() : SimpleViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    fun onPrivacyPolicyClicked() { /*TODO*/ }

    fun onTermsOfUserClicked() { /*TODO*/ }

    fun showRegistrationForm() = viewModelScope.launch {
        _uiState.update {
            it.copy(
                bottomDialogType = BottomDialogType.LoginWithEmail,
                confirmationCodeValue = "",
                emailAddressValue = "",
            )
        }
        _command publish Command.ShowBottomDialog
    }

    fun onLoginWithEmailClicked() = viewModelScope.launch {
        if (uiState.value.isConfirmationCodeValid) {
            _command publish Command.OpenConnectWalletScreen
        }
    }
    fun onLoginWithAppleClicked() { /*TODO*/ }

    fun onLoginWithGoogleClicked() { /*TODO*/ }

    fun onLoginWithTwitterClicked() { /*TODO*/ }

    fun onLoginWithFacebookClicked() { /*TODO*/ }

    fun onSendCodeClicked() { /*TODO*/ }

    fun onEmailAddressValueChanged(emailAddress: String) {
        _uiState.update {
            it.copy(emailAddressValue = emailAddress)
        }
    }

    fun onConfirmationCodeValueChanged(confirmationCode: String) {
        _uiState.update {
            it.copy(confirmationCodeValue = confirmationCode)
        }
    }

    data class UiState(
        val bottomDialogType: BottomDialogType = BottomDialogType.LoginWithEmail,
        val emailAddressValue: String = "",
        val confirmationCodeValue: String = "",
    ) {
        val isConfirmationCodeValid get() = confirmationCodeValue.length >= 0 // todo
    }

    enum class BottomDialogType {
        LoginWithEmail,
    }

    sealed class Command {
        object ShowBottomDialog : Command()
        object HideBottomDialog : Command()
        object OpenConnectWalletScreen : Command()
    }
}