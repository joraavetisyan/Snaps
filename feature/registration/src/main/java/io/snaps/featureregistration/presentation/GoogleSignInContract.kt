package io.snaps.featureregistration.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

private const val SERVER_CLIENT_ID = "132799039711-rd59jfaphbpinbmhrp647hqapp2b6aiu.apps.googleusercontent.com"

class GoogleSignInContract : ActivityResultContract<Any?, String?>() {

    override fun createIntent(context: Context, input: Any?): Intent {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(SERVER_CLIENT_ID)
                .requestEmail()
                .build()
        return GoogleSignIn
            .getClient(context, googleSignInOptions)
            .signInIntent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String? = when {
        resultCode != Activity.RESULT_OK -> null
        else -> GoogleSignIn.getSignedInAccountFromIntent(intent).result.idToken
    }
}