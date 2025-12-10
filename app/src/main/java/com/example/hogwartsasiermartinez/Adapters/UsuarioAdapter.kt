package com.example.hogwartsasiermartinez.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hogwartsasiermartinez.R
import com.example.hogwartsasiermartinez.model.Usuario

class UsuarioAdapter : ListAdapter<Usuario, UsuarioAdapter.UsuarioViewHolder>(DiffCallback()) {

    // Variable para manejar el clic largo (borrar)
    var onUserLongClick: ((Usuario) -> Unit)? = null
    // Variable NUEVA para manejar el clic normal (editar rol)
    var onUserClick: ((Usuario) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_usuario, parent, false)
        return UsuarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = getItem(position)
        holder.bind(usuario)

        // Asignamos la lógica para el clic largo
        holder.itemView.setOnLongClickListener {
            onUserLongClick?.invoke(usuario)
            true // Requerido para el clic largo
        }

        // Asignamos la lógica para el clic normal
        holder.itemView.setOnClickListener {
            onUserClick?.invoke(usuario)
        }
    }

    inner class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombre: TextView = itemView.findViewById(R.id.tvNombre)
        private val casa: TextView = itemView.findViewById(R.id.tvCasa)

        // Mapa de casas para mostrar el nombre en lugar del ID
        private val nombresCasas = mapOf(
            1 to "Gryffindor",
            2 to "Slytherin",
            3 to "Ravenclaw",
            4 to "Hufflepuff"
        )

        fun bind(usuario: Usuario) {
            nombre.text = usuario.nombre
            casa.text = "Casa: ${nombresCasas[usuario.casa_id] ?: "Sin casa"}"
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Usuario>() {
        override fun areItemsTheSame(oldItem: Usuario, newItem: Usuario): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Usuario, newItem: Usuario): Boolean {
            return oldItem == newItem
        }
    }
}
