package com.vinicius.carousel

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.RecyclerView

internal class ContentCarouselLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    internal var collapse: Boolean = false
    private var itemAdded: Boolean = false
    private var horizontalWithIndicator: IndicatorsCarouselLayout? = null

    init {
        orientation = HORIZONTAL
        layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        configHorizontalWithIndicator()
    }

    fun configHorizontalWithIndicator(
        colorSelectedId: Int = R.color.purple_700,
        colorNoSelectedId: Int = R.color.teal_700,
        paddingBottom: Int = R.dimen.bottom_margin
    ) {
        val selectedWidthPixels: Int = context.getDimension(R.dimen.start_and_end_margin)
        val spacingBetweenInPixels: Int = context.getDimension(R.dimen.between)
        val colorSelected = ContextCompat.getColor(context, colorSelectedId)
        val colorNoSelected = ContextCompat.getColor(context, colorNoSelectedId)
        val paddingBottomToIndicator = context.getDimension(paddingBottom)
        val ySegmentSize = selectedWidthPixels / 4
        val spacingToDrawIndicators = paddingBottomToIndicator + ySegmentSize
        setPadding(paddingStart, paddingTop, paddingEnd, spacingToDrawIndicators)
        horizontalWithIndicator = IndicatorsCarouselLayout(
            spacingToDrawIndicators,
            selectedWidthPixels,
            ySegmentSize,
            spacingBetweenInPixels,
            colorSelected,
            colorNoSelected
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        updateViewsToSameHeight()
    }

    fun setItemAdded(newValue: Boolean) {
        itemAdded = newValue
    }

    internal fun collapse() {
        doOnPreDraw {
            children.map { it as? CollapseInterface }.forEach { it?.animationCollapse(collapse) }
        }
    }

    fun onDrawOver(canvas: Canvas, scrollX: Int) {
        horizontalWithIndicator?.onDrawOver(canvas, this, scrollX)
    }

    private fun updateViewsToSameHeight() {
        doOnPreDraw {
            val maxHeight = children.asSequence().sortedByDescending { it.measuredHeight }
                .firstOrNull()?.measuredHeight ?: return@doOnPreDraw

            val itemsWithDifferenceHeights = children.asSequence()
                .filter { it.measuredHeight != maxHeight }.toList()

            Log.i("Vini", maxHeight.toString())
            Log.i("Vini", itemsWithDifferenceHeights.map { it.measuredHeight }.toString())

            itemsWithDifferenceHeights.forEach { it.layoutParams?.height = maxHeight }
            if(itemsWithDifferenceHeights.isNotEmpty()) requestLayout()

            if(itemAdded && itemsWithDifferenceHeights.isEmpty()) {
                itemAdded = false
                collapse()
            }
        }
    }

    internal fun findFirstCompletelyVisible() : Int {
        return children.asSequence()
            .mapIndexed { index, view -> Pair(index, view.isCompletelyVisible()) }
            .filter { it.second }
            .firstOrNull()?.first ?: RecyclerView.NO_POSITION
    }

    internal fun findViewsDisplayed() = run {
        children.asSequence().mapIndexed { index, view ->
            val globalVisibilityRectangle = Rect()
            view.getGlobalVisibleRect(globalVisibilityRectangle)
            Triple(view, globalVisibilityRectangle, index)
        }.filter {
            it.first.isOnTheScreen(it.second)
        }.map {
            val percentageShow = ((it.second.right - it.second.left) / it.first.width.toFloat())
            Triple(it.first, percentageShow, it.third)
        }.sortedByDescending { it.second }.toList()
    }

}