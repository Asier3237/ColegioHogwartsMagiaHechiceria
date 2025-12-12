package com.example.hogwartsasiermartinez.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hogwartsasiermartinez.R
import com.example.hogwartsasiermartinez.model.Casa

class RankingCasasAdapter : ListAdapter<Casa, RankingCasasAdapter.CasaViewHolder>(DiffCallback()) {

    // aquí se crea el molde para cada fila, usando el layout item_casa
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CasaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_casa, parent, false)
        return CasaViewHolder(view)
    }

    // rellena cada fila con los datos de la casa
    override fun onBindViewHolder(holder: CasaViewHolder, position: Int) {
        val casa = getItem(position)
        holder.bind(casa)
    }

    // guarda las vistas de las filas
    class CasaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombreCasa: TextView = itemView.findViewById(R.id.tvNombreCasa)
        private val tvPuntos: TextView = itemView.findViewById(R.id.tvPuntosCasa)

        // pone los datos de la casa en los textviews
        fun bind(casa: Casa) {
            tvNombreCasa.text = casa.nombre
            tvPuntos.text = "${casa.puntos} puntos"
        }
    }

    // esto es lo que usa el listadapter para saber qué ha cambiado en la lista
    class DiffCallback : DiffUtil.ItemCallback<Casa>() {
        // comprueba si dos items son el mismo (por el id)
        override fun areItemsTheSame(oldItem: Casa, newItem: Casa): Boolean {
            return oldItem.id == newItem.id
        }

        // comprueba si el contenido de un item ha cambiado (gracias a que es una data class)
        override fun areContentsTheSame(oldItem: Casa, newItem: Casa): Boolean {
            return oldItem == newItem
        }
    }
}