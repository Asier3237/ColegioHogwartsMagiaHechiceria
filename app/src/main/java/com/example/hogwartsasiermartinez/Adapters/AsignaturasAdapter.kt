package com.example.hogwartsasiermartinez.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.hogwartsasiermartinez.R
import com.example.hogwartsasiermartinez.model.Asignatura
import com.example.hogwartsasiermartinez.model.Hechizo
import com.example.hogwartsasiermartinez.model.Usuario

class AsignaturasAdapter(
    private var asignaturas: List<Asignatura>,
    private val onAsignaturaClick: (Asignatura) -> Unit // le paso una lambda para el click, para poder controlar mejor el código
) : RecyclerView.Adapter<AsignaturasAdapter.AsignaturaViewHolder>() {

    //el ViewHolder obtiene las vistas del layout item_asignatura
    inner class AsignaturaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreTextView: TextView = itemView.findViewById(R.id.nombreAsignaturaTextView)

        fun bind(asignatura: Asignatura) {
            nombreTextView.text = asignatura.nombre

            itemView.setOnClickListener {
                onAsignaturaClick(asignatura) // se ejecuta la función que le pasé sobre esa asignatura
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsignaturaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_asignatura, parent, false)
        return AsignaturaViewHolder(view)
    }

    override fun onBindViewHolder(holder: AsignaturaViewHolder, position: Int) {
        holder.bind(asignaturas[position])
    }

    override fun getItemCount(): Int = asignaturas.size

    fun updateData(nuevaLista: List<Asignatura>) {
        asignaturas = nuevaLista
        // avisamos al adapter de que los datos han cambiado para que se repinte
        notifyDataSetChanged()
    }
}
