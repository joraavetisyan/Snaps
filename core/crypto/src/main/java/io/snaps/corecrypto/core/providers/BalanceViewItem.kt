package io.snaps.corecrypto.core.providers

import androidx.compose.runtime.Immutable
import io.horizontalsystems.marketkit.models.BlockchainType
import io.horizontalsystems.marketkit.models.CoinPrice
import io.horizontalsystems.marketkit.models.Token
import io.snaps.corecrypto.core.AdapterState
import io.snaps.corecrypto.core.CryptoKit
import io.snaps.corecrypto.core.iconPlaceholder
import io.snaps.corecrypto.core.swappable
import io.snaps.corecrypto.entities.Currency
import io.snaps.corecrypto.entities.Wallet
import io.snaps.corecrypto.other.iconUrl
import io.snaps.corecrypto.walletconnect.other.BalanceViewType
import java.math.BigDecimal

@Immutable
data class BalanceViewItem(
    val wallet: Wallet,
    val currencySymbol: String,
    val coinCode: String,
    val coinTitle: String,
    val coinIconUrl: String,
    val coinIconPlaceholder: Int,
    val primaryValue: DeemedValue<String>,
    val exchangeValue: DeemedValue<String>,
    val diff: BigDecimal?,
    val secondaryValue: DeemedValue<String>,
    val coinValueLocked: DeemedValue<String>,
    val fiatValueLocked: DeemedValue<String>,
    val expanded: Boolean,
    val sendEnabled: Boolean = false,
    val syncingProgress: SyncingProgress,
    val failedIconVisible: Boolean,
    val coinIconVisible: Boolean,
    val badge: String?,
    val swapVisible: Boolean,
    val swapEnabled: Boolean = false,
    val mainNet: Boolean,
    val errorMessage: String?,
    val isWatchAccount: Boolean
)

data class DeemedValue<T>(val value: T, val dimmed: Boolean = false, val visible: Boolean = true)
data class SyncingProgress(val progress: Int?, val dimmed: Boolean = false)

class BalanceViewItemFactory {

    private fun coinValue(
        state: AdapterState?,
        balance: BigDecimal,
        coinDecimals: Int
    ): DeemedValue<String> {
        val dimmed = state !is AdapterState.Synced
        val formatted =
            CryptoKit.numberFormatter.formatCoinFull(balance, null, coinDecimals)

        return DeemedValue(formatted, dimmed)
    }

    private fun currencyValue(
        state: AdapterState?,
        balance: BigDecimal,
        coinPrice: CoinPrice?,
        currency: Currency
    ): DeemedValue<String> {
        val dimmed = state !is AdapterState.Synced || coinPrice?.expired ?: false
        val formatted = coinPrice?.value?.let { rate ->
            val balanceFiat = balance.multiply(rate)

                CryptoKit.numberFormatter.formatFiatFull(balanceFiat, currency.symbol)
        } ?: ""

        return DeemedValue(formatted, dimmed)
    }

    private fun rateValue(
        coinPrice: CoinPrice?,
        showSyncing: Boolean,
        currency: Currency
    ): DeemedValue<String> {
        val value = coinPrice?.let {
            CryptoKit.numberFormatter.formatFiatFull(coinPrice.value, currency.symbol)
        } ?: ""

        return DeemedValue(value, dimmed = coinPrice?.expired ?: false, visible = !showSyncing)
    }

    private fun getSyncingProgress(
        state: AdapterState?,
        blockchainType: BlockchainType
    ): SyncingProgress {
        return when (state) {
            is AdapterState.Syncing -> SyncingProgress(
                state.progress ?: getDefaultSyncingProgress(
                    blockchainType
                ), false
            )
            is AdapterState.SearchingTxs, is AdapterState.Zcash -> SyncingProgress(10, true)
            else -> SyncingProgress(null, false)
        }
    }

    private fun getDefaultSyncingProgress(blockchainType: BlockchainType) = when (blockchainType) {
        BlockchainType.Bitcoin,
        BlockchainType.BitcoinCash,
        BlockchainType.Litecoin,
        BlockchainType.Dash,
        BlockchainType.Zcash -> 10
        BlockchainType.Ethereum,
        BlockchainType.EthereumGoerli,
        BlockchainType.BinanceSmartChain,
        BlockchainType.BinanceChain,
        BlockchainType.Polygon,
        BlockchainType.Avalanche,
        BlockchainType.Optimism,
        BlockchainType.Solana,
        BlockchainType.Gnosis,
        BlockchainType.ArbitrumOne -> 50
        BlockchainType.Solana -> 50
        is BlockchainType.Unsupported -> 0
    }

    private fun lockedCoinValue(
        state: AdapterState?,
        balance: BigDecimal,
        hideBalance: Boolean = false,
        coinDecimals: Int,
        token: Token
    ): DeemedValue<String> {
        val visible = !hideBalance && balance > BigDecimal.ZERO
        val deemed = state !is AdapterState.Synced

        val value = CryptoKit.numberFormatter.formatCoinFull(balance, token.coin.code, coinDecimals)

        return DeemedValue(value, deemed, visible)
    }

    fun viewItem(
        item: BalanceItem,
        currency: Currency,
    ): BalanceViewItem {
        val wallet = item.wallet
        val coin = wallet.coin
        val state = item.state
        val latestRate = item.coinPrice

        val coinValueStr = coinValue(
            state,
            item.balanceData.total,
            wallet.decimal
        )
        val currencyValueStr = currencyValue(
            state,
            item.balanceData.total,
            latestRate,
            currency
        )

        val primaryValue: DeemedValue<String> = coinValueStr
        val secondaryValue: DeemedValue<String> = currencyValueStr

        return BalanceViewItem(
            wallet = item.wallet,
            currencySymbol = currency.symbol,
            coinCode = coin.code,
            coinTitle = coin.name,
            coinIconUrl = coin.iconUrl,
            coinIconPlaceholder = wallet.token.iconPlaceholder,
            primaryValue = primaryValue,
            secondaryValue = secondaryValue,
            coinValueLocked = lockedCoinValue(
                state = state,
                balance = item.balanceData.locked,
                hideBalance = false,
                coinDecimals = wallet.decimal,
                token = wallet.token
            ),
            fiatValueLocked = currencyValue(
                state = state,
                balance = item.balanceData.locked,
                coinPrice = latestRate,
                currency = currency
            ),
            exchangeValue = rateValue(latestRate, false, currency),
            diff = item.coinPrice?.diff,
            expanded = true,
            sendEnabled = state is AdapterState.Synced,
            syncingProgress = getSyncingProgress(state, wallet.token.blockchainType),
            failedIconVisible = state is AdapterState.NotSynced,
            coinIconVisible = state !is AdapterState.NotSynced,
            badge = wallet.badge,
            swapVisible = wallet.token.swappable,
            swapEnabled = state is AdapterState.Synced,
            mainNet = item.mainNet,
            errorMessage = (state as? AdapterState.NotSynced)?.error?.message,
            isWatchAccount = false,
        )
    }
}
