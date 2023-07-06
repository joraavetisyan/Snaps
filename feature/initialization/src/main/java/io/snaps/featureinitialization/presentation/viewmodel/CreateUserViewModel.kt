package io.snaps.featureinitialization.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.baseprofile.domain.EditUserInteractor
import io.snaps.basesession.data.SessionRepository
import io.snaps.basesources.NotificationsSource
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.container.imageValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.strings.StringKey
import io.snaps.corecommon.strings.isUserNameValid
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.network.Action
import io.snaps.coreui.FileManager
import io.snaps.coreui.viewmodel.SimpleViewModel
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
    private val interactor: EditUserInteractor,
    private val action: Action,
    @Bridged private val profileRepository: ProfileRepository,
    private val sessionRepository: SessionRepository,
    private val notificationsSource: NotificationsSource,
) : SimpleViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    init {
        viewModelScope.launch {
            profileRepository.updateData().doOnSuccess { user ->
                _uiState.update {
                    it.copy(
                        nicknameValue = user.name,
                        avatar = user.avatarUrl?.imageValue(),
                        photoStatus = if (user.avatarUrl != null) {
                            PhotoStatus.Uploaded
                        } else PhotoStatus.NotUploaded,
                    )
                }
            }
        }
    }

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
                avatar = null,
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
                avatar = null,
            )
        }
    }

    fun onDeleteClicked() {
        _uiState.update {
            it.copy(photoStatus = PhotoStatus.NotUploaded, avatar = null)
        }
    }

    fun onStartButtonClicked() = viewModelScope.launch {
        val avatarFile = uiState.value.imageUri?.let { fileManager.createFileFromUri(it) }
        if (!uiState.value.nicknameValue.isUserNameValid()) {
            notificationsSource.sendError(StringKey.ErrorUserNameInvalid.textValue())
            return@launch
        }
        action.execute {
            _uiState.update { it.copy(isLoading = true) }
            interactor.editUser(avatarFile = avatarFile, userName = uiState.value.nicknameValue)
        }.doOnSuccess {
            handleCreate()
        }.doOnComplete {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun handleCreate() {
        sessionRepository.onInitialized()
    }

    fun onNickNameValueChanged(value: String) {
        _uiState.update {
            it.copy(nicknameValue = value)
        }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val isDialogVisible: Boolean = false,
        val avatar: ImageValue? = null,
        val nicknameValue: String = "",
        val imageUri: Uri? = null,
        val photoStatus: PhotoStatus = PhotoStatus.NotUploaded,
    ) {

        val isStartButtonEnabled get() = nicknameValue.isNotBlank()
    }

    enum class PhotoStatus {
        Uploaded, NotUploaded,
    }

    sealed class Command
}