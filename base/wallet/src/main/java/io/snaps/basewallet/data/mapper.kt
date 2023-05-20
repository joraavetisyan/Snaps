package io.snaps.basewallet.data

import io.horizontalsystems.ethereumkit.api.jsonrpc.JsonRpc
import io.horizontalsystems.marketkit.models.TokenType
import io.snaps.basewallet.data.model.SnpsAccountResponseDto
import io.snaps.basewallet.domain.SnpsAccountModel
import io.snaps.basewallet.domain.TotalBalanceModel
import io.snaps.basewallet.domain.WalletModel
import io.snaps.corecommon.R
import io.snaps.corecommon.container.imageValue
import io.snaps.corecommon.ext.stringAmountToDoubleOrZero
import io.snaps.corecommon.model.AppError
import io.snaps.corecommon.model.CoinBNB
import io.snaps.corecommon.model.CoinSNPS
import io.snaps.corecommon.model.CoinType
import io.snaps.corecommon.model.CoinValue
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.FiatUSD
import io.snaps.corecommon.model.State
import io.snaps.corecrypto.core.CryptoKit
import io.snaps.corecrypto.core.providers.BalanceViewItem
import io.snaps.corecrypto.core.providers.TotalUIState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.CancellationException

suspend inline fun <reified T : Any> blockchainCall(
    dispatcher: CoroutineDispatcher,
    noinline block: suspend () -> T,
): Effect<T> = withContext(dispatcher) {
    try {
        Effect.success(block())
    } catch (t: Throwable) {
        if (t is CancellationException) throw t

        val error = (t.cause as? JsonRpc.ResponseError.RpcError)?.error?.let {
            Exception(it.code.toString() + " " + it.message)
        } ?: (t.cause as? JsonRpc.ResponseError.InvalidResult)?.let {
            Exception(it.toString())
        } ?: t as? Exception
        Effect.error(AppError.Unknown(cause = error))
    }
}

fun SnpsAccountResponseDto.toModel() = SnpsAccountModel(
    unlocked = CoinSNPS(unlockedTokensBalance),
    locked = CoinSNPS(lockedTokensBalance),
    snpsUsdExchangeRate = snpsExchangeRate,
    bnbUsdExchangeRate = bnbExchangeRate,
)

fun BalanceViewItem.toWalletModel(balance: State<SnpsAccountModel>): WalletModel? {
    return if (wallet.token.type == TokenType.Native) {
        CoinType.BNB
    } else {
        CoinType.byAddress(wallet.tokenAddress.orEmpty())
    }?.let {
        val coinValue = CoinValue(it, primaryValue.value.stringAmountToDoubleOrZero())
        WalletModel(
            coinUid = wallet.coin.uid,
            coinType = it,
            image = if (it == CoinType.SNPS) R.drawable.ic_snp_token.imageValue() else coinIconUrl.imageValue(),
            coinValue = coinValue,
            fiatValue = if (it == CoinType.SNPS) {
                coinValue.toFiat(balance.dataOrCache?.snpsUsdExchangeRate ?: 0.0)
            } else {
                FiatUSD(secondaryValue.value.stringAmountToDoubleOrZero())
            },
            receiveAddress = CryptoKit.adapterManager.getReceiveAdapterForWallet(wallet)?.receiveAddress.orEmpty(),
        )
    }
}

fun totalBalanceModel(
    totalUIState: TotalUIState,
    snps: WalletModel?,
    balance: State<SnpsAccountModel>,
): TotalBalanceModel {
    return if (totalUIState !is TotalUIState.Visible) {
        TotalBalanceModel.empty
    } else {
        TotalBalanceModel(
            coin = run {
                val bnb = CoinBNB(totalUIState.primaryAmountStr.stringAmountToDoubleOrZero())
                snps?.coinValue?.toCoin(balance.dataOrCache?.snpsBnbExchangeRate ?: 0.0)?.plus(bnb) ?: bnb
            },
            fiat = run {
                val bnbInUsd = FiatUSD(totalUIState.secondaryAmountStr.stringAmountToDoubleOrZero())
                snps?.fiatValue?.plus(bnbInUsd) ?: bnbInUsd
            },
        )
    }
}