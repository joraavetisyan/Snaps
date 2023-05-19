package io.snaps.corecrypto.core.managers

import io.horizontalsystems.marketkit.models.Token
import io.horizontalsystems.marketkit.models.TokenQuery
import io.snaps.corecrypto.core.IWalletManager
import io.snaps.corecrypto.core.defaultSettingsArray
import io.snaps.corecrypto.entities.Account
import io.snaps.corecrypto.entities.ConfiguredToken
import io.snaps.corecrypto.entities.Wallet

class WalletActivator(
    private val walletManager: IWalletManager,
    private val marketKit: MarketKitWrapper,
) {

    fun activateWallets(account: Account, tokenQueries: List<TokenQuery>, tokens: List<Token> = emptyList()) {
        val wallets = mutableListOf<Wallet>()

        fun addToWallets(token: Token) {
            val defaultSettingsArray = token.blockchainType.defaultSettingsArray(account.type)

            if (defaultSettingsArray.isEmpty()) {
                wallets.add(Wallet(token, account))
            } else {
                defaultSettingsArray.forEach { coinSettings ->
                    val configuredToken = ConfiguredToken(token, coinSettings)
                    wallets.add(Wallet(configuredToken, account))
                }
            }
        }

        tokens.forEach(::addToWallets)
        tokenQueries.forEach { marketKit.token(it)?.let(::addToWallets) }
        walletManager.save(wallets)
    }

    fun activateConfiguredTokens(account: Account, configuredTokens: List<ConfiguredToken>) {
        val wallets = mutableListOf<Wallet>()

        for (configuredToken in configuredTokens) {
            wallets.add(Wallet(configuredToken, account))
        }

        walletManager.save(wallets)
    }
}