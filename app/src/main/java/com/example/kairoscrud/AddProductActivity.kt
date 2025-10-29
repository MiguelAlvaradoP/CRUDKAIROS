package com.example.kairoscrud

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kairoscrud.ui.theme.KAIROSCRUDTheme

class AddProductActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KAIROSCRUDTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AddProductScreen(onAddClicked = { nombre, precio ->
                        if (nombre.isBlank() || precio.isBlank()) {
                            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                            return@AddProductScreen
                        }

                        val nuevo = ProductoRepository.agregar(nombre, precio.toDouble())
                        Toast.makeText(this, "Producto agregado: ${nuevo.nombre}", Toast.LENGTH_SHORT).show()
                        finish() // Cierra esta pantalla y vuelve a ProductActivity
                    })
                }
            }
        }
    }
}

@Composable
fun AddProductScreen(onAddClicked: (String, String) -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Agregar Producto", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre del producto") }
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = precio,
            onValueChange = { precio = it },
            label = { Text("Precio") }
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { onAddClicked(nombre, precio) }) {
            Text("Guardar Producto")
        }
    }
}
