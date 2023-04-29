package io.snaps.corecrypto.core

import io.horizontalsystems.hdwalletkit.ExtendedKeyCoinType
import io.horizontalsystems.hdwalletkit.HDWallet
import io.horizontalsystems.marketkit.models.Blockchain
import io.horizontalsystems.marketkit.models.BlockchainType
import io.horizontalsystems.marketkit.models.Coin
import io.horizontalsystems.marketkit.models.FullCoin
import io.horizontalsystems.marketkit.models.Token
import io.horizontalsystems.marketkit.models.TokenQuery
import io.horizontalsystems.marketkit.models.TokenType
import io.horizontalsystems.marketkit.models.TopPlatform
import io.snaps.corecrypto.core.managers.RestoreSettingType
import io.snaps.corecrypto.entities.AccountType
import io.snaps.corecrypto.entities.BitcoinCashCoinType
import io.snaps.corecrypto.entities.CoinSettingType
import io.snaps.corecrypto.entities.CoinSettings
import io.snaps.corecrypto.entities.FeePriceScale
import io.snaps.corecrypto.entities.derivation

val Token.protocolType: String?
    get() = tokenQuery.protocolType

val Token.isCustom: Boolean
    get() = coin.uid == tokenQuery.customCoinUid

val Token.isSupported: Boolean
    get() = tokenQuery.isSupported

val Token.iconPlaceholder: Int
    get() = when (type) {
        is TokenType.Eip20 -> blockchainType.tokenIconPlaceholder
        is TokenType.Bep2 -> 0
        else -> 0
    }

val Token.swappable: Boolean
    get() = when (blockchainType) {
        BlockchainType.Ethereum,
        BlockchainType.BinanceSmartChain,
        BlockchainType.Polygon,
        BlockchainType.Avalanche,
        BlockchainType.Optimism,
        BlockchainType.Gnosis,
        BlockchainType.ArbitrumOne -> true
        else -> false
    }

val Token.protocolInfo: String
    get() = when (type) {
        TokenType.Native -> blockchain.name
        is TokenType.Eip20,
        is TokenType.Bep2,
        is TokenType.Spl -> protocolType ?: ""
        else -> ""
    }

val Token.typeInfo: String
    get() = when (val type = type) {
        TokenType.Native -> {
            val parts = mutableListOf("")
            when (this.blockchainType) {
                BlockchainType.BinanceSmartChain -> parts.add("(BEP20)")
                BlockchainType.BinanceChain -> parts.add("(BEP2)")
                else -> {}
            }
            parts.joinToString(" ")
        }
        is TokenType.Eip20 -> type.address.shorten()
        is TokenType.Bep2 -> type.symbol
        is TokenType.Spl -> type.address.shorten()
        is TokenType.Unsupported -> ""
    }

val Token.copyableTypeInfo: String?
    get() = when (val type = type) {
        is TokenType.Eip20 -> type.address
        is TokenType.Bep2 -> type.symbol
        is TokenType.Spl -> type.address
        else -> null
    }


val TokenQuery.protocolType: String?
    get() = when (tokenType) {
        is TokenType.Native -> {
            when (blockchainType) {
                BlockchainType.Optimism -> "Optimism"
                BlockchainType.ArbitrumOne -> "Arbitrum"
                BlockchainType.BinanceChain -> "BEP2"
                BlockchainType.Gnosis -> "Gnosis"
                else -> null
            }
        }
        is TokenType.Eip20 -> {
            when (blockchainType) {
                BlockchainType.Ethereum -> "ERC20"
                BlockchainType.EthereumGoerli -> "Goerli ERC20"
                BlockchainType.BinanceSmartChain -> "BEP20"
                BlockchainType.Polygon -> "Polygon"
                BlockchainType.Avalanche -> "Avalanche"
                BlockchainType.Optimism -> "Optimism"
                BlockchainType.ArbitrumOne -> "Arbitrum"
                BlockchainType.Gnosis -> "Gnosis"
                else -> null
            }
        }
        is TokenType.Bep2 -> "BEP2"
        is TokenType.Spl -> "Solana"
        else -> null
    }

val TokenQuery.Companion.customCoinPrefix: String
    get() = "custom-"

