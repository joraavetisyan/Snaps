package io.snaps.featureregistration.presentation.data

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import io.snaps.corecommon.model.AppError
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.network.apiCall
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface AuthRepository {

    fun getCurrentUser(): FirebaseUser?

    fun isEmailVerified(): Boolean

    suspend fun signInWithGoogle(idToken: String): Effect<Completable>

    suspend fun signInWithEmail(email: String, password: String): Effect<Completable>

    suspend fun signUpWithEmail(email: String, password: String): Effect<Completable>

    fun sendEmailVerification()
}

class AuthRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val api: AuthApi,
    private val auth: FirebaseAuth,
) : AuthRepository {

    override fun getCurrentUser() = auth.currentUser

    override fun isEmailVerified(): Boolean {
        getCurrentUser()?.reload()
        return getCurrentUser()?.isEmailVerified ?: true
    }

    override suspend fun signInWithGoogle(idToken: String): Effect<Completable> {
        val googleCredential = GoogleAuthProvider.getCredential(idToken, null)
        return try {
            auth.signInWithCredential(googleCredential)
                .await()
                .handleAuthResult()
        } catch (e: Exception) {
            Effect.error(AppError.Unknown(cause = e))
        }
    }

    override suspend fun signUpWithEmail(email: String, password: String): Effect<Completable> {
        return try {
            auth.createUserWithEmailAndPassword(email, password)
                .await()
                .handleAuthResult()
        } catch (e: Exception) {
            Effect.error(AppError.Unknown(cause = e))
        }
    }

    override fun sendEmailVerification() {
        getCurrentUser()?.sendEmailVerification()
    }

    override suspend fun signInWithEmail(email: String, password: String): Effect<Completable> {
        return try {
            auth.signInWithEmailAndPassword(email, password)
                .await()
                .handleAuthResult()
        } catch (e: Exception) {
            Effect.error(AppError.Unknown(cause = e))
        }
    }

    private suspend fun AuthResult.handleAuthResult(): Effect<Completable> {
        val token = user?.getIdToken(false)?.result?.token
        return if (token != null) {
            auth(token)
        } else {
            Effect.error(AppError.Unknown())
        }
    }

    private suspend fun auth(token: String): Effect<Completable> {
        return apiCall(ioDispatcher) {
            api.auth(token)
        }
    }
}