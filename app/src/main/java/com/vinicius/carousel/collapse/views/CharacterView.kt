package com.vinicius.carousel.collapse.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import br.com.arch.toolkit.delegate.viewProvider
import com.vinicius.carousel.CollapseView
import com.vinicius.carousel.R
import com.vinicius.carousel.collapse.Character

class CharacterView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CollapseView(context, attrs, defStyleAttr) {

    private val title: AppCompatTextView by viewProvider(R.id.title)
    private val description: AppCompatTextView by viewProvider(R.id.description)
    private val collapsedTitle: AppCompatTextView by viewProvider(R.id.collapsed_title)

    init {
        LayoutInflater.from(context).inflate(R.layout.character_item, this)
        layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        setBackgroundResource(R.drawable.background_blue)
        outlineProvider = ViewOutlineProvider.BACKGROUND
        clipToOutline = true
    }

    override fun defaultCollapseAnimationDuration(): Long {
        return 700L
    }

    override fun viewsInvisibleCollapse() = listOf(title, description)

    override fun viewsVisibleCollapse() = listOf(collapsedTitle)

    fun bind(character: Character) {
        title.text = character.name
        collapsedTitle.text = character.name
        description.text = character.description
    }

}