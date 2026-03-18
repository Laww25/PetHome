package com.example.pethome

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class WelcomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken

                if (idToken.isNullOrEmpty()) {
                    Toast.makeText(this, "No se pudo obtener el token de Google", Toast.LENGTH_LONG).show()
                    return@registerForActivityResult
                }

                firebaseAuthWithGoogle(idToken)
            } catch (e: ApiException) {
                Toast.makeText(this, "Error con Google Sign-In: ${e.statusCode}", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        auth = FirebaseAuth.getInstance()
        configurarGoogleSignIn()

        findViewById<android.view.View>(R.id.btnCorreo).setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }

        findViewById<android.view.View>(R.id.tvIniciaSesion).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        findViewById<android.view.View>(R.id.btnGoogle).setOnClickListener {
            iniciarGoogleSignIn()
        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            startActivity(Intent(this, InicioActivity::class.java))
            finish()
        }
    }

    private fun configurarGoogleSignIn() {
        val webClientId = getString(R.string.default_web_client_id)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun iniciarGoogleSignIn() {
        googleSignInLauncher.launch(googleSignInClient.signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    guardarUsuarioGoogle()
                } else {
                    val msg = task.exception?.message ?: "No se pudo iniciar sesión con Google"
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun guardarUsuarioGoogle() {
        val user = auth.currentUser
        val uid = user?.uid ?: return

        val usuario = Usuario(
            dni = "",
            nombres = user.displayName.orEmpty(),
            apellidoPaterno = "",
            apellidoMaterno = "",
            correo = user.email.orEmpty()
        )

        FirebaseDatabase.getInstance()
            .getReference("usuarios")
            .child(uid)
            .setValue(usuario)
            .addOnSuccessListener {
                startActivity(Intent(this, InicioActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                startActivity(Intent(this, InicioActivity::class.java))
                finish()
            }
    }
}