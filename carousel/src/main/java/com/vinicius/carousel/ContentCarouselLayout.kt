package com.vinicius.carousel

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.DimenRes
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.children
import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.RecyclerView

class CollapseHelper(
    context: Context,
    val collapsePercentage: Float,
    val maxHeightPercentage: Float,
    @DimenRes
    minHeightRes: Int,
    var collapse: Boolean = false,
    var animateCollapse: Boolean = false,
    val animationDuration: Long = 1000L
) {

    private val minHeightPx: Int

    init {
        minHeightPx = context.getDimension(minHeightRes)
    }

    fun collapsedHeight(
        lastMaxHeight: Float,
        collapsePercentage: Float
    ) : Float {
        val minHeight = lastMaxHeight * collapsePercentage

        val minCollapsedHeight = minHeightPx * collapsePercentage

        return if(minCollapsedHeight > minHeight) minCollapsedHeight else minHeight
    }

    fun maxHeight(lastMaxHeight: Float, maxHeightPercentage: Float) : Float {
        val maxHeightAllowed = minHeightPx * maxHeightPercentage

        return if(lastMaxHeight > maxHeightAllowed) maxHeightAllowed else lastMaxHeight
    }

}

private const val DEFAULT_LAST_MAX_HEIGHT = -5f

internal class ContentCarouselLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    internal var collapseHelper: CollapseHelper? = null
    private var lastMaxHeight: Float = DEFAULT_LAST_MAX_HEIGHT
    private var shouldCollapsed: Boolean = false
    private var inAnimation = false

    init {
        orientation = HORIZONTAL
        layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        updateViewsToSameHeight()
    }

    fun setItemAdded(newValue: Boolean) {
        shouldCollapsed = newValue
    }

    internal fun collapse() {
        collapseHelper?.animateCollapse = false
        doOnPreDraw {
            val collapseHelper = collapseHelper ?: return@doOnPreDraw

            val collapse = collapseHelper.collapse

            val collapseViews = children.asSequence()
                .filter { it is CollapseView }
                .map { it as CollapseView }.toList()

            if(collapseViews.isEmpty()) return@doOnPreDraw

            val firstView = collapseViews.first()

            val collapsePercentage = collapseHelper.collapsePercentage
            val maxHeightPercentage = collapseHelper.maxHeightPercentage

            val maxHeight = collapseHelper.maxHeight(lastMaxHeight, maxHeightPercentage)
            val collapsedHeight = collapseHelper.collapsedHeight(lastMaxHeight, collapsePercentage)

            val initial = if(collapse) maxHeight else collapsedHeight
            val end = if(collapse) collapsedHeight else maxHeight

            val anim = ValueAnimator.ofInt(initial.toInt(), end.toInt())
            anim.addUpdateListener {
                val newHeight = it.animatedValue as Int
                collapseViews.forEach { view ->
                    view.layoutParams?.height = newHeight
                    view.requestLayout()
                }
            }
            anim.duration = collapseHelper.animationDuration
            anim.doOnStart {
                inAnimation = true
            }
            anim.doOnEnd {
                inAnimation = false
            }

            collapseViews.forEach { it.animationCollapse(collapse) }

            anim.start()
        }
    }

    private fun updateViewsToSameHeight() {
        doOnPreDraw {
            val maxHeight = children.asSequence().sortedByDescending { it.measuredHeight }
                .firstOrNull()?.measuredHeight ?: return@doOnPreDraw

            val itemsWithDifferenceHeights = children.asSequence()
                .filter { it.measuredHeight != maxHeight }.toList()

            itemsWithDifferenceHeights.forEach { it.layoutParams?.height = maxHeight }
            if(itemsWithDifferenceHeights.isNotEmpty()) requestLayout()

            if(maxHeight != lastMaxHeight.toInt() && !inAnimation && collapseHelper?.collapse != true
                || lastMaxHeight == DEFAULT_LAST_MAX_HEIGHT)
                lastMaxHeight = maxHeight.toFloat()

            if(itemsWithDifferenceHeights.isNotEmpty()) return@doOnPreDraw

            if(collapseHelper?.animateCollapse == true) {
                collapseHelper?.animateCollapse = false
                shouldCollapsed = false
                collapse()
                return@doOnPreDraw
            }

            if(shouldCollapsed && collapseHelper?.animateCollapse == false) {
                collapseHelper?.animateCollapse = false
                shouldCollapsed = false
                collapse()
                return@doOnPreDraw
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