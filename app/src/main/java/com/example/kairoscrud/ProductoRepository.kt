package com.example.kairoscrud

object ProductoRepository {
    private val productos = mutableListOf<Producto>()
    private var nextId = 1

    fun agregar(nombre: String,descripcion: String, precio: Double): Producto {
        val producto = Producto(nextId++, nombre,descripcion, precio)
        productos.add(producto)
        return producto
    }

    fun listar(): List<Producto> = productos.toList()

    fun actualizar(id: Int, nombre: String,descripcion: String, precio: Double): Boolean {
        val producto = productos.find { it.id == id } ?: return false
        producto.nombre = nombre
        producto.descripcion = descripcion
        producto.precio = precio
        return true
    }

    fun eliminar(id: Int): Boolean {
        return productos.removeIf { it.id == id }
    }
}