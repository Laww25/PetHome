package com.example.pethome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class InicioFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_inicio, container, false)

        val tvSaludo = view.findViewById<TextView>(R.id.tvSaludo)
        val tvContador = view.findViewById<TextView>( R.id.tvContadorMascotas)

        // Cargar nombre del usuario desde Firebase

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null){
            FirebaseDatabase.getInstance()
                .getReference("usuarios")
                .child(uid)
                .get()
                .addOnSuccessListener { snapshot ->
                    val u = snapshot.getValue(Usuario::class.java)
                    if (u != null) {
                        tvSaludo.text = "¡Hola, ${u.nombres}!"
                    }
                }
        }
        // Contar mascotas desde SQLite

        val db = DatabaseHelper(requireContext())
        val total = db.getMascotas().size
        tvContador.text = total.toString()

        // Accesos rapidos

        view.findViewById<TextView>(R.id.btnIrMascotas).setOnClickListener {
            (activity as? InicioActivity)?.navegarA(R.id.itMascotas)
        }
        view.findViewById<TextView>(R.id.btnIrHistorial).setOnClickListener {
            (activity as? InicioActivity)?.navegarA(R.id.itHistorial)
        }
        view.findViewById<TextView>(R.id.btnIrSeguimiento).setOnClickListener {
            (activity as? InicioActivity)?.navegarA(R.id.itSeguimiento)
        }

        return view
    }
}
