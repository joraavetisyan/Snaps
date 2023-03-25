package io.snaps.basewallet.data.trustwallet

import android.webkit.JavascriptInterface
import android.webkit.WebView
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.model.WalletAddress
import org.json.JSONObject

class WebAppInterface(
    private val address: WalletAddress,
    private val webView: WebView,
    private val sendTransaction: (
        address: String,
        amount: String,
    ) -> Unit,
) {

    companion object {

        const val name = "_tw_"
    }

    @JavascriptInterface
    fun postMessage(json: String) {
        val obj = JSONObject(json)
        val id = obj.getLong("id")
        val method = DAppMethod.fromValue(obj.getString("name"))
        val network = obj.getString("network")
        when (method) {
            DAppMethod.REQUESTACCOUNTS -> {
                val setAddress = "window.$network.setAddress(\"$address\");"
                val callback = "window.$network.sendResponse($id, [\"$address\"])"
                webView.post {
                    webView.evaluateJavascript(setAddress) {
                        log("EvaluateJavascript result: setAddress $it")
                    }
                    webView.evaluateJavascript(callback) {
                        log("EvaluateJavascript result: callback $it")
                    }
                }
            }
            DAppMethod.SIGNTRANSACTION -> {
                val param = obj.getJSONObject("object")
                val to = param.getString("to")
                val value = param.getString("value")
                val gasPrice = param.getString("gasPrice")
                val gas = param.getString("gas")
                sendTransaction(to, value)
            }
            else -> {
                log("Unhandled method: $method")
            }
        }
    }
}
