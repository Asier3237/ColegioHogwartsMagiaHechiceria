package com.example.hogwartsasiermartinez.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hogwartsasiermartinez.R
import com.example.hogwartsasiermartinez.model.Asignatura

// 1. Añade un parámetro al constructor para manejar el clic: onAsignaturaClick
class AsignaturasAdapter(
    private var asignaturas: List<Asignatura>,
    private val onAsignaturaClick: (Asignatura) -> Unit
) : RecyclerView.Adapter<AsignaturasAdapter.AsignaturaViewHolder>() {

    // El ViewHolder obtiene las vistas del layout item_asignatura.xml
    inner class AsignaturaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Asegúrate de que tu layout 'item_asignatura.xml' tenga un TextView con este ID
        private val nombreTextView: TextView = itemView.findViewById(R.id.nombreAsignaturaTextView)

        fun bind(asignatura: Asignatura) {
            nombreTextView.text = asignatura.nombre
            // 2. Aquí se asigna el listener a toda la vista del item
            itemView.setOnClickListener {
                onAsignaturaClick(asignatura)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsignaturaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_asignatura, parent, false) // Asegúrate que el layout se llame así
        return AsignaturaViewHolder(view)
    }

    override fun onBindViewHolder(holder: AsignaturaViewHolder, position: Int) {
        holder.bind(asignaturas[position])
    }

    override fun getItemCount(): Int = asignaturas.size

    // Función para actualizar los datos del adapter y refrescar la lista
    fun updateData(nuevaLista: List<Asignatura>) {
        this.asignaturas = nuevaLista
        notifyDataSetChanged() // Notifica al RecyclerView que los datos han cambiado
    }
}
