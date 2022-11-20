package com.vinicius.carousel

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

data class UpdateCarousel(
    val position: Int,
    val type: UpdateCarouselType,
    val oldPosition: Int
) {
    companion object {
        fun <T : Any>calculateUpdate(
            diffUtil: DiffUtil.ItemCallback<T>,
            oldList: List<T>,
            newList: List<T>
        ) : List<UpdateCarousel> {
            val updates = mutableListOf<UpdateCarousel>()

            val removedItems = oldList.filter { item ->
                newList.find { newItem -> diffUtil.areItemsTheSame(newItem, item) } == null
            }.map { oldList.indexOf(it) }

            newList.forEachIndexed { index, newItem ->
                var isNewItem = true
                var oldPosition = -1
                var hasNewContent = false
                oldList.forEachIndexed { indexOld, oldItem ->
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

                if(!isNewItem && oldPosition != -1 && oldPosition + (newList.size - oldList.size) != index) {
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

            return updates
        }
    }
}

enum class UpdateCarouselType {
    MOVE,
    REMOVE,
    UPDATE,
    ADD
}

abstract class ListCarouselAdapter<T : Any>(private val diffUtil: DiffUtil.ItemCallback<T>) : CarouselAdapter() {

    private var items: List<T> = mutableListOf()

    val currentList: List<T>
        get() = items

    override fun bind(position: Int, view: View, configLayoutCarousel: ConfigLayoutCarousel) {

    }

    override fun getItemCount() = items.size

    abstract override fun createView(viewType: Int, parent: ViewGroup, configLayoutCarousel: ConfigLayoutCarousel): View

    fun clearData() {
        val oldList = items.toList()
        items = listOf()
        submitList(oldList, true)
    }

    fun updateAll() {
        val updates = mutableListOf<UpdateCarousel>()
        items.forEachIndexed { index, _ ->
            updates.add(
                UpdateCarousel(index, UpdateCarouselType.UPDATE, index)
            )
        }
        onUpdate.invoke(updates, false)
    }

    private fun submitList(newList: List<T>, clear: Boolean) {
        scope.launch {
            val updates = UpdateCarousel.calculateUpdate(diffUtil, items, newList)
            items = newList

            launch(Dispatchers.Main) {
                onUpdate.invoke(updates, clear)
            }
        }
    }

    fun submitList(newList: List<T>) {
        submitList(newList, false)
    }

}