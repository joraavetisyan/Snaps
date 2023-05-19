package io.snaps.basewallet.data.blockchain.trustwallet

import android.app.Activity
import io.horizontalsystems.ethereumkit.models.Chain
import io.snaps.basewallet.R
import io.snaps.corecommon.model.CryptoAddress
import io.snaps.corecrypto.core.CryptoKit

private const val SwapUrl = "https://pancakeswap.finance/swap?outputCurrency=BNB&inputCurrency=%s"

object TrustWalletProvider {

    fun loadProviderJs(activity: Activity): String {
        return activity.resources
            .openRawResource(R.raw.trust_min)
            .bufferedReader()
            .use { it.readText() }
    }

    fun loadInitJs(address: CryptoAddress): String {
        return """
        (function() {
            var config = {                
                ethereum: {
                    address: "$address",
                    chainId: ${Chain.BinanceSmartChain.id},
                    rpcUrl: "${
            if (CryptoKit.testMode) "https://data-seed-prebsc-1-s1.binance.org:8545/"
            else "https://bsc-dataseed2.binance.org"
        }"
                },
                isDebug: true
            };
            trustwallet.ethereum = new trustwallet.Provider(config);
            trustwallet.postMessage = (json) => {
                window._tw_.postMessage(JSON.stringify(json));
            }
            window.ethereum = trustwallet.ethereum;
        })();
        """
    }

    fun swapProvideUrl(symbol: String) = SwapUrl.format(symbol)
}