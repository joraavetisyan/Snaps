package com.defince.featuremain.presentation.viewmodel

import com.defince.basesources.LocationSource
import com.defince.corecommon.container.ImageValue
import com.defince.coreui.viewmodel.SimpleViewModel
import com.defince.featuremain.data.StubApi
import com.defince.featuremain.domain.Nft
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val source: LocationSource,
    private val api: StubApi,
) : SimpleViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    data class UiState(
        val isLoading: Boolean = false,
        val profileImage: ImageValue = ImageValue.Url("https://picsum.photos/76"),
        val likes: String = "1,2M",
        val subscribers: String = "32,6k",
        val subscriptions: String = "26,3k",
        val publication: String = "10",
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

    sealed class Command
}

data class Photo(
    val image: ImageValue,
    val views: String,
)