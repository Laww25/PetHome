package com.example.pethome

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var etCorreo: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnIngresar: Button
    private lateinit var tvRegistro: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1) Firebase Auth
        auth = FirebaseAuth.getInstance()

        // 2) Referencias UI
        etCorreo = findViewById(R.id.etCorreo)
        etPassword = findViewById(R.id.etPassword)
        btnIngresar = findViewById(R.id.btnIngresar)
        tvRegistro = findViewById(R.id.tvRegistro)

        // 3) Login
        btnIngresar.setOnClickListener {
            login()
        }

        // 4) Ir a Registro
        tvRegistro.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }
    }

    // Auto-login: si ya hay sesión, entra directo
    override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        if (user != null) {
            irAInicio()
        }
    }

    private fun login() {
        val email = etCorreo.text.toString().trim()
        val pass = etPassword.text.toString().trim()

        if (email.isEmpty()) {
            Toast.makeText(this, "Ingrese correo", Toast.LENGTH_SHORT).show()
            return
        }
        if (pass.isEmpty()) {
            Toast.makeText(this, "Ingrese contraseña", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Bienvenido ✅", Toast.LENGTH_SHORT).show()
                    irAInicio()
                } else {
                    val error = task.exception

                    val mensaje = when (error) {
                        is FirebaseAuthInvalidUserException -> "El usuario no existe"
                        is FirebaseAuthInvalidCredentialsException -> "Correo o contraseña incorrectos"
                        is FirebaseAuthException -> "Error de autenticación"
                        else -> "Error al iniciar sesión"
                    }

                    Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun irAInicio() {
        startActivity(Intent(this, InicioActivity::class.java))
        finish()
    }
}