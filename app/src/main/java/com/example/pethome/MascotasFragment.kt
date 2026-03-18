package com.example.pethome

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MascotasFragment : Fragment() {

    private lateinit var db: DatabaseHelper
    private lateinit var adapter: MascotaAdapter
    private lateinit var listaMascotas: MutableList<Mascota>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.activity_mascotas_fragment, container, false)

        val rv = view.findViewById<RecyclerView>(R.id.rvMascotas)
        val fab = view.findViewById<FloatingActionButton>(R.id.fabAddMascota)

        rv.layoutManager = LinearLayoutManager(requireContext())

        db = DatabaseHelper(requireContext())

        // Demo solo si está vacío
        if (db.getMascotas().isEmpty()) {
            db.insertMascota("Firulais", "Perro", "Labrador", 3)
            db.insertMascota("Michi", "Gato", "Criollo", 2)
            db.insertMascota("Rocky", "Perro", "Pastor Alemán", 5)
        }

        listaMascotas = db.getMascotas().toMutableList()

        adapter = MascotaAdapter(
            listaMascotas,
            onDelete = { mascota ->
                val filas = db.deleteMascota(mascota.id)
                if (filas > 0) {
                    adapter.removeItemById(mascota.id)
                    Toast.makeText(requireContext(), "Mascota eliminada", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "No se pudo eliminar", Toast.LENGTH_SHORT).show()
                }
            },
            onEdit = { mascota, index ->
                mostrarDialogoEditar(mascota, index)
            }
        )

        rv.adapter = adapter

        fab.setOnClickListener { mostrarDialogoAgregar() }

        return view
    }

    private fun mostrarDialogoAgregar() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_mascota, null)

        val etNombre = dialogView.findViewById<EditText>(R.id.etNombre)
        val etTipo = dialogView.findViewById<EditText>(R.id.etTipo)
        val etRaza = dialogView.findViewById<EditText>(R.id.etRaza)
        val etEdad = dialogView.findViewById<EditText>(R.id.etEdad)

        AlertDialog.Builder(requireContext())
            .setTitle("Agregar mascota")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = etNombre.text.toString().trim()
                val tipo = etTipo.text.toString().trim()
                val raza = etRaza.text.toString().trim()
                val edadStr = etEdad.text.toString().trim()

                if (nombre.isEmpty() || tipo.isEmpty() || raza.isEmpty() || edadStr.isEmpty()) {
                    Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val edad = edadStr.toIntOrNull()
                if (edad == null || edad < 0) {
                    Toast.makeText(requireContext(), "Edad inválida", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val newId = db.insertMascota(nombre, tipo, raza, edad).toInt()
                if (newId > 0) {
                    val nueva = Mascota(newId, nombre, tipo, raza, edad)
                    listaMascotas.add(0, nueva)
                    adapter.notifyItemInserted(0)
                } else {
                    Toast.makeText(requireContext(), "No se pudo guardar", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoEditar(mascota: Mascota, index: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_mascota, null)

        val etNombre = dialogView.findViewById<EditText>(R.id.etNombre)
        val etTipo = dialogView.findViewById<EditText>(R.id.etTipo)
        val etRaza = dialogView.findViewById<EditText>(R.id.etRaza)
        val etEdad = dialogView.findViewById<EditText>(R.id.etEdad)

        // Prellenar
        etNombre.setText(mascota.nombre)
        etTipo.setText(mascota.tipo)
        etRaza.setText(mascota.raza)
        etEdad.setText(mascota.edad.toString())

        AlertDialog.Builder(requireContext())
            .setTitle("Editar mascota")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = etNombre.text.toString().trim()
                val tipo = etTipo.text.toString().trim()
                val raza = etRaza.text.toString().trim()
                val edadStr = etEdad.text.toString().trim()

                if (nombre.isEmpty() || tipo.isEmpty() || raza.isEmpty() || edadStr.isEmpty()) {
                    Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val edad = edadStr.toIntOrNull()
                if (edad == null || edad < 0) {
                    Toast.makeText(requireContext(), "Edad inválida", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val filas = db.updateMascota(mascota.id, nombre, tipo, raza, edad)
                if (filas > 0) {
                    val actualizada = Mascota(mascota.id, nombre, tipo, raza, edad)
                    adapter.updateItemAt(index, actualizada)
                    Toast.makeText(requireContext(), "Mascota actualizada", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "No se pudo actualizar", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}