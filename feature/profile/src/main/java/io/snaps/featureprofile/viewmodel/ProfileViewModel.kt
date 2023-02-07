package io.snaps.featureprofile.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.corecommon.container.ImageValue
import io.snaps.coredata.network.Action
import io.snaps.coreui.viewmodel.SimpleViewModel
import io.snaps.coreui.viewmodel.publish
import io.snaps.featureprofile.screen.UserInfoTileState
import io.snaps.featureprofile.toUserInfoTileState
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
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val action: Action,
) : SimpleViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    init {
        subscribeOnProfile()
        loadProfile()
    }

    private fun subscribeOnProfile() {
        profileRepository.state.onEach { state ->
            _uiState.update {
                it.copy(userInfoTileState = state.toUserInfoTileState())
            }
        }.launchIn(viewModelScope)
    }

    private fun loadProfile() = viewModelScope.launch {
        action.execute {
            profileRepository.updateData()
        }
    }

    fun onSettingsClicked() = viewModelScope.launch {
        _command publish Command.OpenSettingsScreen
    }

    data class UiState(
        val isLoading: Boolean = true,
        val userInfoTileState: UserInfoTileState = UserInfoTileState.Shimmer,
        val images: List<Photo> = listOf(
            Photo(ImageValue.Url("https://picsum.photos/116/162"), "1,2M"),
            Photo(ImageValue.Url("https://picsum.photos/116/162"), "1,2M"),
            Photo(ImageValue.Url("https://picsum.photos/116/162"), "1,2M"),
            Photo(ImageValue.Url("https://picsum.photos/116/162"), "1,2M"),
            Photo(ImageValue.Url("https://picsum.photos/116/162"), "1,2M"),
            Photo(ImageValue.Url("https://picsum.photos/116/162"), "1,2M"),
            Photo(ImageValue.Url("https://picsum.photos/116/162"), "1,2M"),
            Photo(ImageValue.Url("https://picsum.photos/116/162"), "1,2M"),
            Photo(ImageValue.Url("https://picsum.photos/116/162"), "1,2M"),
            Photo(ImageValue.Url("https://picsum.photos/116/162"), "1,2M"),
            Photo(ImageValue.Url("https://picsum.photos/116/162"), "1,2M"),
            Photo(ImageValue.Url("https://picsum.photos/116/162"), "1,2M"),
            Photo(ImageValue.Url("https://picsum.photos/116/162"), "1,2M"),
            Photo(ImageValue.Url("https://picsum.photos/116/162"), "1,2M"),
            Photo(ImageValue.Url("https://picsum.photos/116/162"), "1,2M"),
            Photo(ImageValue.Url("https://picsum.photos/116/162"), "1,2M"),
            Photo(ImageValue.Url("https://picsum.photos/116/162"), "1,2M"),
        ),
    )

    sealed class Command {
        object OpenSettingsScreen : Command()
    }
}

data class Photo(
    val image: ImageValue,
    val views: String,
)