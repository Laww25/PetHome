package com.example.pethome

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text

class HistorialAdapter (private val lista: List<Mascota>) :
        RecyclerView.Adapter<HistorialAdapter.ViewHolder>(){

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreMascota)
        val tvTipo: TextView = view.findViewById(R.id.tvTipoMascota)
        val tvEdad: TextView = view.findViewById(R.id.tvEdadMascota)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historial, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mascota = lista[position]
        holder.tvNombre.text = mascota.nombre
        holder.tvTipo.text = mascota.tipo
        holder.tvEdad.text = "${mascota.edad} años"
    }
    override fun  getItemCount() = lista.size

}