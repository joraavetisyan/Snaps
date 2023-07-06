package io.snaps.coredata.network

import android.telephony.TelephonyManager
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.ext.logE
import io.snaps.corecommon.model.AppError
import io.snaps.corecommon.model.BuildInfo
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiService @Inject constructor(
    private val buildInfo: BuildInfo,
    private val telephonyManager: TelephonyManager
) {

    private val test = "http://51.250.36.197:5100/api/"
    private val productionUrl = "http://51.250.42.53:5100/api/"
    private val googleProductionUrl = "http://34.118.21.102/api/"
    private var prod: String = ""

    init {
        if (buildInfo.isRelease)
            loadProdUrl()
    }

    fun getBaseUrl() = when {
        buildInfo.isDebug || buildInfo.isAlpha -> test
        buildInfo.isRelease -> prod
        else -> throw IllegalStateException("Unknown build type")
    }

    private fun checkForCountry(): String {
        log(telephonyManager.networkCountryIso)
        return telephonyManager.networkCountryIso
    }

    fun loadProdUrl(): Effect<Completable> {
        return try {
            if (checkForCountry() == "ru") {
                log(productionUrl)
                prod = productionUrl
                Effect.success(Completable)
            } else {
                log(googleProductionUrl)
                prod = googleProductionUrl
                Effect.success(Completable)
            }
        } catch (e: Exception) {
            logE("telephony manager  error: $e")
            Effect.error(AppError.Unknown(cause = e))
        }
    }
}

/*
enum class ApiService(
    val prod: String,
    val test: String,
) {

    General(
        "http://51.250.42.53:5100/api/", // todo release https once back supports
        "http://51.250.36.197:5100/api/",
    );

    fun getBaseUrl(buildInfo: BuildInfo) = when {
        buildInfo.isDebug || buildInfo.isAlpha -> test
        buildInfo.isRelease -> prod
        else -> throw IllegalStateException("Unknown build type")
    }
}*/