val TokenQuery.customCoinUid: String
    get() = "${TokenQuery.customCoinPrefix}${id}"

val TokenQuery.isSupported: Boolean
    get() = when (blockchainType) {
        BlockchainType.Bitcoin,
        BlockchainType.BitcoinCash,
        BlockchainType.Litecoin,
        BlockchainType.Dash,
        BlockchainType.Zcash -> {
            tokenType is TokenType.Native
        }
        BlockchainType.Ethereum,
        BlockchainType.EthereumGoerli,
        BlockchainType.BinanceSmartChain,
        BlockchainType.Polygon,
        BlockchainType.Optimism,
        BlockchainType.ArbitrumOne,
        BlockchainType.Gnosis,
        BlockchainType.Avalanche -> {
            tokenType is TokenType.Native || tokenType is TokenType.Eip20
        }
        BlockchainType.BinanceChain -> {
            tokenType is TokenType.Native || tokenType is TokenType.Bep2
        }
        BlockchainType.Solana -> {
            tokenType is TokenType.Native || tokenType is TokenType.Spl
        }
        else -> false
    }

val Blockchain.description: String
    get() = when (type) {
        BlockchainType.Bitcoin -> "BTC (BIP44, BIP49, BIP84)"
        BlockchainType.BitcoinCash -> "BCH (Legacy, CashAddress)"
        BlockchainType.Zcash -> "ZEC"
        BlockchainType.Litecoin -> "LTC (BIP44, BIP49, BIP84)"
        BlockchainType.Dash -> "DASH"
        BlockchainType.BinanceChain -> "BNB, BEP2 tokens"
        BlockchainType.Ethereum -> "ETH, ERC20 tokens"
        BlockchainType.EthereumGoerli -> "ETH, ERC20 tokens"
        BlockchainType.BinanceSmartChain -> "BNB, BEP20 tokens"
        BlockchainType.Polygon -> "MATIC, ERC20 tokens"
        BlockchainType.Avalanche -> "AVAX, ERC20 tokens"
        BlockchainType.Optimism -> "L2 chain"
        BlockchainType.ArbitrumOne -> "L2 chain"
        BlockchainType.Solana -> "SOL, SPL tokens"
        BlockchainType.Gnosis -> "xDAI, ERC20 tokens"
        else -> ""
    }


val BlockchainType.imageUrl: String
    get() = "https://cdn.blocksdecoded.com/blockchain-icons/32px/$uid@3x.png"

val BlockchainType.coinSettingType: CoinSettingType?
    get() = when (this) {
        BlockchainType.Bitcoin,
        BlockchainType.Litecoin -> CoinSettingType.derivation
        BlockchainType.BitcoinCash -> CoinSettingType.bitcoinCashCoinType
        else -> null
    }

fun BlockchainType.defaultSettingsArray(accountType: AccountType): List<CoinSettings> =
    when (this) {
        BlockchainType.Bitcoin,
        BlockchainType.Litecoin -> {
            when (accountType) {
                is AccountType.Mnemonic -> listOf(CoinSettings(mapOf(CoinSettingType.derivation to AccountType.Derivation.bip84.value)))
                is AccountType.HdExtendedKey -> listOf(CoinSettings(mapOf(CoinSettingType.derivation to accountType.hdExtendedKey.info.purpose.derivation.value)))
                else -> listOf()
            }
        }
        BlockchainType.BitcoinCash -> listOf(CoinSettings(mapOf(CoinSettingType.bitcoinCashCoinType to BitcoinCashCoinType.type145.value)))
        else -> listOf()
    }

val BlockchainType.restoreSettingTypes: List<RestoreSettingType>
    get() = when (this) {
        BlockchainType.Zcash -> listOf(RestoreSettingType.BirthdayHeight)
        else -> listOf()
    }

val BlockchainType.order: Int
    get() = when (this) {
        BlockchainType.Bitcoin -> 1
        BlockchainType.Ethereum -> 2
        BlockchainType.BinanceSmartChain -> 3
        BlockchainType.Polygon -> 4
        BlockchainType.Avalanche -> 5
        BlockchainType.Zcash -> 6
        BlockchainType.BitcoinCash -> 7
        BlockchainType.Litecoin -> 8
        BlockchainType.Dash -> 9
        BlockchainType.BinanceChain -> 10
        BlockchainType.Gnosis -> 11
        BlockchainType.ArbitrumOne -> 12
        BlockchainType.Optimism -> 13
        BlockchainType.Solana -> 14
        BlockchainType.EthereumGoerli -> 15
        else -> Int.MAX_VALUE
    }

