// Ubicación: com/example/kairoscrud/ProductActivity.kt

package com.example.kairoscrud

import android.content.Intent // 🔽 CAMBIO 1: Importar Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.example.kairoscrud.ui.theme.KAIROSCRUDTheme

class ProductActivity : ComponentActivity() {

    private lateinit var account: Auth0
    private lateinit var credentialsManager: CredentialsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        account = Auth0(
            getString(R.string.com_auth0_client_id),
            getString(R.string.com_auth0_domain)
        )
        credentialsManager = CredentialsManager(this)

        setContent {
            KAIROSCRUDTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ProductScreen(
                        onLogoutClicked = { logout() },
                        onAddProductClicked = { navigateToAddProduct() } // 🔽 CAMBIO 2: Pasar la nueva función
                    )
                }
            }
        }
    }

    // 🔽 CAMBIO 3: Nueva función para navegar
    private fun navigateToAddProduct() {
        // Inicia la AddProductActivity
        startActivity(Intent(this, AddProductActivity::class.java))
    }

    private fun logout() {
        WebAuthProvider.logout(account)
            .withScheme(getString(R.string.com_auth0_scheme))
            .start(this, object : Callback<Void?, AuthenticationException> {
                override fun onFailure(error: AuthenticationException) {
                    Toast.makeText(
                        this@ProductActivity,
                        "Error al cerrar sesión: ${error.getDescription() ?: error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onSuccess(result: Void?) {
                    credentialsManager.clearCredentials()
                    Toast.makeText(this@ProductActivity, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@ProductActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            })
    }

}

@Composable
fun ProductScreen(
    onLogoutClicked: () -> Unit,
    onAddProductClicked: () -> Unit // 🔽 CAMBIO 4: Agregar nuevo parámetro
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("¡Bienvenido!", style = MaterialTheme.typography.headlineSmall)
        Text("Aquí va tu lista de productos (RecyclerView o LazyColumn)")

        Spacer(modifier = Modifier.height(48.dp))

        // Conectar el onClick
        Button(onClick = onAddProductClicked) { // 🔽 CAMBIO 5: Usar el nuevo parámetro
            Text("Agregar Producto")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onLogoutClicked) {
            Text("Cerrar Sesión")
        }
    }
}