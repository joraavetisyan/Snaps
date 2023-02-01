package com.defince.featuremain.presentation.viewmodel

import com.defince.coreui.viewmodel.SimpleViewModel
import com.defince.featuremain.data.MainHeaderHandler
import com.defince.featuremain.data.demoReels
import com.defince.featuremain.domain.Reel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class PopularVideosViewModel @Inject constructor(
    mainHeaderHandlerDelegate: MainHeaderHandler,
) : SimpleViewModel(), MainHeaderHandler by mainHeaderHandlerDelegate {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    data class UiState(
        val reels: List<Reel> = demoReels,
    )

    sealed class Command
}