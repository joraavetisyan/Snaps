package io.snaps.baseauth.data

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.model.AppError
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.coredata.database.TokenStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface AuthRepository {

    fun getCurrentUser(): FirebaseUser?

    suspend fun isEmailVerified(): Boolean

    suspend fun signInWithCredential(authCredential: AuthCredential): Effect<Completable>

    suspend fun signInWithEmail(email: String, password: String): Effect<Completable>

    suspend fun signUpWithEmail(email: String, password: String): Effect<Completable>

    suspend fun sendEmailVerification(): Effect<Completable>

    suspend fun resetPassword(email: String): Effect<Completable>
}

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val tokenStorage: TokenStorage,
) : AuthRepository {

    override fun getCurrentUser() = auth.currentUser

    override suspend fun isEmailVerified(): Boolean {
        return try {
            getCurrentUser()?.reload()?.await()
            getCurrentUser()?.isEmailVerified ?: false
        } catch (e: Exception) {
            log(e)
            false
        }
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
            auth.createUserWithEmailAndPassword(email, password).await()
            Effect.completable
        } catch (e: FirebaseAuthException) {
            Effect.error(AppError.Custom(displayMessage = e.localizedMessage, cause = e))
        } catch (e: Exception) {
            Effect.error(AppError.Unknown(cause = e))
        }
    }

    override suspend fun sendEmailVerification(): Effect<Completable> {
        return try {
            getCurrentUser()?.sendEmailVerification()?.await()
            Effect.completable
        } catch (e: Exception) {
            Effect.error(AppError.Unknown(cause = e))
        }
    }

    override suspend fun resetPassword(email: String): Effect<Completable> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Effect.completable
        } catch (e: FirebaseAuthException) {
            Effect.error(AppError.Custom(displayMessage = e.localizedMessage, cause = e))
        } catch (e: Exception) {
            Effect.error(AppError.Unknown(cause = e))
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): Effect<Completable> {
        return try {
            auth.signInWithEmailAndPassword(email, password)
                .await()
                .handleAuthResult() // todo call only if the email is confirmed
        } catch (e: FirebaseAuthException) {
            Effect.error(AppError.Custom(displayMessage = e.localizedMessage, cause = e))
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