package com.example.pethome

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegistroActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var etDni: TextInputEditText
    private lateinit var etNombres: TextInputEditText
    private lateinit var etApellidoPaterno: TextInputEditText
    private lateinit var etApellidoMaterno: TextInputEditText
    private lateinit var etCorreo: TextInputEditText
    private lateinit var etPassword: TextInputEditText

    private lateinit var btnGuardar: Button
    private lateinit var btnVolver: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        // Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Referencias UI
        etDni = findViewById(R.id.etDni)
        etNombres = findViewById(R.id.etNombres)
        etApellidoPaterno = findViewById(R.id.etApellidoPaterno)
        etApellidoMaterno = findViewById(R.id.etApellidoMaterno)
        etCorreo = findViewById(R.id.etCorreo)
        etPassword = findViewById(R.id.etPassword)

        btnGuardar = findViewById(R.id.btnGuardar)
        btnVolver = findViewById(R.id.btnVolver)

        btnVolver.setOnClickListener {
            finish()
        }

        btnGuardar.setOnClickListener {
            registrarYGuardar()
        }
    }

    private fun registrarYGuardar() {
        val dni = etDni.text?.toString()?.trim() ?: ""
        val nombres = etNombres.text?.toString()?.trim() ?: ""
        val apPat = etApellidoPaterno.text?.toString()?.trim() ?: ""
        val apMat = etApellidoMaterno.text?.toString()?.trim() ?: ""
        val correo = etCorreo.text?.toString()?.trim() ?: ""
        val pass = etPassword.text?.toString()?.trim() ?: ""

        // Validaciones rápidas
        if (dni.isEmpty() || nombres.isEmpty() || apPat.isEmpty() || apMat.isEmpty()) {
            Toast.makeText(this, "Completa tus datos personales", Toast.LENGTH_SHORT).show()
            return
        }
        if (correo.isEmpty()) {
            Toast.makeText(this, "Ingrese correo", Toast.LENGTH_SHORT).show()
            return
        }
        if (pass.isEmpty()) {
            Toast.makeText(this, "Ingrese contraseña", Toast.LENGTH_SHORT).show()
            return
        }
        if (pass.length < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return
        }

        // 1) Crear usuario en Firebase Auth
        auth.createUserWithEmailAndPassword(correo, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val uid = auth.currentUser?.uid
                    if (uid == null) {
                        Toast.makeText(this, "No se pudo obtener UID", Toast.LENGTH_SHORT).show()
                        return@addOnCompleteListener
                    }

                    // 2) Crear objeto usuario
                    val usuario = Usuario(
                        dni = dni,
                        nombres = nombres,
                        apellidoPaterno = apPat,
                        apellidoMaterno = apMat,
                        correo = correo
                    )

                    // 3) Guardar en Realtime Database: usuarios/{uid}
                    FirebaseDatabase.getInstance()
                        .getReference("usuarios")
                        .child(uid)
                        .setValue(usuario)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Registro completo ✅", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, InicioActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al guardar perfil: ${e.message}", Toast.LENGTH_LONG).show()
                        }

                } else {
                    val msg = task.exception?.message ?: "Error al registrar"
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                }
            }
    }
}