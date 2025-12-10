package com.example.hogwartsasiermartinez.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hogwartsasiermartinez.R
import com.example.hogwartsasiermartinez.model.Usuario

class ProfesoresAdapter(
    private var profesores: List<Usuario>,
    private val onProfesorClick: (Usuario) -> Unit
) : RecyclerView.Adapter<ProfesoresAdapter.ProfesorViewHolder>() {

    class ProfesorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.tvNombre)
        val casa: TextView = itemView.findViewById(R.id.tvCasa)
        val rol: TextView = itemView.findViewById(R.id.tvRol)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfesorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_usuario, parent, false)
        return ProfesorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfesorViewHolder, position: Int) {
        val profesor = profesores[position]
        holder.nombre.text = profesor.nombre
        holder.casa.text = "Casa ID: ${profesor.casa_id}"
        holder.rol.text = "Rol: profesor"

        holder.itemView.setOnClickListener { onProfesorClick(profesor) }
    }

    override fun getItemCount(): Int = profesores.size

    fun updateData(nuevaLista: List<Usuario>) {
        profesores = nuevaLista
        notifyDataSetChanged()
    }
}
