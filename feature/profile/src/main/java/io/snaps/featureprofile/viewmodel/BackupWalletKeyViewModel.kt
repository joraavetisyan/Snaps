package io.snaps.featureprofile.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.coreui.viewmodel.SimpleViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class BackupWalletKeyViewModel @Inject constructor() : SimpleViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _command = Channel<Command>()
    val command = _command.receiveAsFlow()

    data class UiState(
        val phrase: List<Phrase> = listOf(
            Phrase("load", 1),
            Phrase("good", 2),
            Phrase("mamba", 3),
            Phrase("sweet", 4),
            Phrase("come", 5),
            Phrase("ligekscg", 6),
            Phrase("moloko", 7),
            Phrase("sooq", 8),
            Phrase("eghtins", 9),
            Phrase("weluy", 10),
            Phrase("game", 11),
            Phrase("opeda", 12),
        ),
    )

    sealed class Command
}

data class Phrase(
    val text: String,
    val orderNumber: Int,
)