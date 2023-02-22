package io.snaps.featureinitialization.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.coredata.database.UserDataStorage
import io.snaps.coredata.network.Action
import io.snaps.coreui.FileManager
import io.snaps.coreui.FileType
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
class CreateUserViewModel @Inject constructor(
    private val fileManager: FileManager,
    private val profileRepository: ProfileRepository,
    private val action: Action,
    private val userDataStorage: UserDataStorage,
) : SimpleViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    fun onUploadPhotoClicked() {
        _uiState.update { it.copy(isDialogVisible = true) }
    }

    fun onDismissRequest() {
        _uiState.update { it.copy(isDialogVisible = false) }
    }

    fun onTakePhotoClicked(imageUri: Uri?) = viewModelScope.launch {
        _uiState.update {
            it.copy(
                isDialogVisible = false,
                photoStatus = if (imageUri != null) {
                    PhotoStatus.Uploaded
                } else PhotoStatus.NotUploaded,
                imageUri = imageUri,
            )
        }
    }

    fun onPickPhotoClicked(imageUri: Uri?) = viewModelScope.launch {
        _uiState.update {
            it.copy(
                isDialogVisible = false,
                photoStatus = if (imageUri != null) {
                    PhotoStatus.Uploaded
                } else PhotoStatus.NotUploaded,
                imageUri = imageUri,
            )
        }
    }

    fun onDeleteClicked() {
        _uiState.update {
            it.copy(photoStatus = PhotoStatus.NotUploaded)
        }
    }

    fun onStartButtonClicked() = viewModelScope.launch {
        fileManager.copyFileToInternalStorage(
            uri = uiState.value.imageUri,
            fileType = FileType.Pictures,
        )?.let {
            action.execute {
                _uiState.update { it.copy(isLoading = true) }
                profileRepository.createUser(it, uiState.value.nicknameValue)
            }.doOnComplete {
                _uiState.update { it.copy(isLoading = false) }
            }.doOnSuccess {
                if (userDataStorage.hasNft) {
                    _command publish Command.OpenRankSelectionScreen
                } else {
                    _command publish Command.OpenMainScreen
                }
            }
        }
    }

    fun onNickNameValueChanged(nickname: String) {
        _uiState.update {
            it.copy(nicknameValue = nickname)
        }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val isDialogVisible: Boolean = false,
        val nicknameValue: String = "",
        val imageUri: Uri? = null,
        val photoStatus: PhotoStatus = PhotoStatus.NotUploaded,
    ) {

        val isStartButtonEnabled
            get() = nicknameValue.isNotBlank()
                    && photoStatus == PhotoStatus.Uploaded
    }

    enum class PhotoStatus {
        Uploaded, NotUploaded,
    }

    sealed class Command {
        object OpenRankSelectionScreen : Command()
        object OpenMainScreen : Command()
    }
}