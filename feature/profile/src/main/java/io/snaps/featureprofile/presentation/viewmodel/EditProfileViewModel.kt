package io.snaps.featureprofile.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.corecommon.container.ImageValue
import io.snaps.coredata.di.Bridged
import io.snaps.coredata.network.Action
import io.snaps.coreui.FileManager
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.featureprofile.domain.EditProfileInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    @Bridged profileRepository: ProfileRepository,
    private val editProfileInteractor: EditProfileInteractor,
    private val fileManager: FileManager,
    private val action: Action,
) : SimpleViewModel() {

    private val _uiState = MutableStateFlow(
        UiState(
            name = profileRepository.state.value.dataOrCache?.name.orEmpty(),
            avatar = profileRepository.state.value.dataOrCache?.avatar
        )
    )
    val uiState = _uiState.asStateFlow()

    fun onUploadPhotoClicked() {
        _uiState.update { it.copy(isDialogVisible = true) }
    }

    fun onDismissRequest() {
        _uiState.update { it.copy(isDialogVisible = false) }
    }

    fun onNameValueChanged(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onTakePhotoClicked(imageUri: Uri?) = viewModelScope.launch {
        _uiState.update {
            it.copy(
                isDialogVisible = false,
                imageUri = imageUri,
                avatar = null,
            )
        }
        editAvatar()
    }

    fun onPickPhotoClicked(imageUri: Uri?) = viewModelScope.launch {
        _uiState.update {
            it.copy(
                isDialogVisible = false,
                imageUri = imageUri,
                avatar = null,
            )
        }
        editAvatar()
    }

    private fun editAvatar() = viewModelScope.launch {
        uiState.value.imageUri?.let { fileManager.createFileFromUri(it) }?.let {
            action.execute {
                _uiState.update { it.copy(isLoading = true) }
                editProfileInteractor.editAvatar(avatarFile = it)
            }.doOnComplete {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun editName() = viewModelScope.launch {
        action.execute {
            _uiState.update { it.copy(isLoading = true) }
            editProfileInteractor.editName(userName = uiState.value.name)
        }.doOnComplete {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    data class UiState(
        val isLoading: Boolean = false,
        val isDialogVisible: Boolean = false,
        val name: String = "",
        val avatar: ImageValue? = null,
        val imageUri: Uri? = null,
    ) {

        val isNameValid get() = Regex("^[a-zA-Z0-9_.]+$").matches(name)
    }
}