package com.defince.featuremain.data

import com.defince.corecommon.container.ImageValue
import com.defince.featuremain.presentation.screen.MainHeaderState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

interface MainHeaderHandler {

    val headerState: StateFlow<UiState>

    data class UiState(
        val value: MainHeaderState = MainHeaderState.Data(
            profileImage = ImageValue.Url("https://picsum.photos/44"),
            energy = "12",
            gold = "12",
            silver = "12",
            bronze = "12",
        ),
    )
}

class MainHeaderHandlerImplDelegate @Inject constructor() : MainHeaderHandler {

    private val _uiState = MutableStateFlow(MainHeaderHandler.UiState())
    override val headerState = _uiState.asStateFlow()
}