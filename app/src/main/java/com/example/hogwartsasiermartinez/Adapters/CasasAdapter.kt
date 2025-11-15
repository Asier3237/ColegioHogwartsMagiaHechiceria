package com.example.hogwartsasiermartinez.Adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hogwartsasiermartinez.R
import com.example.hogwartsasiermartinez.model.Casa

class CasasAdapter(
    val casas: MutableList<Casa>
) : RecyclerView.Adapter<CasasAdapter.VH>() {

    class VH(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_casa, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val casa = casas[position]
        val tvNombre = holder.view.findViewById<TextView>(R.id.tvNombreCasa)
        val tvPuntos = holder.view.findViewById<TextView>(R.id.tvPuntosCasa)

        tvNombre.text = casa.nombre
        tvPuntos.text = "Puntos: ${casa.puntos}"

        val fondo = when (casa.nombre) {
            "Gryffindor" -> Color.parseColor("#7F0909")
            "Slytherin" -> Color.parseColor("#0D6217")
            "Ravenclaw" -> Color.parseColor("#000A90")
            "Hufflepuff" -> Color.parseColor("#EEE117")
            else -> Color.WHITE
        }
        holder.view.setBackgroundColor(fondo)

    }

    override fun getItemCount() = casas.size

    // 👉 Esta es la función que usas en el Activity
    fun getOrdenCasasId(): List<Int> = casas.map { it.id }

    // Si quieres también devolver nombres:
    fun getOrdenCasasNombre(): List<String> = casas.map { it.nombre }
}

