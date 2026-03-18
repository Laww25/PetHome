package com.example.pethome

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class PerfilFragment : Fragment() {

    private lateinit var tvNombres: TextView
    private lateinit var tvApellidos: TextView
    private lateinit var tvDni: TextView
    private lateinit var tvCorreo: TextView

    private lateinit var btnCerrarSesion: Button
    private lateinit var btnProbarNotificacion: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_perfil, container, false)

        tvNombres = view.findViewById(R.id.tvNombres)
        tvApellidos = view.findViewById(R.id.tvApellidos)
        tvDni = view.findViewById(R.id.tvDni)
        tvCorreo = view.findViewById(R.id.tvCorreo)

        btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion)
        btnProbarNotificacion = view.findViewById(R.id.btnProbarNotificacion)

        cargarPerfil()

        //  LOGOUT
        btnCerrarSesion.setOnClickListener {

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build()

            val googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

            googleSignInClient.signOut().addOnCompleteListener {

                FirebaseAuth.getInstance().signOut()

                val intent = Intent(requireContext(), WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)

                requireActivity().finish()
            }
        }

        btnProbarNotificacion.setOnClickListener {
            val work = OneTimeWorkRequestBuilder<RecordatorioWorker>().build()
            WorkManager.getInstance(requireContext()).enqueue(work)
            Toast.makeText(requireContext(), "Notificación enviada ✅", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun cargarPerfil() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(requireContext(), "No hay sesión", Toast.LENGTH_SHORT).show()
            return
        }

        val uid = user.uid

        FirebaseDatabase.getInstance()
            .getReference("usuarios")
            .child(uid)
            .get()
            .addOnSuccessListener { snapshot ->
                val u = snapshot.getValue(Usuario::class.java)
                if (u != null) {
                    tvNombres.text = "Nombres: ${u.nombres}"
                    tvApellidos.text = "Apellidos: ${u.apellidoPaterno} ${u.apellidoMaterno}"
                    tvDni.text = "DNI: ${u.dni}"
                    tvCorreo.text = "Correo: ${u.correo}"
                } else {
                    Toast.makeText(requireContext(), "Perfil no encontrado", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}