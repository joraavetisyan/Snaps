package io.snaps.coredata.network

import com.google.firebase.database.FirebaseDatabase
import io.snaps.corecommon.ext.logE
import io.snaps.corecommon.model.AppError
import io.snaps.corecommon.model.BuildInfo
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.coredata.coroutine.ApplicationCoroutineScope
import io.snaps.coredata.database.UserDataStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.IllegalStateException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiService @Inject constructor(
    @ApplicationCoroutineScope private val scope: CoroutineScope,
    private val buildInfo: BuildInfo,
    private val userDataStorage: UserDataStorage,
) {

    private val firebaseDatabase by lazy { FirebaseDatabase.getInstance() }

    private val test = "http://51.250.36.197:5100/api/"
    private var prod: String = userDataStorage.prodBaseUrl.orEmpty()

    init {
        scope.launch {
            loadProdUrl()
        }
    }

    fun getBaseUrl() = when {
        buildInfo.isDebug || buildInfo.isAlpha -> test
        buildInfo.isRelease -> prod
        else -> throw IllegalStateException("Unknown build type")
    }

    suspend fun loadProdUrl(): Effect<Completable> {
        return try {
            val endPoint = firebaseDatabase.getReference("Endpoint").get().await().value
            prod = "$endPoint/api/"
            userDataStorage.prodBaseUrl = prod
            Effect.success(Completable)
        } catch (e: Exception) {
            logE("firebase database load cancelled: $e")
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