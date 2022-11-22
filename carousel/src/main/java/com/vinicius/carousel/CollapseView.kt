package com.vinicius.carousel

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

abstract class CollapseView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    abstract fun viewsInvisibleCollapse() : List<View>

    abstract fun viewsVisibleCollapse() : List<View>

    open fun defaultCollapseAnimationDuration() : Long = 1000L

    open fun onAnimationCollapseStart(collapse: Boolean, initValueAlpha: Float, endValueAlpha: Float) {

    }

    open fun onAnimationCollapseEnd(collapse: Boolean, initValueAlpha: Float, endValueAlpha: Float) {
        viewsInvisibleCollapse().forEach {
            it.alpha = endValueAlpha
        }
        viewsVisibleCollapse().forEach {
            it.alpha = initValueAlpha
        }
    }

    fun animationCollapse(collapse: Boolean) {
        val initValueAlpha = if (collapse) 1.0f else 0.0f
        val endValueAlpha = if (collapse) 0.0f else 1.0f

        var startValue = endValueAlpha

        val positiveList = if(collapse) viewsVisibleCollapse() else viewsInvisibleCollapse()
        val negativeList = if(collapse) viewsInvisibleCollapse() else viewsVisibleCollapse()

        ValueAnimator.ofFloat(endValueAlpha, initValueAlpha, endValueAlpha).apply {
            interpolator = FastOutSlowInInterpolator()
            duration = defaultCollapseAnimationDuration()
            addUpdateListener { value ->
                val alphaValue = value.animatedValue as Float

                val diff = alphaValue - startValue

                if(diff > 0) {
                    positiveList.forEach { it.alpha = alphaValue }
                } else if(diff < 0) {
                    negativeList.forEach { it.alpha = alphaValue }
                }

                startValue = alphaValue
            }
            doOnStart {
                onAnimationCollapseStart(collapse, initValueAlpha, endValueAlpha)
            }
            doOnEnd {
                onAnimationCollapseEnd(collapse, initValueAlpha, endValueAlpha)
            }
        }.start()
    }

}