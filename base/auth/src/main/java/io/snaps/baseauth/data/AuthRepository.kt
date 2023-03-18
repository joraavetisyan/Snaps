package io.snaps.baseauth.data

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.snaps.corecommon.model.AppError
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.coredata.database.TokenStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface AuthRepository {

    fun getCurrentUser(): FirebaseUser?

    fun isEmailVerified(): Boolean

    suspend fun signInWithCredential(authCredential: AuthCredential): Effect<Completable>

    suspend fun signInWithEmail(email: String, password: String): Effect<Completable>

    suspend fun signUpWithEmail(email: String, password: String): Effect<Completable>

    fun sendEmailVerification()
}

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val tokenStorage: TokenStorage,
) : AuthRepository {

    override fun getCurrentUser() = auth.currentUser

    override fun isEmailVerified(): Boolean {
        getCurrentUser()?.reload()
        return getCurrentUser()?.isEmailVerified ?: true
    }

    override suspend fun signInWithCredential(authCredential: AuthCredential): Effect<Completable> {
        return try {
            auth.signInWithCredential(authCredential)
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

    private fun AuthResult.handleAuthResult(): Effect<Completable> {
        val token = user?.getIdToken(false)?.result?.token
        return if (token != null) {
            tokenStorage.authToken = token
            Effect.completable
        } else {
            Effect.error(AppError.Unknown())
        }
    }
}