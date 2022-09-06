package com.vinicius.carousel

import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.setPadding

abstract class CarouselAdapter<T>() {

    private var list: MutableList<T> = mutableListOf()

}

class CarouselLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : HorizontalScrollView(context, attrs, defStyleAttr) {

    private val root: LinearLayoutCompat

    init {
        root = LinearLayoutCompat(context).apply {
            orientation = LinearLayoutCompat.HORIZONTAL
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        addView(root)
        addTexts(root)
    }

    private var moveTo = false

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if(ev.action == MotionEvent.ACTION_MOVE) {
            moveTo = true
        }

        if(ev.action == MotionEvent.ACTION_UP) {
            if(moveTo) {
                Log.i("Vini", "Devia mover pro meio")
                val midPositionGlobal = Resources.getSystem().displayMetrics.widthPixels / 2
//                (getChildAt(0) as? ViewGroup)?.apply {
//                    children.iterator().forEach { child ->
//                        if(child.isCompletelyVisible()) {
//                            val globalVisibilityRectangle = Rect()
//                            child.getGlobalVisibleRect(globalVisibilityRectangle)
//                            val midPosition = globalVisibilityRectangle.left + (child.width)
//                            val distance = midPositionGlobal - midPosition
//                            scrollTo(distance, 0)
//                        }
//                    }
//                }
            }
            moveTo = false
        }

        return super.onTouchEvent(ev)
    }

    private fun View.isCompletelyVisible(): Boolean {
        if(!isShown || visibility != VISIBLE) return false

        val globalVisibilityRectangle = Rect()
        getGlobalVisibleRect(globalVisibilityRectangle)
        val visibleWidth = globalVisibilityRectangle.right - globalVisibilityRectangle.left
        val actualWidth = measuredWidth
        return visibleWidth == actualWidth && isOnTheScreen()
    }

    private fun View.isOnTheScreen() : Boolean {
        if(!isShown || visibility != VISIBLE) return false

        val width = Resources.getSystem().displayMetrics.widthPixels
        val height = Resources.getSystem().displayMetrics.heightPixels
        val actualPosition = Rect()
        getGlobalVisibleRect(actualPosition)
        val screen = Rect(0, 0, width, height)
        return actualPosition.intersect(screen)
    }

    private fun addTexts(viewGroup: ViewGroup) {

        listOf("Test222222222222222", "Test222222222222222222", "Test32222222222222", "Test42222222222222", "Test52222222222").forEach {
            viewGroup.addView(
                TextView(context).apply {
                    text = it
                    setPadding(16)
                }
            )
        }
    }
}