package io.snaps.basesession.data

import android.app.Application
import com.google.android.recaptcha.Recaptcha
import com.google.android.recaptcha.RecaptchaAction
import com.google.android.recaptcha.RecaptchaClient
import io.snaps.basesources.featuretoggle.Feature
import io.snaps.basesources.featuretoggle.FeatureToggle
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.model.AppError
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.coredata.coroutine.ApplicationCoroutineScope
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.database.UserDataStorage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

// todo rename
interface AntiFraudHandler {

    suspend fun setCaptcha(): Effect<Completable>
}

class AntiFraudHandlerSafetyNetImpl @Inject constructor(
    @ApplicationCoroutineScope private val scope: CoroutineScope,
    private val application: Application,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val featureToggle: FeatureToggle,
    private val userDataStorage: UserDataStorage,
) : AntiFraudHandler {

    private var isVerifyInProcess = AtomicBoolean(false)
    private var recaptchaClient: RecaptchaClient? = null

    override suspend fun setCaptcha(): Effect<Completable> {
        if (!featureToggle.isEnabled(Feature.Captcha)) { // todo handle case if feature changed while app launched
            return Effect.completable
        }
        return suspendCoroutine { continuation ->
            scope.launch(ioDispatcher) {
                if (isVerifyInProcess.get()) {
                    while (continuation.context.isActive && isVerifyInProcess.get()) {
                    }
                    continuation.resume(Effect.completable)
                } else {
                    isVerifyInProcess.set(true)
                    getClient()?.let { client ->
                        val result = client.execute(RecaptchaAction.LOGIN)
                        if (result.isSuccess) {
                            log("Captcha resolved!")
                            userDataStorage.captchaResult = result.getOrThrow()
                            isVerifyInProcess.set(false)
                            continuation.resume(Effect.completable)
                        } else {
                            val error = result.exceptionOrNull() ?: Exception("Captcha error!")
                            log(error)
                            isVerifyInProcess.set(false)
                            continuation.resume(Effect.error(AppError.Unknown(cause = error as? Exception)))
                        }
                    } ?: kotlin.run {
                        isVerifyInProcess.set(false)
                        continuation.resume(Effect.error(AppError.Unknown(cause = IllegalStateException("RecaptchaClient is null!"))))
                    }
                }
            }
        }
    }

    private suspend fun getClient() = recaptchaClient ?: Recaptcha.getClient(
        application = application, siteKey = "6Ld5QDgmAAAAAC5wrvEUzPRRE1FWuDc5BUzjH_dB"
    ).getOrNull()?.also {
        log("RecaptchaClient $it")
        recaptchaClient = it
    }
}