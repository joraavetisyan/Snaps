package io.snaps.featuretasks.presentation.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.basenft.data.NftRepository
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.baseprofile.data.model.SocialPostStatus
import io.snaps.baseprofile.domain.UserInfoModel
import io.snaps.basesession.AppRouteProvider
import io.snaps.basesources.NotificationsSource
import io.snaps.basewallet.data.WalletRepository
import io.snaps.corecommon.R
import io.snaps.corecommon.container.imageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.ext.toCompactDecimalFormat
import io.snaps.corecommon.model.TaskType
import io.snaps.corecommon.model.Uuid
import io.snaps.corecommon.strings.StringKey
import io.snaps.coredata.database.UserDataStorage
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.network.Action
import io.snaps.corenavigation.AppRoute
import io.snaps.coreui.FileManager
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.coreuicompose.uikit.listtile.CellTileState
import io.snaps.coreuicompose.uikit.listtile.LeftPart
import io.snaps.coreuicompose.uikit.listtile.MiddlePart
import io.snaps.coreuicompose.uikit.listtile.RightPart
import io.snaps.featuretasks.data.TasksRepository
import io.snaps.featuretasks.domain.ConnectInstagramInteractor
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class ShareTemplateViewModel @Inject constructor(
    private val action: Action,
    private val tasksRepository: TasksRepository,
    private val connectInstagramInteractor: ConnectInstagramInteractor,
    private val fileManager: FileManager,
    private val notificationsSource: NotificationsSource,
    private val userDataStorage: UserDataStorage,
    private val appRouteProvider: AppRouteProvider,
    @Bridged private val profileRepository: ProfileRepository,
    @Bridged private val nftRepository: NftRepository,
    @Bridged private val walletRepository: WalletRepository,
) : SimpleViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    init {
        subscribeOnCurrentUser()
        subscribeOnUserNft()
        subscribeOnMenuRouteState()
    }

    private fun subscribeOnCurrentUser() {
        profileRepository.state.onEach { state ->
            state.dataOrCache?.let { user ->
                _uiState.update {
                    it.copy(instagramConnectTileState = instagramTileState(user.instagramId))
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun subscribeOnUserNft() {
        nftRepository.nftCollectionState.combine(flow = walletRepository.snpsAccountState) { collection, balance ->
            val totalDailyReward = collection.dataOrCache?.sumOf { it.dailyReward.value } ?: 0.0
            val snpExchangeRate = balance.dataOrCache?.snpsUsdExchangeRate ?: 0.0
            totalDailyReward * snpExchangeRate
        }.onEach { state ->
            _uiState.update { it.copy(payments = state.toCompactDecimalFormat()) }
        }.launchIn(viewModelScope)
    }

    private fun subscribeOnMenuRouteState() {
        appRouteProvider.menuRouteState
            .filter { it == AppRoute.ShareTemplate.pattern }
            .onEach { _command publish Command.ShowBottomDialog }
            .launchIn(viewModelScope)
    }

    private fun instagramTileState(instagramUserId: Uuid?): CellTileState? {
        return if (instagramUserId != null) {
            CellTileState.Data(
                leftPart = LeftPart.Logo(
                    R.drawable.ic_instagram.imageValue(),
                ),
                middlePart = MiddlePart.Data(
                    value = instagramUserId.textValue(),
                ),
                rightPart = RightPart.DeleteIcon(
                    clickListener = ::onDeleteIconClicked,
                ),
            )
        } else {
            null
            /*CellTileState.Data(
                leftPart = LeftPart.Logo(
                    R.drawable.ic_instagram.imageValue(),
                ),
                middlePart = MiddlePart.Data(
                    value = StringKey.TaskShareTitleConnectInstagram.textValue(),
                ),
                rightPart = RightPart.ButtonData(
                    text = StringKey.TaskShareActionConnect.textValue(),
                    onClick = ::onConnectClicked,
                ),
            )*/
        }
    }

    private fun onDeleteIconClicked() = viewModelScope.launch {
        _uiState.update {
            it.copy(isLoading = true)
        }
        action.execute {
            connectInstagramInteractor.disconnectInstagram()
        }.doOnComplete {
            _uiState.update {
                it.copy(isLoading = false)
            }
        }
    }

    fun onShareIconClicked(bitmap: Bitmap) = viewModelScope.launch {
        fileManager.createFileFromBitmap(bitmap)?.let {
            _command publish Command.OpenShareDialog(uri = fileManager.getUriForFile(it))
        }
    }

    fun onSavePhotoButtonClicked(bitmap: Bitmap) = viewModelScope.launch {
        fileManager.saveMediaToExternalStorage(bitmap)?.let {
            notificationsSource.sendMessage(StringKey.TaskShareMessagePhotoSaved.textValue())
        }
    }

    fun onPostToInstagramButtonClicked() {
        viewModelScope.launch {
            val user = profileRepository.state.value.dataOrCache
            if (user?.instagramId == null) {
                notificationsSource.sendError(StringKey.TaskShareErrorNoInstagramConnected.textValue())
                return@launch
            }
            val socialShareTask = user.questInfo?.quests?.find { it.type == TaskType.SocialPost }
            if (socialShareTask?.status == SocialPostStatus.WaitForVerification) {
                notificationsSource.sendMessage(StringKey.TaskShareMessagePostInstagram.textValue())
                return@launch
            }
            if (!isAllowedToPost(user)) {
                notificationsSource.sendError(StringKey.TaskShareErrorPostToInstagramLimit.textValue())
                return@launch
            }
            _uiState.update { it.copy(isPostToInstagramEnabled = false) }
            action.execute {
                tasksRepository.postToInstagram()
            }.doOnSuccess {
                notificationsSource.sendMessage(StringKey.TaskShareMessagePostInstagram.textValue())
                onPosted(user)
                _command publish Command.BackToTasksScreen
            }.doOnError { _, _ ->
                _uiState.update { it.copy(isPostToInstagramEnabled = true) }
            }
        }
    }

    // todo delete once checked on backend
    private fun isAllowedToPost(userInfoModel: UserInfoModel?): Boolean {
        val date = userInfoModel?.questInfo?.questDate?.toInstant(ZoneOffset.UTC)?.toEpochMilli() ?: return true
        val maxCount = 3
        val currentCount = userDataStorage.getPostedInstagramTemplateCount(userInfoModel.userId, date)
        return (currentCount < maxCount)
    }

    private fun onPosted(userInfoModel: UserInfoModel?) {
        val date = userInfoModel?.questInfo?.questDate?.toInstant(ZoneOffset.UTC)?.toEpochMilli() ?: return
        val currentCount = userDataStorage.getPostedInstagramTemplateCount(userInfoModel.userId, date)
        userDataStorage.setPostedInstagramTemplateCount(userInfoModel.userId, date, currentCount + 1)
    }

    fun onInstagramUsernameChanged(value: String) {
        _uiState.update { it.copy(instagramUsernameValue = value) }
    }

    fun onInstagramConnectClicked() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        action.execute {
            connectInstagramInteractor.connectInstagramWithUsername(_uiState.value.instagramUsernameValue)
        }.doOnComplete {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onAuthCodeResultReceived(code: String) = viewModelScope.launch {
        _uiState.update {
            it.copy(isLoading = true)
        }
        action.execute {
            connectInstagramInteractor.connectInstagramWithAuthCode(code)
        }.doOnComplete {
            _uiState.update {
                it.copy(isLoading = false)
            }
        }
    }

    fun onContinueClicked() {
        viewModelScope.launch {
            _command publish Command.HideBottomDialog
        }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val instagramUsernameValue: String = "",
        val instagramConnectTileState: CellTileState? = CellTileState.Shimmer(
            leftPart = LeftPart.Shimmer,
            middlePart = MiddlePart.Shimmer(needValueLine = true),
            rightPart = RightPart.Shimmer(needLine = true),
        ),
        val payments: String = "0",
        val bottomDialog: BottomDialog = BottomDialog.Requirements,
        val isPostToInstagramEnabled: Boolean = true,
    ) {

        val instagramConnectAvailable get() = instagramUsernameValue.isNotEmpty()
    }

    enum class BottomDialog {
        Requirements,
    }

    sealed class Command {
        object OpenWebView : Command()
        data class OpenShareDialog(val uri: Uri) : Command()
        object BackToTasksScreen : Command()
        object ShowBottomDialog : Command()
        object HideBottomDialog : Command()
    }
}