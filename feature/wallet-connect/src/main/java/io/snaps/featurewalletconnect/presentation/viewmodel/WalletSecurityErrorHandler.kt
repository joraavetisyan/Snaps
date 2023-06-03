package io.snaps.featurewalletconnect.presentation.viewmodel

import io.snaps.basewallet.domain.WalletSecurityException
import io.snaps.basewallet.domain.DeviceNotSecuredException
import io.snaps.basewallet.domain.ScreenLockNotSetException
import io.snaps.basewallet.domain.UserNotAuthenticatedRecentlyException
import io.snaps.corecommon.model.AppError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

interface WalletSecurityErrorHandler {

    val walletSecurityState: StateFlow<UiState>

    fun handleWalletSecurityError(error: AppError)

    fun onDialogDismissRequested()

    data class UiState(
        val dialog: Dialog? = null,
    )

    enum class Dialog {
        ScreenLockNotSet,
        UserNotAuthenticatedRecently,
        DeviceNotSecured,
    }
}

class WalletSecurityErrorHandlerImplDelegate @Inject constructor() : WalletSecurityErrorHandler {

    private val _walletSecurityState = MutableStateFlow(WalletSecurityErrorHandler.UiState())
    override val walletSecurityState = _walletSecurityState.asStateFlow()

    override fun handleWalletSecurityError(error: AppError) {
        when (error.cause as? WalletSecurityException) {
            is ScreenLockNotSetException -> _walletSecurityState.update {
                it.copy(dialog = WalletSecurityErrorHandler.Dialog.ScreenLockNotSet)
            }
            is UserNotAuthenticatedRecentlyException -> _walletSecurityState.update {
                it.copy(dialog = WalletSecurityErrorHandler.Dialog.UserNotAuthenticatedRecently)
            }
            is DeviceNotSecuredException -> _walletSecurityState.update {
                it.copy(dialog = WalletSecurityErrorHandler.Dialog.DeviceNotSecured)
            }
            null -> Unit // only security exceptions handled
        }
    }

    override fun onDialogDismissRequested() {
        _walletSecurityState.update { it.copy(dialog = null) }
    }
}