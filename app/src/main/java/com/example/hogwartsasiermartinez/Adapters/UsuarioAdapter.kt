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

    // se definen las acciones para el clic normal (editar) y el clic largo (borrar)
    var onUserLongClick: ((Usuario) -> Unit)? = null
    var onUserClick: ((Usuario) -> Unit)? = null

    // aquí se crea el molde para cada fila, usando el layout item_usuaroi
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_usuario, parent, false)
        return UsuarioViewHolder(view)
    }

    // rellena cada fila con sus datos y le pone las acciones de los clicks
    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = getItem(position)
        holder.bind(usuario)

        holder.itemView.setOnLongClickListener {
            onUserLongClick?.invoke(usuario)
            true
        }

        holder.itemView.setOnClickListener {
            onUserClick?.invoke(usuario)
        }
    }

    // guarda las vistas de las filas
    class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombre: TextView = itemView.findViewById(R.id.tvNombre)
        private val casa: TextView = itemView.findViewById(R.id.tvCasa)

        // uso un map para sacar el nombre de la casa por su id
        private val nombresCasas = mapOf(
            1 to "Gryffindor",
            2 to "Slytherin",
            3 to "Ravenclaw",
            4 to "Hufflepuff"
        )

        // pone los datos del usuario en los textviews
        fun bind(usuario: Usuario) {
            nombre.text = usuario.nombre
            casa.text = "Casa: ${nombresCasas[usuario.casa_id] ?: "Sin casa"}"
        }
    }

    // se usa para saber que ha cambiado en la lista
    class DiffCallback : DiffUtil.ItemCallback<Usuario>() {
        // comprueba si son el mismo usuario (por el id)
        override fun areItemsTheSame(oldItem: Usuario, newItem: Usuario): Boolean {
            return oldItem.id == newItem.id
        }

        // comprueba si los datos del usuario han cambiado
        override fun areContentsTheSame(oldItem: Usuario, newItem: Usuario): Boolean {
            return oldItem == newItem
        }
    }
}