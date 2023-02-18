package io.snaps.corecrypto.core.providers

import io.snaps.corecrypto.entities.Currency

class AppConfigProvider {

    val twitterBearerToken: String =
        "AAAAAAAAAAAAAAAAAAAAAJgeNwEAAAAA6xVpR6xLKTrxIA3kkSyRA92LDpA%3Da6auybDwcymUyh2BcS6zZwicUdxGtrzJC0qvOSdRwKLeqBGhwB"
    val cryptoCompareApiKey: String = ""
    val uniswapGraphUrl: String = "https://api.thegraph.com/subgraphs/name/uniswap/uniswap-v2"
    val infuraProjectId: String = "5bf760228fcd47bb8d277dba49b7b369"
    val infuraSecretKey: String = "7b81c992c98d4e60aaa8a0ef9acff2be"
    val etherscanKey: String = "TTH1114D5VD5ZMCJZ4B74SGIDRCGSKWGX9"
    val bscscanKey: String = "HBQQN4GTKCHYSRZCKFVQJ3FWGPY4T8237Y"
    val polygonscanKey: String = "2JM7USE5YRI59RWFZQI2RECAZSNI5QEQGV"
    val snowtraceApiKey: String = "47IXTRAAFT1E1J4RNSPZPNB5EWUIQR16FG"
    val optimisticEtherscanApiKey: String = "745EUI4781T147M9QJRNS5G3Q5NFF2SJXP"
    val arbiscanApiKey: String = "4QWW522BV13BJCZMXH1JIB2ESJ7MZTSJYI"
    val gnosisscanApiKey: String = "KEXFAQKDUENZ5U9CW3ZKYJEJ84ZIHH9QTY"
    val defiyieldProviderApiKey: String = "b66ea1f5-f645-4e15-905a-65941580107d"
    val is_release: String = "true"
    val guidesUrl: String =
        "https://raw.githubusercontent.com/horizontalsystems/blockchain-crypto-guides/v1.2/index.json"
    val faqUrl: String =
        "https://raw.githubusercontent.com/horizontalsystems/Unstoppable-Wallet-Website/v1.3/src/faq.json"
    val coinsJsonUrl: String =
        "https://raw.githubusercontent.com/horizontalsystems/cryptocurrencies/v0.21/coins.json"
    val providerCoinsJsonUrl: String =
        "https://raw.githubusercontent.com/horizontalsystems/cryptocurrencies/v0.21/provider.coins.json"
    val marketApiBaseUrl: String = "https://api.blocksdecoded.com"
    val marketApiKey: String = "IQf1uAjkthZp1i2pYzkXFDom"
    val walletConnectV2Key: String = "0c5ca155c2f165a7d0c88686f2113a72"

    val companyWebPageLink: String = "https://horizontalsystems.io"
    val appWebPageLink: String = "https://unstoppable.money"
    val appGithubLink: String = "https://github.com/horizontalsystems/unstoppable-wallet-android"
    val appTwitterLink: String = "https://twitter.com/UnstoppableByHS"
    val appTelegramLink: String = "https://t.me/unstoppable_announcements"
    val appRedditLink: String = "https://www.reddit.com/r/UNSTOPPABLEWallet/"
    val reportEmail = "support.unstoppable@protonmail.com"
    val btcCoreRpcUrl: String = "https://btc.blocksdecoded.com/rpc"
    val releaseNotesUrl: String =
        "https://api.github.com/repos/horizontalsystems/unstoppable-wallet-android/releases/tags/"
    val walletConnectUrl = "relay.walletconnect.com"
    val walletConnectProjectId by lazy {
        walletConnectV2Key
    }
    val infuraProjectSecret by lazy {
        infuraSecretKey
    }
    val etherscanApiKey by lazy {
        etherscanKey
    }
    val bscscanApiKey by lazy {
        bscscanKey
    }
    val polygonscanApiKey by lazy {
        polygonscanKey
    }
    val fiatDecimal: Int = 2
    val feeRateAdjustForCurrencies: List<String> = listOf("USD", "EUR")

    val currencies: List<Currency> = listOf(
        Currency("AUD", "A$", 2, 0),
        Currency("BRL", "R$", 2, 0),
        Currency("CAD", "C$", 2, 0),
        Currency("CHF", "₣", 2, 0),
        Currency("CNY", "¥", 2, 0),
        Currency("EUR", "€", 2, 0),
        Currency("GBP", "£", 2, 0),
        Currency("HKD", "HK$", 2, 0),
        Currency("ILS", "₪", 2, 0),
        Currency("INR", "₹", 2, 0),
        Currency("JPY", "¥", 2, 0),
        Currency("RUB", "₽", 2, 0),
        Currency("SGD", "S$", 2, 0),
        Currency("USD", "$", 2, 0),
        Currency("ZAR", "R", 2, 0),
    )

}
