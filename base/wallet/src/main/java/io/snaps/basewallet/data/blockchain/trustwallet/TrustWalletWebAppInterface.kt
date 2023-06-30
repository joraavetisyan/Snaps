package io.snaps.basewallet.data.blockchain.trustwallet

import android.webkit.JavascriptInterface
import android.webkit.WebView
import io.horizontalsystems.ethereumkit.core.Ext
import io.horizontalsystems.ethereumkit.core.stripHexPrefix
import io.snaps.basewallet.domain.SwapTransactionModel
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.ext.logE
import io.snaps.corecommon.model.CryptoAddress
import io.snaps.corecommon.model.TxHash
import org.json.JSONObject
import java.math.BigInteger

/**
 * This class is used to communicate with webview
 */
class TrustWalletWebAppInterface(
    private val address: CryptoAddress,
    private val webView: WebView,
    private val sendTransaction: (SwapTransactionModel, (TxHash) -> Unit) -> Unit,
) {

    companion object {

        const val name = "_tw_"
    }

    @JavascriptInterface
    fun postMessage(json: String) {
        val obj = JSONObject(json)
        when (val method = DAppMethod.fromValue(obj.getString("name"))) {
            DAppMethod.REQUESTACCOUNTS -> {
                handleRequestAccounts(obj)
            }
            DAppMethod.SIGNTRANSACTION -> {
                handleSignTransaction(obj)
            }
            DAppMethod.UNKNOWN -> {
                log("Unhandled method: $method")
            }
        }
    }

    private fun handleRequestAccounts(obj: JSONObject) {
        val id = obj.getLong("id")
        val network = obj.getString("network")
        val callback = "window.$network.sendResponse($id, [\"$address\"])"
        webView.post {
            webView.evaluateJavascript(callback) {
                log("EvaluateJavascript result: callback $it")
            }
        }
    }

    private fun handleSignTransaction(obj: JSONObject) {
        val param = obj.getJSONObject("object")
        val data = try {
            param.getString("data")
        } catch (e: Exception) {
            log(e, "Error extracting data!")
            null
        }?.let {
            Ext.hexStringAsByteArray(it)
        } ?: kotlin.run {
            logE("No data found!")
            return
        }
        val to = param.getString("to")
        val value = param.getString("value")
        val valueInt = BigInteger(value.stripHexPrefix(), 16)
        val gasPriceInt = "10000000000".toBigInteger()
        val gasLimit = param.getString("gas")
        val gasLimitInt = BigInteger(gasLimit.stripHexPrefix(), 16)
        sendTransaction(
            SwapTransactionModel(
                address = to,
                amount = valueInt,
                gasPrice = gasPriceInt,
                gasLimit = gasLimitInt,
                data = data,
            )
        ) { thHash ->
            val id = obj.getLong("id")
            val network = obj.getString("network")
            val callback = "window.$network.sendResponse($id, \"$thHash\")"
            webView.post {
                webView.evaluateJavascript(callback) {
                    log("EvaluateJavascript result: callback $it")
                }
            }
        }
    }
}