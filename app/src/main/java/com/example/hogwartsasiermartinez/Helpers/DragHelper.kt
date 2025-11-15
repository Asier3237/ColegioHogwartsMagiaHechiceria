package com.example.hogwartsasiermartinez.Helpers

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.hogwartsasiermartinez.Adapters.CasasAdapter
import java.util.Collections

class DragHelper(
    private val adapter: CasasAdapter
) : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {

    override fun onMove(
        rv: RecyclerView,
        vh: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val from = vh.bindingAdapterPosition
        val to = target.bindingAdapterPosition
        Collections.swap(adapter.casas, from, to)
        adapter.notifyItemMoved(from, to)
        return true
    }

    override fun onSwiped(vh: RecyclerView.ViewHolder, direction: Int) {
        // No usamos swipe
    }

    // 🔑 Esto asegura que el drag se activa con pulsación larga
    override fun isLongPressDragEnabled(): Boolean = true
}


