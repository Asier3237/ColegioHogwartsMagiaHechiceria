package com.example.hogwartsasiermartinez.Helpers

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.hogwartsasiermartinez.Adapters.CasasAdapter

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
        adapter.onItemMove(from, to)
        return true
    }

    override fun onSwiped(vh: RecyclerView.ViewHolder, direction: Int) {
        // No se usa de momento
    }

    override fun isLongPressDragEnabled(): Boolean = true
}
