package io.snaps.coredata.network

import io.snaps.corecommon.model.BuildInfo
import java.lang.IllegalStateException

enum class ApiService(
    val prod: String,
    val test: String = prod,
) {

    General(
//        "http://51.250.42.53:5100/api/v1/", // todo release
        "http://51.250.36.197:5100/api/v1/",
    );

    fun getBaseUrl(buildInfo: BuildInfo) = when {
        buildInfo.isDebug || buildInfo.isAlpha -> test
        buildInfo.isRelease -> prod
        else -> throw IllegalStateException("Unknown build type")
    }
}