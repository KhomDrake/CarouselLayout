package com.vinicius.carousel

import android.content.Context
import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DimenRes

class ConfigLayoutCarousel(
    private val edgeWidth: Int = R.dimen.edge_width,
    private val startAndEndMargin: Int = R.dimen.start_and_end_margin,
    private val betweenMargin: Int = R.dimen.between_margin,
    private val timesEdgeWidthOnlyOneItem: Float = 4f,
    private val timesEdgeWidthMoreItems: Float = 8f,
    private val percentageOfScreen: Float = 1f
) {

    fun configLayout(context: Context, view: View, position: Int, size: Int) {
        view.layoutParams?.apply {
            val edgeWidthRealValue = context.getDimension(edgeWidth)
            width = when(size) {
                1 -> {
                    (Resources.getSystem().displayMetrics.widthPixels * percentageOfScreen).toInt() -
                            (timesEdgeWidthOnlyOneItem * edgeWidthRealValue).toInt()
                }
                else -> (Resources.getSystem().displayMetrics.widthPixels * percentageOfScreen).toInt() -
                        (timesEdgeWidthMoreItems * edgeWidthRealValue).toInt()
            }
        }

        val marginStart = if(position == 0) context.getDimension(startAndEndMargin)
            else context.getDimension(betweenMargin)

        val marginEnd = if(position == size - 1) context.getDimension(startAndEndMargin)
            else context.getDimension(betweenMargin)

        (view.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
            setMargins(
                marginStart,
                topMargin,
                marginEnd,
                bottomMargin
            )
        }
    }

}

fun Context.getDimension(@DimenRes id: Int) = resources.getDimension(id).toInt()