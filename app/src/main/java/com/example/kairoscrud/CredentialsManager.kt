// Ubicación: com/example/kairoscrud/CredentialsManager.kt

package com.example.kairoscrud

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.auth0.android.result.Credentials

class CredentialsManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "auth0_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveCredentials(credentials: Credentials) {
        sharedPreferences.edit()
            .putString("access_token", credentials.accessToken)
            .putString("id_token", credentials.idToken)
            .apply()
    }

    fun getAccessToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    fun clearCredentials() {
        sharedPreferences.edit().clear().apply()
    }
}