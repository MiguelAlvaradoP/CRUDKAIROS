package com.example.kairoscrud

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
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

        adapter = ProductoAdapter(
            ProductoRepository.listar(),
            onEdit = { producto -> mostrarDialogoEditar(producto) },
            onDelete = { producto ->
                ProductoRepository.eliminar(producto.id)
                actualizarLista()
                Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show()
            }
        )
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
        val etDescripcion = dialogView.findViewById<EditText>(R.id.etDescripcion)
        val etPrecio = dialogView.findViewById<EditText>(R.id.etPrecio)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Agregar Producto")
            .setView(dialogView)
            .setPositiveButton("Agregar", null) // <- No cerramos aún
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val descripcion = etDescripcion.text.toString().trim()
            val precioTexto = etPrecio.text.toString().trim()

            // 🔍 Validaciones
            when {
                nombre.isEmpty() -> {
                    Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                }
                descripcion.isEmpty() -> {
                    Toast.makeText(this, "La descripción no puede estar vacía", Toast.LENGTH_SHORT).show()
                }
                precioTexto.isEmpty() -> {
                    Toast.makeText(this, "Ingrese un precio", Toast.LENGTH_SHORT).show()
                }
                precioTexto.toDoubleOrNull() == null -> {
                    Toast.makeText(this, "Ingrese un número válido para el precio", Toast.LENGTH_SHORT).show()
                }
                precioTexto.toDouble() <= 0 -> {
                    Toast.makeText(this, "El precio debe ser mayor que 0", Toast.LENGTH_SHORT).show()
                }
                ProductoRepository.listar().any { it.nombre.equals(nombre, true) } -> {
                    Toast.makeText(this, "Ya existe un producto con ese nombre", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // ✅ Todo bien → agregar
                    val precio = precioTexto.toDouble()
                    ProductoRepository.agregar(nombre, descripcion, precio)
                    actualizarLista()
                    Toast.makeText(this, "Producto agregado correctamente", Toast.LENGTH_SHORT).show()
                    dialog.dismiss() // Cierra solo si pasa todo
                }
            }
        }
    }

    private fun mostrarDialogoEditar(producto: Producto) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialogo_producto, null)
        val etNombre = dialogView.findViewById<EditText>(R.id.etNombre)
        val etDescripcion = dialogView.findViewById<EditText>(R.id.etDescripcion)
        val etPrecio = dialogView.findViewById<EditText>(R.id.etPrecio)

        etNombre.setText(producto.nombre)
        etDescripcion.setText(producto.descripcion)
        etPrecio.setText(producto.precio.toString())

        AlertDialog.Builder(this)
            .setTitle("Editar Producto")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = etNombre.text.toString().trim()
                val descripcion = etDescripcion.text.toString().trim()
                val precioTexto = etPrecio.text.toString().trim()

                // 🔍 Validaciones
                if (nombre.isEmpty()) {
                    Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (descripcion.isEmpty()) {
                    Toast.makeText(this, "La descripción no puede estar vacía", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val precio = precioTexto.toDoubleOrNull()
                if (precio == null || precio <= 0.0) {
                    Toast.makeText(this, "Ingrese un precio válido mayor que 0", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                ProductoRepository.actualizar(producto.id, nombre, descripcion, precio)
                actualizarLista()
                Toast.makeText(this, "Producto actualizado", Toast.LENGTH_SHORT).show()
            }
            .setNeutralButton("Eliminar") { _, _ ->
                ProductoRepository.eliminar(producto.id)
                actualizarLista()
                Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
