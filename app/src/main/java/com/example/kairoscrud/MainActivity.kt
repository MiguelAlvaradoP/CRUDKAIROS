package com.example.kairoscrud

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private lateinit var rvProductos: RecyclerView
    private lateinit var adapter: ProductoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvProductos = findViewById(R.id.rvProductos)
        rvProductos.layoutManager = LinearLayoutManager(this)

        adapter = ProductoAdapter(ProductoRepository.listar(),
            onEdit = { producto -> mostrarDialogoEditar(producto) },
            onDelete = { producto ->
                ProductoRepository.eliminar(producto.id)
                actualizarLista()
            })
        rvProductos.adapter = adapter

        val btnAgregar: MaterialButton = findViewById(R.id.btnAgregar)
        btnAgregar.setOnClickListener { mostrarDialogoAgregar() }
    }

    private fun actualizarLista() {
        adapter.updateList(ProductoRepository.listar())
    }

    private fun mostrarDialogoAgregar() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialogo_producto, null)
        val etNombre = dialogView.findViewById<EditText>(R.id.etNombre)
        val etPrecio = dialogView.findViewById<EditText>(R.id.etPrecio)

        AlertDialog.Builder(this)
            .setTitle("Agregar Producto")
            .setView(dialogView)
            .setPositiveButton("Agregar") { _, _ ->
                val nombre = etNombre.text.toString()
                val precio = etPrecio.text.toString().toDoubleOrNull() ?: 0.0
                ProductoRepository.agregar(nombre, precio)
                actualizarLista()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoEditar(producto: Producto) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialogo_producto, null)
        val etNombre = dialogView.findViewById<EditText>(R.id.etNombre)
        val etPrecio = dialogView.findViewById<EditText>(R.id.etPrecio)

        etNombre.setText(producto.nombre)
        etPrecio.setText(producto.precio.toString())

        AlertDialog.Builder(this)
            .setTitle("Editar Producto")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = etNombre.text.toString()
                val precio = etPrecio.text.toString().toDoubleOrNull() ?: 0.0
                ProductoRepository.actualizar(producto.id, nombre, precio)
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
