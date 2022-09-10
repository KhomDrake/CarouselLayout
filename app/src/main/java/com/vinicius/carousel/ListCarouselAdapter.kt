package com.vinicius.carousel

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

class UpdateCarousel(
    val position: Int,
    val type: UpdateCarouselType,
    val oldPosition: Int
)

enum class UpdateCarouselType {
    MOVE,
    REMOVE,
    UPDATE,
    ADD
}

abstract class ListCarouselAdapter<T : Any>(private val diffUtil: DiffUtil.ItemCallback<T>) : CarouselAdapter() {

    private val items: MutableList<T> = mutableListOf()

    val currentList: List<T>
        get() = items

    override fun bind(position: Int, view: View, configLayoutCarousel: ConfigLayoutCarousel) {
        configLayoutCarousel.configLayout(view.context, view, position, currentList.size)
    }

    override fun getItemCount() = items.size

    abstract override fun createView(viewType: Int, parent: ViewGroup, configLayoutCarousel: ConfigLayoutCarousel): View

    fun notifySetDataChanged() {
        val oldList = items.toList()
        items.clear()
        submitList(oldList, true)
    }

    private fun submitList(newList: List<T>, isSetDataChanged: Boolean) {
        scope.launch {
            val updates = mutableListOf<UpdateCarousel>()
            val removedItems = items.filter { item ->
                !newList.contains(item)
            }.map { items.indexOf(it) }
            newList.forEachIndexed { index, newItem ->
                var isNewItem = true
                var oldPosition = -1
                var hasNewContent = false
                items.forEachIndexed { indexOld, oldItem ->
                    val areItemsTheSame = diffUtil.areItemsTheSame(oldItem, newItem)
                    if(areItemsTheSame) {
                        isNewItem = false
                        val areContentsTheSame = diffUtil.areContentsTheSame(oldItem, newItem)
                        hasNewContent = !areContentsTheSame
                        if(indexOld != index) oldPosition = indexOld
                        return@forEachIndexed
                    }
                }
                if(isNewItem) {
                    updates.add(
                        UpdateCarousel(index, UpdateCarouselType.ADD, oldPosition)
                    )
                }

                if(!isNewItem && oldPosition != -1) {
                    updates.add(
                        UpdateCarousel(index, UpdateCarouselType.MOVE, oldPosition)
                    )
                }

                if(hasNewContent) {
                    updates.add(
                        UpdateCarousel(index, UpdateCarouselType.UPDATE, oldPosition)
                    )
                }
            }
            removedItems.forEach {
                updates.add(UpdateCarousel(it, UpdateCarouselType.REMOVE, it))
            }
            items.clear()
            items.addAll(newList)

            launch(Dispatchers.Main) {
                onUpdate.invoke(updates, isSetDataChanged)
            }
        }
    }

    fun submitList(newList: List<T>) {
        submitList(newList, false)
    }

}