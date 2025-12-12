package com.example.hogwartsasiermartinez.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hogwartsasiermartinez.R
import com.example.hogwartsasiermartinez.model.Hechizo

class HechizosAdapter(
    private val onHechizoClick: (Hechizo) -> Unit,
    private val onHechizoLongClick: (Hechizo) -> Unit
) : ListAdapter<Hechizo, HechizosAdapter.HechizoViewHolder>(DiffCallback()) {

    // aquí se crea el molde para cada fila, usando el layout item_hechizo
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HechizoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hechizo, parent, false)
        return HechizoViewHolder(view)
    }

    // esta se encarga de rellenar cada fila con sus datos y ponerle los listeners para los clicks
    override fun onBindViewHolder(holder: HechizoViewHolder, position: Int) {
        val hechizo = getItem(position)
        holder.bind(hechizo)

        // accion click normal
        holder.itemView.setOnClickListener {
            onHechizoClick(hechizo)
        }

        // accion click largo
        holder.itemView.setOnLongClickListener {
            onHechizoLongClick(hechizo)
            true
        }
    }

    // guarda las vistas de cada fila
    class HechizoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreTextView: TextView = itemView.findViewById(R.id.tvNombreHechizo)
        private val descripcionTextView: TextView = itemView.findViewById(R.id.tvDescripcionHechizo)
        private val experienciaTextView: TextView = itemView.findViewById(R.id.tvExperienciaHechizo)

        // esta función pone los datos de cada hechizo en los textviews de la fila
        fun bind(hechizo: Hechizo) {
            nombreTextView.text = hechizo.nombre
            descripcionTextView.text = hechizo.descripcion
            experienciaTextView.text = "+${hechizo.experiencia} EXP"
        }
    }

    // se usa para saber que ha cambiado en la lista
    class DiffCallback : DiffUtil.ItemCallback<Hechizo>() {
        // comprueba si dos items son el mismo (por el id)
        override fun areItemsTheSame(oldItem: Hechizo, newItem: Hechizo): Boolean {
            return oldItem.id == newItem.id
        }

        // comprueba si el contenido de un item ha cambiado
        override fun areContentsTheSame(oldItem: Hechizo, newItem: Hechizo): Boolean {
            return oldItem == newItem
        }
    }
}