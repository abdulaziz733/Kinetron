package com.abdulaziz733.kinetron.data.network

import android.accounts.Account
import android.content.Context
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * OAuth helper for Google Sign-In using Android Client only.
 * Token refresh is handled automatically by Google Play Services via GoogleAuthUtil.
 * No client_secret or server-side token exchange required.
 */
object OAuthHelper {

    private const val GMAIL_SCOPE = "oauth2:https://www.googleapis.com/auth/gmail.readonly"

    /** Build sign-in options requesting Gmail read scope. */
    fun buildGoogleSignInOptions(): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope("https://www.googleapis.com/auth/gmail.readonly"))
            .build()
    }

    /**
     * Retrieves a valid Gmail access token for the given account.
     * Play Services automatically refreshes the token if expired.
     * Returns null if the account has revoked access or is not signed in.
     */
    suspend fun getGmailAccessToken(context: Context, accountEmail: String): String? =
        withContext(Dispatchers.IO) {
            try {
                val account = Account(accountEmail, "com.google")
                GoogleAuthUtil.getToken(context, account, GMAIL_SCOPE)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
}
