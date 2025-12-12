package com.example.hogwartsasiermartinez.Adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.Model.Pocima
import com.example.hogwartsasiermartinez.R

class PocionesAdapter(
    private val onPocionClick: (Pocima) -> Unit,
    private val onPocionLongClick: (Pocima) -> Unit
) : ListAdapter<Pocima, PocionesAdapter.PocionViewHolder>(DiffCallback()) {

    // aquí se crea el molde para cada fila, usando el layout item_pocion
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PocionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pocion, parent, false)
        return PocionViewHolder(view)
    }

    // aquí rellenamos cada fila con los datos y le ponemos las acciones de click
    override fun onBindViewHolder(holder: PocionViewHolder, position: Int) {
        val pocion = getItem(position)
        holder.bind(pocion)
        holder.itemView.setOnClickListener { onPocionClick(pocion) }
        holder.itemView.setOnLongClickListener {
            onPocionLongClick(pocion)
            true
        }
    }

    // el viewholder es el molde que guarda las vistas de una sola fila
    inner class PocionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombre: TextView = itemView.findViewById(R.id.tvNombrePocion)
        private val resumen: TextView = itemView.findViewById(R.id.tvResumenPocion)
        private val estado: TextView = itemView.findViewById(R.id.tvEstadoPocion)
        private val card: CardView = itemView.findViewById(R.id.cardPocion)

        // esta función pone los datos en los textviews y cambia el color según el estado
        fun bind(pocion: Pocima) {
            nombre.text = pocion.nombre
            resumen.text = pocion.resumen

            when (pocion.validada) {
                0 -> {
                    estado.text = "Pendiente"
                    card.setCardBackgroundColor(Color.parseColor("#FFC107")) // amarillo
                }
                1 -> {
                    estado.text = "Validada (${pocion.tipo})"
                    card.setCardBackgroundColor(Color.parseColor("#4CAF50")) // verde
                }
                2 -> {
                    estado.text = "Rechazada"
                    card.setCardBackgroundColor(Color.parseColor("#F44336")) // rojo
                }
            }
        }
    }

    // esto es lo que usa el listadapter para saber qué ha cambiado en la lista
    class DiffCallback : DiffUtil.ItemCallback<Pocima>() {
        // comprueba si son la misma poción (por el id)
        override fun areItemsTheSame(oldItem: Pocima, newItem: Pocima): Boolean = oldItem.id == newItem.id
        // comprueba si los datos de la poción han cambiado
        override fun areContentsTheSame(oldItem: Pocima, newItem: Pocima): Boolean = oldItem == newItem
    }
}