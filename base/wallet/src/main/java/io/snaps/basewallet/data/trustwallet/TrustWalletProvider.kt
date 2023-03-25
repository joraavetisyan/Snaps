package io.snaps.basewallet.data.trustwallet

import android.app.Activity
import io.horizontalsystems.ethereumkit.models.Chain

private const val RpcUrl = "https://bsc-dataseed2.binance.org"
private const val SwapUrl = "https://pancakeswap.finance/swap?outputCurrency=BNB&inputCurrency=%s"

object TrustWalletProvider {

    fun loadProviderJs(activity: Activity): String {
        return activity.resources
            .openRawResource(trust.web3jprovider.R.raw.trust_min)
            .bufferedReader()
            .use { it.readText() }
    }

    fun loadInitJs(): String {
        return """
        (function() {
            var config = {                
                ethereum: {
                    chainId: ${Chain.BinanceSmartChain.id},
                    rpcUrl: "$RpcUrl"
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