package com.vinicius.carousel

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Rect
import android.os.SystemClock
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import androidx.core.view.children
import androidx.transition.TransitionManager

class CarouselLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : HorizontalScrollView(context, attrs, defStyleAttr) {

    private val content: ContentCarouselLayout
    private val defaultInterval = 100
    private var configLayoutCarousel = ConfigLayoutCarousel()
    private var onSwipe: (Int) -> Unit = {}
    private var moveTo = false
    private var previousX = -1f
    private var lastClickTime: Long = -1
    private var selectedItem = -1

    var carouselAdapter: CarouselAdapter? = null
        private set

    fun setCollapse(newValue: Boolean) : CarouselLayout {
        content.collapseHelper?.collapse = newValue
        return this
    }

    init {
        content = ContentCarouselLayout(context, attrs, defStyleAttr)
        content.id = R.id.content_carousel_layout
        addView(content)
    }

    private fun onUpdateItems(updates: List<UpdateCarousel>, clear: Boolean) {
        if(updates.isEmpty()) return

        if(clear) clear()

        val movesAndRemoves = updates.filter {
            it.type == UpdateCarouselType.REMOVE || it.type == UpdateCarouselType.MOVE
        }
        val rest = updates.filter {
            it.type != UpdateCarouselType.REMOVE && it.type != UpdateCarouselType.MOVE
        }

        TransitionManager.beginDelayedTransition(content)

        if(movesAndRemoves.isNotEmpty()) {
            val views = mutableListOf<View?>()
            content.children.iterator().forEach {
                views.add(it)
            }
            content.removeAllViews()
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
                if(views.size > it.first.position)
                    views[it.first.position] = it.second
                else
                    views.add(it.second)
            }

            views.filterNotNull().forEach {
                if (it.parent == null) content.addView(it)
            }
        }

        rest.forEach { updateCarousel ->
            val viewPosition = updateCarousel.position
            val child: View? = content.getChildAt(viewPosition)
            when(updateCarousel.type) {
                UpdateCarouselType.UPDATE -> {
                    carouselAdapter?.let {
                        val viewType = it.getViewType(viewPosition)
                        val view = child ?: run {
                            val view = it.createView(viewType, content, configLayoutCarousel)
                            addViewAtPosition(view, viewPosition)
                            view
                        }
                        it.bind(viewPosition, view, configLayoutCarousel)
                    }
                }
                UpdateCarouselType.ADD -> {
                    carouselAdapter?.let {
                        val viewType = it.getViewType(viewPosition)
                        val view = it.createView(viewType, content, configLayoutCarousel)
                        addViewAtPosition(view, viewPosition)
                        it.bind(viewPosition, view, configLayoutCarousel)
                    }
                }
                else -> Unit
            }
        }

        val hasItemAdded = updates.find { it.type == UpdateCarouselType.ADD } != null
        updateChildren()
        content.setItemAdded(hasItemAdded)
    }

    fun collapse() {
        content.collapse()
    }

    fun setCollapseHelper(collapseHelper: CollapseHelper?) {
        content.collapseHelper = collapseHelper
    }

    private fun updateChildren() {
        val adapter = carouselAdapter ?: return
        content.children.forEachIndexed { index, view ->
            configLayoutCarousel.configLayout(view.context, view, index, adapter.getItemCount())
        }
    }

    private fun addViewAtPosition(view: View, position: Int) {
        if(content.childCount - 1 >= position) {
            content.addView(view, position)
        } else content.addView(view)
    }

    fun setConfigCarouselLayout(new: ConfigLayoutCarousel) : CarouselLayout {
        configLayoutCarousel = new
        return this
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        val superOnTouchEvent = super.onTouchEvent(ev)

        if(ev.action == MotionEvent.ACTION_MOVE && !moveTo) {
            previousX = ev.x
            lastClickTime = SystemClock.elapsedRealtime()
            moveTo = true
        }

        if(ev.action == MotionEvent.ACTION_UP) {
            if(moveTo) {
                content.apply {
                    val items = findViewsDisplayed()

                    val isSling = SystemClock.elapsedRealtime() - lastClickTime < defaultInterval

                    items.firstOrNull()?.let {
                        val isTheSame = it.third == selectedItem
                        val distance = if(isTheSame && isSling) {
                            val direction = if(previousX - ev.x > 0) 1 else -1
                            val position = it.third + direction
                            val child = content.getChildAt(position) ?: return@let
                            selectedItem = position
                            val initialPosition = child.getPositionInParent().first
                            val screenWidth = Resources.getSystem().displayMetrics.widthPixels
                            val width = it.first.width
                            initialPosition + (width / 2) - (screenWidth / 2)
                        } else {
                            selectedItem = it.third
                            val initialPosition = it.first.getPositionInParent().first
                            val screenWidth = Resources.getSystem().displayMetrics.widthPixels
                            val width = it.first.width
                            initialPosition + (width / 2) - (screenWidth / 2)
                        }

                        onSwipe.invoke(selectedItem)
                        smoothScrollTo(distance, 0)
                    }
                }
            }

            moveTo = false
        }

        return superOnTouchEvent
    }

    fun onSwipeListener(on: (Int) -> Unit) = run {
        onSwipe = on
        this
    }

    fun setCarouselAdapter(new: CarouselAdapter?) {
        clear()
        carouselAdapter = new
        carouselAdapter?.setParent(content)
        carouselAdapter?.setOnUpdateItems(::onUpdateItems)
    }

    private fun clear() {
        content.removeAllViews()
    }
}

internal fun View.getPositionInParent(): Pair<Int, Int> {
    val parent = parent as ViewGroup
    val positionPreview = intArrayOf(1, 2)
    val positionFrame = intArrayOf(1, 2)

    parent.getLocationInWindow(positionPreview)
    getLocationInWindow(positionFrame)

    return Pair(
        positionFrame[0] - positionPreview[0],
        positionFrame[1] - positionPreview[1]
    )
}

internal fun View.isOnTheScreen(actualPosition: Rect) : Boolean {
    if(!isShown || visibility != View.VISIBLE) return false

    val width = Resources.getSystem().displayMetrics.widthPixels
    val height = Resources.getSystem().displayMetrics.heightPixels
    val screen = Rect(0, 0, width, height)
    return actualPosition.intersect(screen)
}

internal fun View.isOnTheScreen() : Boolean {
    if(!isShown || visibility != View.VISIBLE) return false

    val width = Resources.getSystem().displayMetrics.widthPixels
    val height = Resources.getSystem().displayMetrics.heightPixels
    val actualPosition = Rect()
    getGlobalVisibleRect(actualPosition)
    val screen = Rect(0, 0, width, height)
    return actualPosition.intersect(screen)
}

internal fun View.isCompletelyVisible() : Boolean {
    if(!isShown || visibility != View.VISIBLE) return false

    val globalVisibilityRectangle = Rect()
    getGlobalVisibleRect(globalVisibilityRectangle)
    val visibleWidth = globalVisibilityRectangle.right - globalVisibilityRectangle.left
    return visibleWidth == measuredWidth && isOnTheScreen()
}
