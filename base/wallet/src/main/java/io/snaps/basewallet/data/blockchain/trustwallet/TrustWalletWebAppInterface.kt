package io.snaps.basewallet.data.blockchain.trustwallet

import android.webkit.JavascriptInterface
import android.webkit.WebView
import io.horizontalsystems.ethereumkit.core.hexStringToByteArrayOrNull
import io.horizontalsystems.ethereumkit.core.stripHexPrefix
import io.snaps.basewallet.domain.SwapTransactionModel
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.model.WalletAddress
import org.json.JSONObject
import java.math.BigInteger

/**
 * This class is used to communicate with the webview
 */
class TrustWalletWebAppInterface(
    private val address: WalletAddress,
    private val webView: WebView,
    private val sendTransaction: (SwapTransactionModel) -> Unit,
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

    private fun handleSignTransaction(obj: JSONObject) {
        val param = obj.getJSONObject("object")
        val data = try {
            param.getString("data")
        } catch (e: Exception) {
            log("Error extracting data: ${e.message}")
            null
        }?.hexStringToByteArrayOrNull() ?: kotlin.run {
            log("No data found!")
            return
        }
        val to = param.getString("to")
        val value = param.getString("value")
        val valueInt = BigInteger(value.stripHexPrefix(), 16)
        val gasPrice = param.getString("gasPrice")
        val gasPriceInt = BigInteger(gasPrice.stripHexPrefix(), 16)
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
        )
    }
}