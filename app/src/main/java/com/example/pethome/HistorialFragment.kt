package com.example.pethome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HistorialFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_historial, container, false)

        val rv = view.findViewById<RecyclerView>(R.id.rvHistorial)
        val tvTotal = view.findViewById<TextView>(R.id.tvTotalMascotas)

        val db = DatabaseHelper(requireContext())
        val mascotas = db.getMascotas()

        tvTotal.text = "${mascotas.size} mascotas"

        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = HistorialAdapter(mascotas)

        return view
    }
}









