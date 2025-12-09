package com.example.hogwartsasiermartinez.Adapters

import com.example.hogwartsasiermartinez.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hogwartsasiermartinez.model.Usuario

class UsuarioAdapter : RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>() {

    private var usuarios: List<Usuario> = emptyList()
    var onUserLongClick: ((Usuario) -> Unit)? = null


    fun submitList(lista: List<Usuario>) {
        usuarios = lista
        notifyDataSetChanged()
    }

    class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.tvNombre)
        val casa: TextView = itemView.findViewById(R.id.tvCasa)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_usuario, parent, false)
        return UsuarioViewHolder(view)
    }

    val nombresCasas = mapOf(
        1 to "Gryffindor",
        2 to "Slytherin",
        3 to "Ravenclaw",
        4 to "Hufflepuff"
    )

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = usuarios[position]
        holder.nombre.text = usuario.nombre
        holder.casa.text = "Casa: ${nombresCasas[usuario.casa_id]}"

        holder.itemView.setOnLongClickListener {
            onUserLongClick?.invoke(usuario)
            true
        }

    }

    override fun getItemCount() = usuarios.size
}
