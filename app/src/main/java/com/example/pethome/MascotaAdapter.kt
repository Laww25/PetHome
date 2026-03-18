package com.example.pethome

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MascotaAdapter(
    private val lista: MutableList<Mascota>,
    private val onDelete: (Mascota) -> Unit,
    private val onEdit: (Mascota, Int) -> Unit
) : RecyclerView.Adapter<MascotaAdapter.MascotaViewHolder>() {

    class MascotaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvTipo: TextView = itemView.findViewById(R.id.tvTipo)
        val tvEdad: TextView = itemView.findViewById(R.id.tvEdad)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MascotaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mascota, parent, false)
        return MascotaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MascotaViewHolder, position: Int) {
        val mascota = lista[position]

        holder.tvNombre.text = mascota.nombre
        holder.tvTipo.text = "Tipo: ${mascota.tipo}"
        holder.tvEdad.text = "Edad: ${mascota.edad} años"

        //  Click normal = editar
        holder.itemView.setOnClickListener {
            onEdit(mascota, holder.bindingAdapterPosition)
        }

        // ✅Mantener presionado = eliminar
        holder.itemView.setOnLongClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Eliminar")
                .setMessage("¿Eliminar a ${mascota.nombre}?")
                .setPositiveButton("Sí") { _, _ -> onDelete(mascota) }
                .setNegativeButton("No", null)
                .show()
            true
        }
    }

    override fun getItemCount(): Int = lista.size

    fun removeItemById(id: Int) {
        val index = lista.indexOfFirst { it.id == id }
        if (index != -1) {
            lista.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun updateItemAt(index: Int, nueva: Mascota) {
        if (index in 0 until lista.size) {
            lista[index] = nueva
            notifyItemChanged(index)
        }
    }
}