val BlockchainType.tokenIconPlaceholder: Int
    get() = when (this) {
        BlockchainType.Ethereum -> 0
        BlockchainType.EthereumGoerli -> 0
        BlockchainType.BinanceSmartChain -> 0
        BlockchainType.BinanceChain -> 0
        BlockchainType.Avalanche -> 0
        BlockchainType.Polygon -> 0
        BlockchainType.Optimism -> 0
        BlockchainType.ArbitrumOne -> 0
        BlockchainType.Gnosis -> 0
        else -> 0
    }

val BlockchainType.feePriceScale: FeePriceScale
    get() = when (this) {
        BlockchainType.Avalanche -> FeePriceScale.Navax
        else -> FeePriceScale.Gwei
    }

fun BlockchainType.supports(accountType: AccountType): Boolean {
    return when (accountType) {
        is AccountType.Mnemonic -> true
        is AccountType.HdExtendedKey -> {
            val info = accountType.hdExtendedKey.info
            when (this) {
                BlockchainType.Bitcoin -> info.coinType == ExtendedKeyCoinType.Bitcoin
                BlockchainType.Litecoin -> info.coinType == ExtendedKeyCoinType.Litecoin && (info.purpose == HDWallet.Purpose.BIP44 || info.purpose == HDWallet.Purpose.BIP49)
                        || info.coinType == ExtendedKeyCoinType.Bitcoin && (info.purpose == HDWallet.Purpose.BIP44 || info.purpose == HDWallet.Purpose.BIP49 || info.purpose == HDWallet.Purpose.BIP84)
                BlockchainType.BitcoinCash -> info.coinType == ExtendedKeyCoinType.Bitcoin && info.purpose == HDWallet.Purpose.BIP44
                BlockchainType.Dash -> info.coinType == ExtendedKeyCoinType.Bitcoin && info.purpose == HDWallet.Purpose.BIP44
                else -> false
            }
        }
        is AccountType.EvmAddress ->
            this == BlockchainType.Ethereum
                    || this == BlockchainType.EthereumGoerli
                    || this == BlockchainType.BinanceSmartChain
                    || this == BlockchainType.Polygon
                    || this == BlockchainType.Avalanche
                    || this == BlockchainType.Optimism
                    || this == BlockchainType.ArbitrumOne
                    || this == BlockchainType.Gnosis
        is AccountType.EvmPrivateKey -> {
            this == BlockchainType.Ethereum
                    || this == BlockchainType.BinanceSmartChain
                    || this == BlockchainType.Polygon
                    || this == BlockchainType.Avalanche
                    || this == BlockchainType.Optimism
                    || this == BlockchainType.ArbitrumOne
                    || this == BlockchainType.Gnosis
        }
        is AccountType.SolanaAddress ->
            this == BlockchainType.Solana
    }
}

val TokenType.order: Int
    get() = when (this) {
        TokenType.Native -> 0
        else -> Int.MAX_VALUE
    }


val Coin.imageUrl: String
    get() = "https://cdn.blocksdecoded.com/coin-icons/32px/$uid@3x.png"

val TopPlatform.imageUrl
    get() = "https://cdn.blocksdecoded.com/blockchain-icons/32px/${blockchain.uid}@3x.png"

val FullCoin.typeLabel: String?
    get() = tokens.singleOrNull()?.protocolType

val FullCoin.supportedTokens
    get() = tokens
        .filter { it.isSupported }
        .sortedWith(compareBy({ it.type.order }, { it.blockchain.type.order }))

val FullCoin.iconPlaceholder: Int
    get() = if (tokens.size == 1) {
        tokens.first().iconPlaceholder
    } else {
        0
    }

fun FullCoin.eligibleTokens(accountType: AccountType): List<Token> {
    return supportedTokens.filter { it.blockchainType.supports(accountType) }
}