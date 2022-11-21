package com.vinicius.carousel.collapse

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.vinicius.carousel.ConfigLayoutCarousel
import com.vinicius.carousel.ListCarouselAdapter
import com.vinicius.carousel.collapse.views.CharacterView

class Character(
    val id: Int,
    val name: String,
    val description: String
)

class CharacterDiffUtil : DiffUtil.ItemCallback<Character>() {
    override fun areContentsTheSame(oldItem: Character, newItem: Character): Boolean {
        return oldItem.description == newItem.description && oldItem.name == newItem.name
    }
    override fun areItemsTheSame(oldItem: Character, newItem: Character): Boolean {
        return oldItem.id == newItem.id
    }
}

class CollapseAdapter : ListCarouselAdapter<Character>(CharacterDiffUtil()) {
    override fun createView(
        viewType: Int,
        parent: ViewGroup,
        configLayoutCarousel: ConfigLayoutCarousel
    ): View {
        return CharacterView(parent.context)
    }

    override fun bind(position: Int, view: View, configLayoutCarousel: ConfigLayoutCarousel) {
        super.bind(position, view, configLayoutCarousel)
        if(view is CharacterView) {
            view.bind(currentList[position])
        }
    }
}