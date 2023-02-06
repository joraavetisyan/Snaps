package io.snaps.featureinitialization.viewmodel

import io.snaps.coreui.viewmodel.SimpleViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CreateUserViewModel @Inject constructor() : SimpleViewModel() {

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

    fun onTakePhotoClicked(hasImage: Boolean) {
        _uiState.update {
            it.copy(
                isDialogVisible = false,
                photoStatus = if (hasImage) PhotoStatus.Uploaded else PhotoStatus.NotUploaded,
            )
        }
    }

    fun onPickPhotoClicked(hasImage: Boolean) {
        _uiState.update {
            it.copy(
                isDialogVisible = false,
                photoStatus = if (hasImage) PhotoStatus.Uploaded else PhotoStatus.NotUploaded,
            )
        }
    }

    fun onDeleteClicked() {
        _uiState.update {
            it.copy(photoStatus = PhotoStatus.NotUploaded)
        }
    }

    fun onStartButtonClicked() { /*TODO*/ }

    fun onNickNameValueChanged(nickname: String) {
        _uiState.update {
            it.copy(nicknameValue = nickname)
        }
    }

    data class UiState(
        val isDialogVisible: Boolean = false,
        val nicknameValue: String = "",
        val photoStatus: PhotoStatus = PhotoStatus.NotUploaded,
    ) {
        val isStartButtonEnabled get() = nicknameValue.isNotBlank()
            && photoStatus == PhotoStatus.Uploaded
    }

    enum class PhotoStatus {
        Uploaded, NotUploaded,
    }

    sealed class Command
}