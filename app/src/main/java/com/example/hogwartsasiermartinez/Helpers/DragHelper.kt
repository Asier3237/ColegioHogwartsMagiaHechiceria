package com.example.hogwartsasiermartinez.Helpers

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.hogwartsasiermartinez.Adapters.CasasAdapter

class DragHelper(
    private val adapter: CasasAdapter
) : ItemTouchHelper.SimpleCallback(
    // se indica que solo puede arrastrarse hacia arriba o hacia abajo
    ItemTouchHelper.UP or ItemTouchHelper.DOWN,
    // y que no se puede deslizar a los lados
    0
) {

    // esta función se llama cuando movemos un item por encima de otro
    override fun onMove(
        rv: RecyclerView,
        vh: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        // se pilla la posición original y la nueva
        val from = vh.bindingAdapterPosition
        val to = target.bindingAdapterPosition

        // le pasamos las posiciones al adapter para que se cambie el orden en la lista
        adapter.onItemMove(from, to)
        return true
    }

    // esta es para el gesto de deslizar, pero como no se usa, no hace nada
    override fun onSwiped(vh: RecyclerView.ViewHolder, direction: Int) {}

    // esot hace que el arrastre del item solo se pueda hacer si lo mantenemos pulsado
    override fun isLongPressDragEnabled(): Boolean = true
}