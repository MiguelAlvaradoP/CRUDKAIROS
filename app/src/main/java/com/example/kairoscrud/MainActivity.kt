// Ubicación: com/example/kairoscrud/MainActivity.kt

package com.example.kairoscrud

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.example.kairoscrud.ui.theme.KAIROSCRUDTheme

class MainActivity : ComponentActivity() {

    private lateinit var account: Auth0
    private lateinit var credentialsManager: CredentialsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar Auth0 y CredentialsManager
        account = Auth0(
            getString(R.string.com_auth0_client_id),
            getString(R.string.com_auth0_domain)
        )
        credentialsManager = CredentialsManager(this)

        // 🔽--- CAMBIO 1: COMPROBAR SI YA ESTÁ LOGUEADO ---🔽
        // Si ya tenemos un token, no mostramos el login, vamos directo a la app
        if (credentialsManager.getAccessToken() != null) {
            navigateToProductScreen()
            return // Importante: salimos de onCreate para no llamar a setContent
        }
        // 🔼--- FIN DEL CAMBIO 1 ---🔼

        // Si no hay token, mostramos la pantalla de login
        setContent {
            KAIROSCRUDTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Esta pantalla ahora solo tiene un propósito: Iniciar Sesión
                    LoginScreen(
                        onLoginClicked = { loginWithBrowser() }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (WebAuthProvider.resume(intent)) {
            return
        }
    }

    private fun loginWithBrowser() {
        WebAuthProvider.login(account)
            .withScheme(getString(R.string.com_auth0_scheme))
            .withScope("openid profile email")
            .start(this, object : Callback<Credentials, AuthenticationException> {
                override fun onFailure(error: AuthenticationException) {
                    Toast.makeText(this@MainActivity, "Error de login: ${error.message}", Toast.LENGTH_SHORT).show()
                }

                override fun onSuccess(result: Credentials) {
                    // Guardar credenciales
                    credentialsManager.saveCredentials(result)
                    Toast.makeText(this@MainActivity, "Login Exitoso", Toast.LENGTH_SHORT).show()

                    // 🔽--- CAMBIO 2: NAVEGAR A PRODUCTOS ---🔽
                    // En lugar de cambiar un estado, navegamos a la nueva actividad
                    navigateToProductScreen()
                    // 🔼--- FIN DEL CAMBIO 2 ---🔼
                }
            })
    }

    // 🔽--- CAMBIO 3: FUNCIÓN DE NAVEGACIÓN ---🔽
    private fun navigateToProductScreen() {
        val intent = Intent(this, ProductActivity::class.java)
        // Limpiamos el stack para que el usuario no pueda "volver" a la pantalla de login
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // Cierra MainActivity
    }
    // 🔼--- FIN DEL CAMBIO 3 ---🔼
}

// 🔽--- CAMBIO 4: 'LoginScreen' SIMPLIFICADO ---🔽
// Este Composable ahora solo necesita mostrar el botón de login
@Composable
fun LoginScreen(onLoginClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Por favor, inicia sesión.", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onLoginClicked) {
            Text("Iniciar Sesión")
        }
    }
}
// 🔼--- FIN DEL CAMBIO 4 ---🔼