package com.vinicius.carousel

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.children
import androidx.core.view.doOnPreDraw

internal class CarouselHorizontalLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    init {
        orientation = HORIZONTAL
        layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private var viewsAdding = 0

    fun measure() {
        doOnPreDraw {
            val maxHeight = children.asSequence().sortedByDescending { it.height }
                .firstOrNull()?.height ?: return@doOnPreDraw
            children.asSequence().forEach {
                it.layoutParams?.height = maxHeight
                it.requestLayout()
            }
        }
    }

}