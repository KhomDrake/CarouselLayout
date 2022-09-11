package com.vinicius.carousel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.vinicius.carousel.myviews.ViewBinder

class MyAdapter(var type: MyObjectType = MyObjectType.BLUE): ListCarouselAdapter<MyObject>(MyDiffUtil()) {

    override fun bind(position: Int, view: View, configLayoutCarousel: ConfigLayoutCarousel) {
        super.bind(position, view, configLayoutCarousel)
        val data = currentList[position]
        (view as? ViewBinder)?.bind(data)
    }

    override fun createView(
        viewType: Int,
        parent: ViewGroup,
        configLayoutCarousel: ConfigLayoutCarousel
    ): View {
        val layout = when (viewType) {
            MyObjectType.BLUE.ordinal -> R.layout.blue_item
            MyObjectType.PURPLE.ordinal -> R.layout.purple_item
            else -> R.layout.red_item
        }
        return LayoutInflater.from(parent.context).inflate(layout, parent, false)
    }

    override fun getViewType(position: Int): Int {
        return type.ordinal
    }

}

enum class MyObjectType {
    BLUE,
    RED,
    PURPLE
}

class MyObject(val id: Int, var name: String)

class MyDiffUtil: DiffUtil.ItemCallback<MyObject>() {
    override fun areItemsTheSame(oldItem: MyObject, newItem: MyObject): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MyObject, newItem: MyObject): Boolean {
        return oldItem.name == newItem.name
    }

}