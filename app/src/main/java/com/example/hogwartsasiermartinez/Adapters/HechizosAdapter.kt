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

// --- CAMBIO CLAVE: Ahora el constructor acepta dos "listeners" ---
class HechizosAdapter(
    private val onHechizoClick: (Hechizo) -> Unit,
    private val onHechizoLongClick: (Hechizo) -> Unit // NUEVO
) : ListAdapter<Hechizo, HechizosAdapter.HechizoViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HechizoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hechizo, parent, false)
        return HechizoViewHolder(view)
    }

    override fun onBindViewHolder(holder: HechizoViewHolder, position: Int) {
        val hechizo = getItem(position)
        holder.bind(hechizo)

        // Asignamos la acción de clic normal
        holder.itemView.setOnClickListener {
            onHechizoClick(hechizo)
        }

        // --- NUEVO: Asignamos la acción de clic largo ---
        holder.itemView.setOnLongClickListener {
            onHechizoLongClick(hechizo)
            true // Importante para indicar que hemos manejado el evento
        }
    }

    // El ViewHolder y el DiffCallback se quedan exactamente igual que los tenías
    inner class HechizoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreTextView: TextView = itemView.findViewById(R.id.tvNombreHechizo)
        private val descripcionTextView: TextView = itemView.findViewById(R.id.tvDescripcionHechizo)
        private val experienciaTextView: TextView = itemView.findViewById(R.id.tvExperienciaHechizo)

        fun bind(hechizo: Hechizo) {
            nombreTextView.text = hechizo.nombre
            descripcionTextView.text = hechizo.descripcion
            experienciaTextView.text = "+${hechizo.experiencia} EXP"
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Hechizo>() {
        override fun areItemsTheSame(oldItem: Hechizo, newItem: Hechizo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Hechizo, newItem: Hechizo): Boolean {
            return oldItem == newItem
        }
    }
}
