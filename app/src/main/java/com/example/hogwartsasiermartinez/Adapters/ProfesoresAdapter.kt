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

    // guarda las vistas de cada fila
    class ProfesorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.tvNombre)
        val casa: TextView = itemView.findViewById(R.id.tvCasa)
        val rol: TextView = itemView.findViewById(R.id.tvRol)
    }

    // aquí se crea el molde para cada fila, usando el layout item_usuario
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfesorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_usuario, parent, false)
        return ProfesorViewHolder(view)
    }

    // rellena cada fila con los datos del profesor
    override fun onBindViewHolder(holder: ProfesorViewHolder, position: Int) {
        val profesor = profesores[position]

        holder.nombre.text = profesor.nombre
        holder.casa.text = "Casa ID: ${profesor.casa_id}"
        holder.rol.text = "Rol: profesor"

        // le ponemos la acción para cuando se haga click en una fila
        holder.itemView.setOnClickListener { onProfesorClick(profesor) }
    }

    // saca el tamaño de la lista
    override fun getItemCount(): Int = profesores.size

    // actualiza la lista de profesores
    fun updateData(nuevaLista: List<Usuario>) {
        profesores = nuevaLista
        // avisamos al adapter de que los datos han cambiado para que se repinte
        notifyDataSetChanged()
    }
}