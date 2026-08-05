package com.example.data

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DriveAuthRepository(private val context: Context) {

    private val _signedInAccount = MutableStateFlow<GoogleSignInAccount?>(GoogleSignIn.getLastSignedInAccount(context))
    val signedInAccount: StateFlow<GoogleSignInAccount?> = _signedInAccount.asStateFlow()

    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestScopes(Scope(DriveScopes.DRIVE))
        .build()

    private val googleSignInClient = GoogleSignIn.getClient(context, gso)

    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    fun handleSignInResult(intent: Intent?) {
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
            _signedInAccount.value = task.result
        } catch (e: Exception) {
            _signedInAccount.value = null
        }
    }

    fun signOut(onComplete: () -> Unit = {}) {
        googleSignInClient.signOut().addOnCompleteListener {
            _signedInAccount.value = null
            onComplete()
        }
    }

    fun getDriveService(): Drive? {
        val account = _signedInAccount.value ?: return null
        val credential = GoogleAccountCredential.usingOAuth2(
            context, listOf(DriveScopes.DRIVE)
        ).setSelectedAccountName(account.email)

        return Drive.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        )
        .setApplicationName("DriveSync")
        .build()
    }
}
