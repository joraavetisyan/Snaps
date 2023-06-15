package io.snaps.featurereferral.presentation.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.snaps.baseprofile.data.MainHeaderHandler
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.baseprofile.domain.UserInfoModel
import io.snaps.basesession.data.OnboardingHandler
import io.snaps.basesources.BottomDialogBarVisibilityHandler
import io.snaps.basesources.NotificationsSource
import io.snaps.corecommon.R
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.ext.toPercentageFormat
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.OnboardingType
import io.snaps.corecommon.model.Uuid
import io.snaps.corecommon.strings.StringKey
import io.snaps.corecommon.strings.addPrefix
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.network.Action
import io.snaps.corenavigation.AppDeeplink
import io.snaps.corenavigation.AppRoute
import io.snaps.corenavigation.base.getArg
import io.snaps.coreui.FileManager
import io.snaps.coreui.barcode.BarcodeManager
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.featurereferral.presentation.screen.ReferralsTileState
import io.snaps.featurereferral.presentation.toReferralsUiState
import kotlinx.coroutines.CoroutineDispatcher
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
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @Bridged mainHeaderHandler: MainHeaderHandler,
    @Bridged onboardingHandler: OnboardingHandler,
    bottomDialogBarVisibilityHandler: BottomDialogBarVisibilityHandler,
    private val fileManager: FileManager,
    private val barcodeManager: BarcodeManager,
    @Bridged private val profileRepository: ProfileRepository,
    private val action: Action,
    private val notificationsSource: NotificationsSource,
) : SimpleViewModel(),
    MainHeaderHandler by mainHeaderHandler,
    OnboardingHandler by onboardingHandler,
    BottomDialogBarVisibilityHandler by bottomDialogBarVisibilityHandler {

    private val args = savedStateHandle.getArg<AppRoute.ReferralProgram.Args>()

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(
        UiState(referralsTileState = ReferralsTileState.Shimmer(::onShowReferralQrClicked))
    )
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    init {
        subscribeOnCurrentUser()
        subscribeOnReferrals()

        updateReferrals()

        checkOnboarding(OnboardingType.Referral)
        args?.referralCode?.let {
            applyReferralCode(it)
        }
    }

    private fun subscribeOnCurrentUser() {
        profileRepository.state.onEach { state ->
            if (state is Effect && state.isSuccess) {
                _uiState.update {
                    // It's not null for the authed user
                    val inviteCode = state.requireData.ownInviteCode!!
                    generateReferralCode(inviteCode)
                    it.copy(
                        referralCode = inviteCode.addPrefix("#"),
                        referralLink = AppDeeplink.generateSharingLink(deeplink = AppDeeplink.Invite(code = inviteCode)),
                        firstLevelReferral = state.requireData.firstLevelReferralMultiplier.toPercentageFormat(),
                        secondLevelReferral = state.requireData.secondLevelReferralMultiplier.toPercentageFormat(),
                        invitedByCode = state.requireData.inviteCodeRegisteredBy.orEmpty(),
                        isInviteAvailable = state.requireData.inviteCodeRegisteredBy.isNullOrBlank(),
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun generateReferralCode(inviteCode: String) {
        viewModelScope.launch(ioDispatcher) {
            val template = _uiState.value.template ?: BitmapFactory.decodeResource(
                context.resources, R.drawable.img_template_referral
            )
            val referralQr = barcodeManager.getQrCodeBitmap(text = inviteCode, size = template.width / 6f)
            _uiState.update {
                it.copy(template = template, referralQr = referralQr)
            }
        }
    }

    private fun applyReferralCode(code: String) {
        if (uiState.value.isInviteAvailable) {
            _uiState.update { it.copy(dialog = Dialog.ApplyReferralCode(code)) }
        } else {
            _uiState.update { it.copy(dialog = Dialog.ReferralCodeEntered) }
        }
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
            _command publish Command.OpenUserInfoScreen(model.userId)
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
            it.copy(dialog = Dialog.InviteUser)
        }
    }

    fun onReferralCodeDialogButtonClicked() {
        setInviteCode(uiState.value.inviteCodeValue) {
            viewModelScope.launch {
                _command publish Command.HideBottomDialog
            }
        }
    }

    fun onInviteCodeValueChanged(code: String) {
        _uiState.update {
            it.copy(inviteCodeValue = code)
        }
    }

    fun onDismissRequest() {
        _uiState.update {
            it.copy(dialog = null)
        }
    }

    fun onCloseDialogClicked() {
        _uiState.update {
            it.copy(dialog = null)
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

    fun onShowReferralQrClicked() {
        if (_uiState.value.template == null || _uiState.value.referralQr == null) return
        viewModelScope.launch {
            _uiState.update { it.copy(bottomDialog = BottomDialog.ReferralQr) }
            _command publish Command.ShowBottomDialog
        }
    }

    fun onShareQrClicked(bitmap: Bitmap) = viewModelScope.launch {
        fileManager.createFileFromBitmap(bitmap)?.let {
            _command publish Command.OpenShareDialog(
                uri = fileManager.getUriForFile(it),
                code = uiState.value.referralCode,
            )
        }
    }

    fun onApplyReferralCodeClicked(code: String) {
        setInviteCode(code) {
            _uiState.update { it.copy(dialog = null) }
        }
    }

    private fun setInviteCode(code: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            action.execute {
                profileRepository.setInviteCode(code)
            }.doOnComplete {
                _uiState.update { it.copy(isLoading = false) }
            }.doOnSuccess {
                onSuccess()
            }
        }
    }

    data class UiState(
        val referralsTileState: ReferralsTileState,
        val isLoading: Boolean = false,
        val referralCode: String = "",
        val referralLink: String = "",
        val inviteCodeValue: String = "",
        val invitedByCode: String = "",
        val bottomDialog: BottomDialog = BottomDialog.ReferralCode,
        val template: Bitmap? = null,
        val referralQr: Bitmap? = null,
        val firstLevelReferral: String = "",
        val secondLevelReferral: String = "",
        val isInviteAvailable: Boolean = false,
        val dialog: Dialog? = null,
    ) {

        val isReferralCodeValid get() = inviteCodeValue.isNotBlank()
    }

    enum class BottomDialog {
        ReferralCode,
        ReferralQr,
        ReferralProgramFootnote,
        ReferralsInvitedFootnote,
    }

    sealed class Dialog {
        object InviteUser : Dialog()
        object ReferralCodeEntered : Dialog()
        data class ApplyReferralCode(val code: String) : Dialog()
    }

    sealed class Command {
        object ShowBottomDialog : Command()
        object HideBottomDialog : Command()
        data class OpenUserInfoScreen(val userId: Uuid) : Command()
        data class OpenShareDialog(val uri: Uri, val code: String) : Command()
    }
}