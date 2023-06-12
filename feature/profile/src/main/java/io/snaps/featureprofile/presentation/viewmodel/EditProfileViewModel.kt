package io.snaps.featureprofile.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.baseprofile.domain.EditUserInteractor
import io.snaps.baseprofile.domain.UserInfoModel
import io.snaps.corecommon.container.ImageValue
import io.snaps.corecommon.model.Effect
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.network.Action
import io.snaps.coreui.FileManager
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
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
class EditProfileViewModel @Inject constructor(
    @Bridged profileRepository: ProfileRepository,
    @Bridged private val interactor: EditUserInteractor,
    private val fileManager: FileManager,
    private val action: Action,
) : SimpleViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    init {
        profileRepository.state.onEach { state ->
            if (state is Effect<UserInfoModel>) {
                _uiState.update {
                    it.copy(
                        name = state.dataOrCache?.name.orEmpty(),
                        editNameValue = state.dataOrCache?.name.orEmpty(),
                        avatar = state.dataOrCache?.avatar
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    fun onUploadPhotoClicked() {
        _uiState.update { it.copy(isDialogVisible = true) }
    }

    fun onDismissRequest() {
        _uiState.update { it.copy(isDialogVisible = false) }
    }

    fun onNameValueChanged(name: String) {
        _uiState.update { it.copy(editNameValue = name) }
    }

    fun onTakePhotoClicked(imageUri: Uri?) {
        onPhotoSelectFromGallery(imageUri)
    }

    fun onPickPhotoClicked(imageUri: Uri?) {
        onPhotoSelectFromGallery(imageUri)
    }

    private fun onPhotoSelectFromGallery(imageUri: Uri?) {
        viewModelScope.launch {
            if (imageUri == null) {
                _uiState.update { it.copy(isDialogVisible = false) }
                return@launch
            }
            _uiState.update {
                it.copy(
                    isDialogVisible = false,
                    imageUri = imageUri,
                    avatar = null,
                )
            }
            editAvatar(imageUri)
        }
    }

    private fun editAvatar(imageUri: Uri) = viewModelScope.launch {
        fileManager.createFileFromUri(imageUri)?.let {
            action.execute {
                _uiState.update { it.copy(isLoading = true) }
                interactor.editUser(avatarFile = it)
            }.doOnComplete {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun editName() = viewModelScope.launch {
        if (uiState.value.name != uiState.value.editNameValue) {
            action.execute {
                _uiState.update { it.copy(isLoading = true) }
                interactor.editUser(userName = uiState.value.editNameValue)
            }.doOnComplete {
                _uiState.update { it.copy(isLoading = false) }
            }.doOnSuccess {
                _uiState.update { it.copy(name = uiState.value.editNameValue) }
                _command publish Command.CloseScreen
            }
        } else {
            _command publish Command.CloseScreen
        }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val isDialogVisible: Boolean = false,
        val name: String = "",
        val avatar: ImageValue? = null,
        val imageUri: Uri? = null,
        val editNameValue: String = "",
    ) {
        val isNameValid get() = isNameValid(editNameValue)
    }

    sealed class Command {
        object CloseScreen : Command()
    }
}

private fun isNameValid(name: String): Boolean {
    return Regex("^\\p{L}+[\\p{L}\\p{Nd}_.]*$").matches(name)
}