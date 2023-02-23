package io.snaps.corecrypto.core.providers

import io.snaps.corecrypto.core.CryptoKit
import io.snaps.corecrypto.core.managers.BalanceHiddenManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class TotalUIState {
    data class Visible(
        val primaryAmountStr: String,
        val secondaryAmountStr: String,
        val dimmed: Boolean
    ) : TotalUIState()

    object Hidden : TotalUIState()

}

interface ITotalBalance {
    val balanceHidden: Boolean
    val totalUiState: StateFlow<TotalUIState>

    fun toggleBalanceVisibility()
    fun toggleTotalType()
    fun setTotalServiceItems(map: List<TotalService.BalanceItem>?)
    fun start(viewModelScope: CoroutineScope)
}

class TotalBalance(
    private val totalService: TotalService,
    private val balanceHiddenManager: BalanceHiddenManager,
) : ITotalBalance {

    private var totalState = totalService.stateFlow.value

    override val balanceHidden by balanceHiddenManager::balanceHidden

    private val _totalUiState = MutableStateFlow(createTotalUIState())
    override val totalUiState = _totalUiState.asStateFlow()

    override fun start(viewModelScope: CoroutineScope) {
        viewModelScope.launch {
            totalService.stateFlow.collect {
                totalState = it
                _totalUiState.update { createTotalUIState() }
            }
        }
        totalService.start()
    }

    private fun createTotalUIState() = when (val state = totalState) {
        TotalService.State.Hidden -> TotalUIState.Hidden
        is TotalService.State.Visible -> TotalUIState.Visible(
            primaryAmountStr = getPrimaryAmount(state) ?: "---",
            secondaryAmountStr = getSecondaryAmount(state) ?: "---",
            dimmed = state.dimmed
        )
    }

    fun stop() {
        totalService.stop()
    }

    override fun setTotalServiceItems(map: List<TotalService.BalanceItem>?) {
        totalService.setItems(map)
    }

    override fun toggleBalanceVisibility() {
        balanceHiddenManager.toggleBalanceHidden()
    }

    override fun toggleTotalType() {
        totalService.toggleType()
    }

    private fun getPrimaryAmount(
        totalState: TotalService.State.Visible
    ) = totalState.coinValue?.let {
        "~" + CryptoKit.numberFormatter.formatCoinFull(it.value, it.coin.code, it.decimal)
    }

    private fun getSecondaryAmount(
        totalState: TotalService.State.Visible
    ) = totalState.currencyValue?.let {
        CryptoKit.numberFormatter.formatFiatFull(it.value, it.currency.symbol)
    }
}
