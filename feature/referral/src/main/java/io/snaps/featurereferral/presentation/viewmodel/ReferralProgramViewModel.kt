package io.snaps.featurereferral.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.baseprofile.domain.UserInfoModel
import io.snaps.basesession.data.OnboardingHandler
import io.snaps.basesources.BottomBarVisibilitySource
import io.snaps.basesources.NotificationsSource
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.OnboardingType
import io.snaps.corecommon.model.Uuid
import io.snaps.corecommon.strings.StringKey
import io.snaps.corecommon.strings.addPrefix
import io.snaps.coredata.network.Action
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
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
    private val profileRepository: ProfileRepository,
    private val action: Action,
    private val bottomBarVisibilitySource: BottomBarVisibilitySource,
    private val notificationsSource: NotificationsSource,
) : SimpleViewModel(),
    MainHeaderHandler by mainHeaderHandlerDelegate,
    OnboardingHandler by onboardingHandlerDelegate {

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
            _uiState.update {
                val inviteCode = state.dataOrCache?.ownInviteCode.orEmpty()
                it.copy(
                    referralCode = inviteCode.addPrefix("#"),
                    referralLink = inviteCode.addPrefix("https://snaps.io/"),
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun subscribeOnReferrals() {
        profileRepository.referralsState.onEach { state ->
            _uiState.update {
                it.copy(
                    referralsUiState = state.toReferralsUiState(),
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

    fun onEnterCodeClicked() = viewModelScope.launch {
        _uiState.update {
            it.copy(bottomDialog = BottomDialog.ReferralCode)
        }
        bottomBarVisibilitySource.updateState(false)
        _command publish Command.ShowBottomDialog
    }

    fun onBottomSheetHidden() {
        bottomBarVisibilitySource.updateState(true)
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

    fun onReferralClick(model: UserInfoModel) {
        viewModelScope.launch {
            _command publish Command.OpenUserInfoScreen(model.entityId)
        }
    }

    fun onReferralsReloadClick() {
        updateReferrals()
    }

    fun onReferralProgramFootnoteClick() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(bottomDialog = BottomDialog.ReferralProgram)
            }
            bottomBarVisibilitySource.updateState(false)
            _command publish Command.ShowBottomDialog
        }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val referralCode: String = "",
        val referralLink: String = "",
        val inviteCodeValue: String = "",
        val bottomDialog: BottomDialog = BottomDialog.ReferralCode,
        val referralsUiState: ReferralsUiState = ReferralsUiState.Shimmer,
        val isInviteUserDialogVisible: Boolean = false,
    ) {

        val isReferralCodeValid get() = inviteCodeValue.isNotBlank()
    }

    enum class BottomDialog {
        ReferralCode,
        ReferralQr,
        ReferralProgram,
        ReferralsInvited,
    }

    sealed class Command {
        object ShowBottomDialog : Command()
        object HideBottomDialog : Command()
        data class OpenUserInfoScreen(val userId: Uuid) : Command()
    }
}

sealed class ReferralsUiState {

    data class Data(
        val values: List<UserInfoModel>,
    ) : ReferralsUiState()

    object Empty : ReferralsUiState()

    object Shimmer : ReferralsUiState()

    object Error : ReferralsUiState()
}