package com.example.kairoscrud

import android.app.AlertDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.kairoscrud.Producto
import com.example.kairoscrud.ProductoRepository

class MainActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView = findViewById(R.id.listViewProductos)
        val btnAgregar: Button = findViewById(R.id.btnAgregar)

        actualizarLista()

        btnAgregar.setOnClickListener {
            mostrarDialogoAgregar()
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val producto = ProductoRepository.listar()[position]
            mostrarDialogoEditar(producto)
        }
    }

    private fun actualizarLista() {
        val nombres = ProductoRepository.listar().map { "${it.nombre} - $${it.precio}" }
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, nombres)
        listView.adapter = adapter
    }

    private fun mostrarDialogoAgregar() {
        val dialogView = layoutInflater.inflate(R.layout.dialogo_producto, null)
        val nombre = dialogView.findViewById<EditText>(R.id.etNombre)
        val precio = dialogView.findViewById<EditText>(R.id.etPrecio)

        AlertDialog.Builder(this)
            .setTitle("Agregar Producto")
            .setView(dialogView)
            .setPositiveButton("Agregar") { _, _ ->
                val n = nombre.text.toString()
                val p = precio.text.toString().toDoubleOrNull() ?: 0.0
                ProductoRepository.agregar(n, p)
                actualizarLista()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoEditar(producto: Producto) {
        val dialogView = layoutInflater.inflate(R.layout.dialogo_producto, null)
        val nombre = dialogView.findViewById<EditText>(R.id.etNombre)
        val precio = dialogView.findViewById<EditText>(R.id.etPrecio)

        nombre.setText(producto.nombre)
        precio.setText(producto.precio.toString())

        AlertDialog.Builder(this)
            .setTitle("Editar Producto")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val n = nombre.text.toString()
                val p = precio.text.toString().toDoubleOrNull() ?: 0.0
                ProductoRepository.actualizar(producto.id, n, p)
                actualizarLista()
            }
            .setNeutralButton("Eliminar") { _, _ ->
                ProductoRepository.eliminar(producto.id)
                actualizarLista()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
