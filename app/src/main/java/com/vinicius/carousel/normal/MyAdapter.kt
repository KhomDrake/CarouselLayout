package com.vinicius.carousel.normal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.vinicius.carousel.R
import com.vinicius.carousel.normal.views.ViewBinder

class MyAdapter(var type: MyObjectType = MyObjectType.BLUE): com.vinicius.carousel.ListCarouselAdapter<MyObject>(
    MyDiffUtil()
) {

    override fun bind(position: Int, view: View, configLayoutCarousel: com.vinicius.carousel.ConfigLayoutCarousel) {
        super.bind(position, view, configLayoutCarousel)
        val data = currentList[position]
        (view as? ViewBinder)?.bind(data)
    }

    override fun createView(
        viewType: Int,
        parent: ViewGroup,
        configLayoutCarousel: com.vinicius.carousel.ConfigLayoutCarousel
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