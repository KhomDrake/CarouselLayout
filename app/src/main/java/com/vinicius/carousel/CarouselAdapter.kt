package com.vinicius.carousel

import android.view.View
import android.view.ViewGroup

abstract class CarouselAdapter internal constructor() {

    private lateinit var parent: ViewGroup
    protected var onUpdate: (List<UpdateCarousel>, Boolean) -> Unit = { _, _ -> }

    abstract fun bind(position: Int, view: View, configLayoutCarousel: ConfigLayoutCarousel)

    abstract fun getItemCount(): Int

    open fun getViewType(position: Int): Int = 0

    abstract fun createView(viewType: Int, parent: ViewGroup, configLayoutCarousel: ConfigLayoutCarousel): View

    fun setParent(parent: ViewGroup) {
        this.parent = parent
    }

    fun setOnUpdateItems(update: (List<UpdateCarousel>, Boolean) -> Unit) {
        onUpdate = update
    }

}