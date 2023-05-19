package io.snaps.featureinitialization.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.basesession.data.SessionRepository
import io.snaps.corecommon.container.ImageValue
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.network.Action
import io.snaps.coreui.FileManager
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.featureinitialization.domain.CreateUserInteractor
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
    private val interactor: CreateUserInteractor,
    private val action: Action,
    @Bridged private val profileRepository: ProfileRepository,
    private val sessionRepository: SessionRepository,
) : SimpleViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    init {
        viewModelScope.launch {
            profileRepository.updateData().doOnSuccess { user ->
                _uiState.update { it.copy(nicknameValue = user.name, avatar = user.avatar) }
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
        uiState.value.imageUri?.let { fileManager.createFileFromUri(it) }?.let {
            action.execute {
                _uiState.update { it.copy(isLoading = true) }
                interactor.createUser(avatarFile = it, userName = uiState.value.nicknameValue)
            }.doOnError { _, _ ->
                _uiState.update { it.copy(isLoading = false) }
            }.doOnSuccess {
                handleCreate()
            }
        } ?: run {
            val currentName = profileRepository.state.value.dataOrCache?.name
            val enteredName = uiState.value.nicknameValue
            if (currentName == enteredName && uiState.value.avatar != null) {
                handleCreate()
            }
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

        val isStartButtonEnabled
            get() = nicknameValue.isNotBlank()
                    && (photoStatus == PhotoStatus.Uploaded || avatar != null)
    }

    enum class PhotoStatus {
        Uploaded, NotUploaded,
    }

    sealed class Command
}