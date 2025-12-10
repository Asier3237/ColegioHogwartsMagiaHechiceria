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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PocionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pocion, parent, false)
        return PocionViewHolder(view)
    }

    override fun onBindViewHolder(holder: PocionViewHolder, position: Int) {
        val pocion = getItem(position)
        holder.bind(pocion)
        holder.itemView.setOnClickListener { onPocionClick(pocion) }
        holder.itemView.setOnLongClickListener {
            onPocionLongClick(pocion)
            true
        }
    }

    inner class PocionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombre: TextView = itemView.findViewById(R.id.tvNombrePocion)
        private val resumen: TextView = itemView.findViewById(R.id.tvResumenPocion)
        private val estado: TextView = itemView.findViewById(R.id.tvEstadoPocion)
        private val card: CardView = itemView.findViewById(R.id.cardPocion) // Asumimos que la raíz es un CardView

        fun bind(pocion: Pocima) {
            nombre.text = pocion.nombre
            resumen.text = pocion.resumen

            when (pocion.validada) {
                0 -> {
                    estado.text = "Pendiente"
                    card.setCardBackgroundColor(Color.parseColor("#FFC107")) // Amarillo
                }
                1 -> {
                    estado.text = "Validada (${pocion.tipo})"
                    card.setCardBackgroundColor(Color.parseColor("#4CAF50")) // Verde
                }
                2 -> {
                    estado.text = "Rechazada"
                    card.setCardBackgroundColor(Color.parseColor("#F44336")) // Rojo
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Pocima>() {
        override fun areItemsTheSame(oldItem: Pocima, newItem: Pocima): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Pocima, newItem: Pocima): Boolean = oldItem == newItem
    }
}
