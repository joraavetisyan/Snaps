package io.snaps.corecrypto.core.managers

import io.snaps.corecrypto.core.IWalletManager
import io.snaps.corecrypto.core.defaultSettingsArray
import io.snaps.corecrypto.entities.Account
import io.snaps.corecrypto.entities.ConfiguredToken
import io.snaps.corecrypto.entities.Wallet
import io.horizontalsystems.marketkit.models.TokenQuery

class WalletActivator(
    private val walletManager: IWalletManager,
    private val marketKit: MarketKitWrapper,
) {

    fun activateWallets(account: Account, tokenQueries: List<TokenQuery>) {
        val wallets = mutableListOf<Wallet>()

        for (tokenQuery in tokenQueries) {
            val token = marketKit.token(tokenQuery) ?: continue

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
