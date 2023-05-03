package io.snaps.corecrypto.core.managers

import io.horizontalsystems.marketkit.models.Blockchain
import io.horizontalsystems.marketkit.models.BlockchainType
import io.horizontalsystems.marketkit.models.Coin
import io.horizontalsystems.marketkit.models.Token
import io.horizontalsystems.marketkit.models.TokenQuery
import io.horizontalsystems.marketkit.models.TokenType
import io.snaps.corecrypto.core.IWalletManager
import io.snaps.corecrypto.core.defaultSettingsArray
import io.snaps.corecrypto.entities.Account
import io.snaps.corecrypto.entities.ConfiguredToken
import io.snaps.corecrypto.entities.Wallet

val SNAPS_NFT = "0x5F0cF62ad1DD5A267427DC161ff365b75142E3b3"
val SNAPS = "0x92677918569A2BEA213Af66b54e0C9B9811d021c"
private const val WBNB = "0xbb4CdB9CBd36B01bD1cBaEBF2De08d9173bc095c"
private const val BUSD = "0xe9e7cea3dedca5984780bafc599bd69add087d56"
private const val USDT = "0x55d398326f99059ff775485246999027b3197955"

val defaultTokens = listOf(
    SNAPS,
    WBNB,
    BUSD,
    USDT,
)

class WalletActivator(
    private val walletManager: IWalletManager,
    private val marketKit: MarketKitWrapper,
) {

    fun activateWallets(account: Account, tokenQueries: List<TokenQuery>) {
        val wallets = mutableListOf<Wallet>()

        for (tokenQuery in tokenQueries) {
            val token: Token = if ((tokenQuery.tokenType as? TokenType.Eip20)?.address == SNAPS) {
                Token(
                    coin = Coin(
                        uid = "snapsCoinUid",
                        name = "Snaps",
                        code = "SNAPS",
                    ),
                    blockchain = Blockchain(
                        type = BlockchainType.BinanceSmartChain,
                        name = "Testnet",
                        explorerUrl = "https://testnet.bscscan.com",
                    ),
                    type = TokenType.Eip20(
                        SNAPS
                    ),
                    decimals = 18,
                )
            } else {
                marketKit.token(tokenQuery) ?: continue
            }

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