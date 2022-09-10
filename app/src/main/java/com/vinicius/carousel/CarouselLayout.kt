package com.vinicius.carousel

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.recyclerview.widget.DiffUtil
import androidx.transition.TransitionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.abs

private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

abstract class CarouselAdapter {

    private lateinit var parent: ViewGroup
    protected var onUpdate: (List<UpdateCarousel>) -> Unit = { }

    abstract fun bind(position: Int, view: View)

    abstract fun getItemCount(): Int

    open fun getViewType(position: Int): Int = 0

    abstract fun createView(viewType: Int, parent: ViewGroup): View

    fun setParent(parent: ViewGroup) {
        this.parent = parent
    }

    fun setOnUpdateItems(update: (List<UpdateCarousel>) -> Unit) {
        onUpdate = update
    }

}

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

    protected val items: MutableList<T> = mutableListOf()

    val currentList: List<T>
        get() = items

    abstract override fun bind(position: Int, view: View)

    override fun getItemCount() = items.size

    abstract override fun createView(viewType: Int, parent: ViewGroup): View

    fun submitList(newList: List<T>) {
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
                onUpdate.invoke(updates)
            }
        }
    }

}

class MyAdapter: ListCarouselAdapter<MyObject>(MyDiffUtil()) {

    override fun bind(position: Int, view: View) {
        view.findViewById<AppCompatTextView>(R.id.title).text = items[position].name
    }

    override fun createView(viewType: Int, parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
    }

}

class MyObject(val id: Int, val name: String)

class MyDiffUtil: DiffUtil.ItemCallback<MyObject>() {
    override fun areItemsTheSame(oldItem: MyObject, newItem: MyObject): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MyObject, newItem: MyObject): Boolean {
        return oldItem.name == newItem.name
    }

}


class CarouselLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : HorizontalScrollView(context, attrs, defStyleAttr) {

    private val root: LinearLayoutCompat
    private var carouselAdapter: CarouselAdapter? = null

    init {
        root = LinearLayoutCompat(context).apply {
            orientation = LinearLayoutCompat.HORIZONTAL
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        addView(root)
    }

    private fun onUpdateItems(updates: List<UpdateCarousel>) {
        val movesAndRemoves =
            updates.filter { it.type == UpdateCarouselType.REMOVE || it.type == UpdateCarouselType.MOVE }
        val rest = updates.filter {
            it.type != UpdateCarouselType.REMOVE
                && it.type != UpdateCarouselType.MOVE
        }
        TransitionManager.beginDelayedTransition(root)

        if(movesAndRemoves.isNotEmpty()) {
            val views = mutableListOf<View?>()
            root.children.iterator().forEach {
                views.add(it)
            }
            root.removeAllViews()
            val new = movesAndRemoves.map { update ->
                val view = if(update.type == UpdateCarouselType.MOVE) {
                    views[update.oldPosition]
                } else {
                    views[update.oldPosition]
                }
                Pair(update, view)
            }

            new.filter { it.first.type == UpdateCarouselType.REMOVE }.forEach {
                if(views[it.first.oldPosition] == it.second) {
                    views[it.first.oldPosition] = null
                }
            }

            views.filterNotNull().takeIf { it.isNotEmpty() }?.let {
                views.clear()
                views.addAll(it)
            }

            new.filter { it.first.type == UpdateCarouselType.MOVE }.forEach {
                views[it.first.position] = it.second
            }

            views.filterNotNull().forEach {
                root.addView(it)
            }
        }



        rest.forEach { updateCarousel ->
            val viewPosition = updateCarousel.position
            val child: View? = root.getChildAt(viewPosition)
            when(updateCarousel.type) {
                UpdateCarouselType.UPDATE -> {
                    carouselAdapter?.let {
                        val viewType = it.getViewType(viewPosition)
                        val view = child ?: run {
                            val view = it.createView(viewType, root)
                            addViewAtPosition(view, viewPosition)
                            view
                        }
                        it.bind(viewPosition, view)
                    }
                }
                UpdateCarouselType.MOVE -> {
                    val oldChild = root.getChildAt(updateCarousel.oldPosition)

                    oldChild?.let {
                        root.removeView(it)
                        addViewAtPosition(it, updateCarousel.position)
                    }
                }
                UpdateCarouselType.ADD -> {
                    carouselAdapter?.let {
                        val viewType = it.getViewType(viewPosition)
                        val view = it.createView(viewType, root)
                        addViewAtPosition(view, viewPosition)
                        it.bind(viewPosition, view)
                    }
                }
                else -> Unit
            }
        }
    }

    private fun addViewAtPosition(view: View, position: Int) {
        if(root.childCount - 1 >= position) {
            root.addView(view, position)
        } else root.addView(view)
    }

    fun setCarouselAdapter(new: CarouselAdapter?) {
        carouselAdapter = new
        carouselAdapter?.setParent(root)
        carouselAdapter?.setOnUpdateItems(::onUpdateItems)
        if(carouselAdapter == null) {
            clear()
        }
    }

    fun clear() {
        root.removeAllViews()
    }
}