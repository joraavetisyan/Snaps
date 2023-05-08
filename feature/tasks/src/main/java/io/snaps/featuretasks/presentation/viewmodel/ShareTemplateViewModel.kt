package io.snaps.featuretasks.presentation.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.basesources.NotificationsSource
import io.snaps.corecommon.R
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.Uuid
import io.snaps.corecommon.strings.StringKey
import io.snaps.coredata.network.Action
import io.snaps.coreui.FileManager
import io.snaps.coreui.barcode.BarcodeManager
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShareTemplateViewModel @Inject constructor(
    barcodeManager: BarcodeManager,
    private val action: Action,
    private val tasksRepository: TasksRepository,
    private val profileRepository: ProfileRepository,
    private val connectInstagramInteractor: ConnectInstagramInteractor,
    private val fileManager: FileManager,
    private val notificationsSource: NotificationsSource,
) : SimpleViewModel() {

    private val _uiState = MutableStateFlow(
        UiState(
            qr = barcodeManager.getQrCodeBitmap(
                text = "https://snapsapp.io/", // todo link to google play
                size = 300f,
            ),
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    init {
        subscribeOnCurrentUser()
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

    private fun instagramTileState(instagramUserId: Uuid?): CellTileState {
        return if (instagramUserId != null) {
            CellTileState.Data(
                leftPart = LeftPart.Logo(
                    ImageValue.ResImage(R.drawable.ic_instagram)
                ),
                middlePart = MiddlePart.Data(
                    value = instagramUserId.textValue()
                ),
                rightPart = RightPart.DeleteIcon(
                    clickListener = ::onDeleteIconClicked,
                ),
            )
        } else {
            CellTileState.Data(
                leftPart = LeftPart.Logo(
                    ImageValue.ResImage(R.drawable.ic_instagram),
                ),
                middlePart = MiddlePart.Data(
                    value = StringKey.TaskShareTitleConnectInstagram.textValue(),
                ),
                rightPart = RightPart.ButtonData(
                    text = StringKey.TaskShareActionConnect.textValue(),
                    onClick = ::onConnectClicked,
                ),
            )
        }
    }

    private fun onConnectClicked() = viewModelScope.launch {
        _command publish Command.OpenWebView
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
            _command publish Command.OpenShareDialog(
                uri = fileManager.getUriForFile(it)
            )
        }
    }

    fun onSavePhotoButtonClicked(bitmap: Bitmap) = viewModelScope.launch {
        fileManager.saveMediaToExternalStorage(bitmap)?.let {
            notificationsSource.sendMessage(StringKey.TaskShareMessagePhotoSaved.textValue())
        }
    }

    fun onPostToInstagramButtonClicked(bitmap: Bitmap) = viewModelScope.launch {
        val instagramId = profileRepository.state.value.dataOrCache?.instagramId
        if (instagramId != null) {
            fileManager.createFileFromBitmap(bitmap)?.let {
                action.execute {
                    tasksRepository.postToInstagram()
                }
            }
        } else {
            onConnectClicked()
        }
    }

    fun onAuthCodeResultReceived(code: String) = viewModelScope.launch {
        _uiState.update {
            it.copy(isLoading = true)
        }
        action.execute {
            connectInstagramInteractor.connectInstagram(code)
        }.doOnComplete {
            _uiState.update {
                it.copy(isLoading = false)
            }
        }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val instagramConnectTileState: CellTileState = CellTileState.Shimmer(
            leftPart = LeftPart.Shimmer,
            middlePart = MiddlePart.Shimmer(needValueLine = true),
            rightPart = RightPart.Shimmer(needLine = true),
        ),
        val qr: Bitmap?,
    )

    sealed class Command {
        object OpenWebView : Command()
        data class OpenShareDialog(val uri: Uri) : Command()
    }
}