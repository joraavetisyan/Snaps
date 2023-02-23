package io.snaps.corecrypto.core.providers

import io.horizontalsystems.marketkit.models.Token
import io.snaps.corecrypto.core.ISendBinanceAdapter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.math.BigDecimal

class SendBinanceFeeService(
    private val adapter: ISendBinanceAdapter,
    private val token: Token,
    private val feeTokenProvider: FeeTokenProvider
) {
    private val feeTokenData = feeTokenProvider.feeTokenData(token)
    val feeToken = feeTokenData?.first ?: token

    private val fee = adapter.fee

    private val _stateFlow = MutableStateFlow(
        State(
            fee = fee,
        )
    )
    val stateFlow = _stateFlow.asStateFlow()

    fun start() {

        emitState()
    }

    private fun emitState() {
        _stateFlow.update {
            State(
                fee = fee,
            )
        }
    }

    data class State(
        val fee: BigDecimal,
    )
}
