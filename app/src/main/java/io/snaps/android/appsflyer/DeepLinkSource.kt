package io.snaps.android.appsflyer

import com.appsflyer.deeplink.DeepLink
import com.appsflyer.deeplink.DeepLinkListener
import com.appsflyer.deeplink.DeepLinkResult
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.ext.logE
import io.snaps.corenavigation.AppDeeplink
import io.snaps.corenavigation.Deeplink
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

interface DeepLinkSource {

    val state: StateFlow<Deeplink?>

    fun getDeepLinkListener(): DeepLinkListener
}

class DeepLinkSourceImpl @Inject constructor() : DeepLinkSource {

    private val _state = MutableStateFlow<Deeplink?>(null)
    override val state = _state.asStateFlow()

    override fun getDeepLinkListener(): DeepLinkListener {
        return object : DeepLinkListener {
            override fun onDeepLinking(deepLinkResult: DeepLinkResult) {
                when (deepLinkResult.status) {
                    DeepLinkResult.Status.FOUND -> {
                        log("Deep link found")
                    }
                    DeepLinkResult.Status.NOT_FOUND -> {
                        log("Deep link not found")
                        return
                    }
                    else -> {
                        val dlError = deepLinkResult.error
                        logE("There was an error getting Deep Link data: $dlError")
                        return
                    }
                }
                try {
                    val deepLinkObj = deepLinkResult.deepLink
                    log("The DeepLink data is: $deepLinkObj")
                    parse(deepLinkObj)?.let {
                        _state.tryEmit(it)
                    }
                } catch (e: Exception) {
                    logE("DeepLink data came back null")
                }
            }
        }
    }

    private fun parse(deepLink: DeepLink): Deeplink? {
        return when (deepLink.mediaSource) {
            "User_invite" -> AppDeeplink.Invite(code = deepLink.getStringValue("code").orEmpty())
            else -> null
        }
    }
}