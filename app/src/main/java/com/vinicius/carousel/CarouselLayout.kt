package com.vinicius.carousel

import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
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

class CarouselLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : HorizontalScrollView(context, attrs, defStyleAttr) {

    private val root: CarouselHorizontalLayout
    private var carouselAdapter: CarouselAdapter? = null
    private var configLayoutCarousel = ConfigLayoutCarousel()

    init {
        root = CarouselHorizontalLayout(context, attrs, defStyleAttr)
        addView(root)
    }

    private fun onUpdateItems(updates: List<UpdateCarousel>, clear: Boolean) {
        if(updates.isEmpty()) return

        if(clear) clear()

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
                            val view = it.createView(viewType, root, configLayoutCarousel)
                            addViewAtPosition(view, viewPosition)
                            view
                        }
                        it.bind(viewPosition, view, configLayoutCarousel)
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
                        val view = it.createView(viewType, root, configLayoutCarousel)
                        addViewAtPosition(view, viewPosition)
                        it.bind(viewPosition, view, configLayoutCarousel)
                    }
                }
                else -> Unit
            }
        }

        root.measure()
    }

    private fun addViewAtPosition(view: View, position: Int) {
        if(root.childCount - 1 >= position) {
            root.addView(view, position)
        } else root.addView(view)
    }

    fun setConfigCarouselLayout(new: ConfigLayoutCarousel) {
        configLayoutCarousel = new
    }

    private var moveTo = false

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        val superOnTouchEvent = super.onTouchEvent(ev)

        val actionName = when(ev.action) {
            MotionEvent.ACTION_UP -> "ACTION_UP"
            MotionEvent.ACTION_DOWN -> "ACTION_DOWN"
            MotionEvent.ACTION_MOVE -> "ACTION_MOVE"
            MotionEvent.ACTION_SCROLL -> "ACTION_SCROLL"
            else -> "Other ${ev.action}"
        }
//        Log.i("Vini", "Action: $actionName")

        if(ev.action == MotionEvent.ACTION_MOVE) moveTo = true

        if(ev.action == MotionEvent.ACTION_UP) {
            if(moveTo) {
                root.apply {
                    val items = children.asSequence().map {
                        val globalVisibilityRectangle = Rect()
                        it.getGlobalVisibleRect(globalVisibilityRectangle)
                        Pair(it, globalVisibilityRectangle)
                    }.filter {
                        it.first.isOnTheScreen(it.second)
                    }.map {
                        val percentageShow = ((it.second.right - it.second.left) / it.first.width.toFloat())
                        Pair(it.first, percentageShow)
                    }.sortedByDescending { it.second }.toList()

                    items.firstOrNull()?.let {
                        val initialPosition = it.first.getPositionInParent().first
                        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
                        val width = it.first.width
                        val distance = initialPosition + (width / 2) - (screenWidth / 2)
                        smoothScrollTo(distance, 0)
                    }
                }
            }

            moveTo = false
        }

        return superOnTouchEvent
    }

    private fun View.getPositionInParent(): Pair<Int, Int> {
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

    private fun View.isOnTheScreen(actualPosition: Rect) : Boolean {
        if(!isShown || visibility != VISIBLE) return false

        val width = Resources.getSystem().displayMetrics.widthPixels
        val height = Resources.getSystem().displayMetrics.heightPixels
        val screen = Rect(0, 0, width, height)
        return actualPosition.intersect(screen)
    }

    fun setCarouselAdapter(new: CarouselAdapter?) {
        clear()
        carouselAdapter = new
        carouselAdapter?.setParent(root)
        carouselAdapter?.setOnUpdateItems(::onUpdateItems)
    }

    private fun clear() {
        root.removeAllViews()
    }
}