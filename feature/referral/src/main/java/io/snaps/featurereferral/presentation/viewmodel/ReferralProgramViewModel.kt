package io.snaps.featurereferral.presentation.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.baseprofile.domain.UserInfoModel
import io.snaps.basesession.data.OnboardingHandler
import io.snaps.basesources.BottomDialogBarVisibilityHandler
import io.snaps.basesources.NotificationsSource
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.OnboardingType
import io.snaps.corecommon.model.Uuid
import io.snaps.corecommon.strings.StringKey
import io.snaps.corecommon.strings.addPrefix
import io.snaps.coredata.network.Action
import io.snaps.coreui.FileManager
import io.snaps.coreui.barcode.BarcodeManager
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.featurereferral.presentation.screen.ReferralsTileState
import io.snaps.featurereferral.presentation.toReferralsUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReferralProgramViewModel @Inject constructor(
    mainHeaderHandlerDelegate: MainHeaderHandler,
    onboardingHandlerDelegate: OnboardingHandler,
    bottomDialogBarVisibilityHandlerDelegate: BottomDialogBarVisibilityHandler,
    private val fileManager: FileManager,
    private val barcodeManager: BarcodeManager,
    private val profileRepository: ProfileRepository,
    private val action: Action,
    private val notificationsSource: NotificationsSource,
) : SimpleViewModel(),
    MainHeaderHandler by mainHeaderHandlerDelegate,
    OnboardingHandler by onboardingHandlerDelegate,
    BottomDialogBarVisibilityHandler by bottomDialogBarVisibilityHandlerDelegate {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    init {
        subscribeOnCurrentUser()
        subscribeOnReferrals()

        updateReferrals()

        checkOnboarding(OnboardingType.Referral)
    }

    private fun subscribeOnCurrentUser() {
        profileRepository.state.onEach { state ->
            if (state is Effect && state.isSuccess) {
                _uiState.update {
                    val inviteCode = state.requireData.ownInviteCode
                    it.copy(
                        referralCode = inviteCode.addPrefix("#"),
                        referralLink = inviteCode.addPrefix("https://snaps.io/"),
                        referralQr = barcodeManager.getQrCodeBitmap(text = inviteCode, size = 600f),
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun subscribeOnReferrals() {
        profileRepository.referralsState.onEach { state ->
            _uiState.update {
                it.copy(
                    referralsTileState = state.toReferralsUiState(
                        onReferralClick = ::onReferralClick,
                        onReloadClick = ::updateReferrals,
                        onShowQrClick = ::onShowReferralQrClicked,
                    ),
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun updateReferrals() {
        viewModelScope.launch {
            action.execute {
                profileRepository.updateReferrals()
            }
        }
    }

    private fun onReferralClick(model: UserInfoModel) {
        viewModelScope.launch {
            _command publish Command.OpenUserInfoScreen(model.entityId)
        }
    }

    fun onEnterCodeClicked() = viewModelScope.launch {
        _uiState.update {
            it.copy(bottomDialog = BottomDialog.ReferralCode)
        }
        _command publish Command.ShowBottomDialog
    }

    fun onInviteUserButtonClicked() {
        _uiState.update {
            it.copy(isInviteUserDialogVisible = true)
        }
    }

    fun onReferralCodeDialogButtonClicked() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        action.execute {
            profileRepository.setInviteCode(uiState.value.inviteCodeValue)
        }.doOnComplete {
            _uiState.update { it.copy(isLoading = false) }
        }.doOnSuccess {
            _command publish Command.HideBottomDialog
        }
    }

    fun onInviteCodeValueChanged(code: String) {
        _uiState.update {
            it.copy(inviteCodeValue = code)
        }
    }

    fun onDismissRequest() {
        _uiState.update {
            it.copy(isInviteUserDialogVisible = false)
        }
    }

    fun onCloseDialogClicked() {
        _uiState.update {
            it.copy(isInviteUserDialogVisible = false)
        }
    }

    fun onReferralCodeCopied() {
        viewModelScope.launch {
            notificationsSource.sendMessage(StringKey.ReferralProgramMessageReferralCodeCopied.textValue())
        }
    }

    fun onReferralLinkCopied() {
        viewModelScope.launch {
            notificationsSource.sendMessage(StringKey.ReferralProgramMessageReferralLinkCopied.textValue())
        }
    }

    fun onReferralProgramFootnoteClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(bottomDialog = BottomDialog.ReferralProgramFootnote) }
            _command publish Command.ShowBottomDialog
        }
    }

    fun onReferralsInvitedFootnoteClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(bottomDialog = BottomDialog.ReferralsInvitedFootnote) }
            _command publish Command.ShowBottomDialog
        }
    }

    fun onShowReferralQrClicked() = viewModelScope.launch {
        _uiState.update { it.copy(bottomDialog = BottomDialog.ReferralQr) }
        _command publish Command.ShowBottomDialog
    }

    fun onShareQrClicked(bitmap: Bitmap) = viewModelScope.launch {
        fileManager.createFileFromBitmap(bitmap)?.let {
            _command publish Command.OpenShareDialog(
                uri = fileManager.getUriForFile(it),
                text = "Download Snaps and use my referral code - ${uiState.value.referralCode}"
            )
        }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val referralCode: String = "",
        val referralLink: String = "",
        val inviteCodeValue: String = "",
        val bottomDialog: BottomDialog = BottomDialog.ReferralCode,
        val referralsTileState: ReferralsTileState = ReferralsTileState.Shimmer,
        val isInviteUserDialogVisible: Boolean = false,
        val referralQr: Bitmap? = null,
    ) {

        val isReferralCodeValid get() = inviteCodeValue.isNotBlank()
    }

    enum class BottomDialog {
        ReferralCode,
        ReferralQr,
        ReferralProgramFootnote,
        ReferralsInvitedFootnote,
    }

    sealed class Command {
        object ShowBottomDialog : Command()
        object HideBottomDialog : Command()
        data class OpenUserInfoScreen(val userId: Uuid) : Command()
        data class OpenShareDialog(val uri: Uri, val text: String) : Command()
    }
}