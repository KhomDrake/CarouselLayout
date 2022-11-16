package com.vinicius.carousel

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.ColorInt
import androidx.core.view.children
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView

internal class IndicatorsCarouselLayout(
    private val spacingToDrawIndicators: Int,
    private val selectedWidth: Int,
    private val ySegmentSize: Int,
    private val spacingBetween: Int,
    @ColorInt colorSelected: Int,
    @ColorInt colorNoSelected: Int
) {

    private class XSegment(var initialPoint: Float, var segmentSizePoint: Float)

    private val paintFillSelected = Paint().apply {
        style = Paint.Style.FILL
        color = colorSelected
        isAntiAlias = true
    }

    private val paintFillNoSelected = Paint().apply {
        style = Paint.Style.FILL
        color = colorNoSelected
        isAntiAlias = true
    }

    private val indicators = mutableListOf<Pair<XSegment, Float>>()
    private val noSelectedWidth: Int = selectedWidth / 2
    private var numberOfIndicators: Int = 0
    private var indicatorTotalWidth: Int = 0
    private var bottom = 0f
    private var initialIndicatorPosX: Float = 0.0f
    private var indicatorPosY: Float = 0.0f
    private var radius: Float = 0.0f
    private var lastCompletelyVisibleItem: Int = 0
    private var actualCompletelyVisibleItem: Int = RecyclerView.NO_POSITION
    private var previousParentHeight = Float.MIN_VALUE
    private var previousScrollX = Int.MIN_VALUE

    fun onDrawOver(canvas: Canvas, parent: ContentCarouselLayout, scrollX: Int) {
        actualCompletelyVisibleItem = parent.findFirstCompletelyVisible()

        if(actualCompletelyVisibleItem != RecyclerView.NO_POSITION)
            lastCompletelyVisibleItem = actualCompletelyVisibleItem

        numberOfIndicators = parent.childCount

        if(numberOfIndicators < 2) {
            parent.setPadding(parent.paddingLeft, parent.paddingTop, parent.paddingRight, 0)
            return
        } else if(parent.paddingBottom != spacingToDrawIndicators) {
            parent.setPadding(
                parent.paddingLeft, parent.paddingTop, parent.paddingRight, spacingToDrawIndicators
            )
        }

        if(previousParentHeight != parent.height.toFloat()) saveStaticCalculations(parent)

        if(previousScrollX != scrollX) {
            previousScrollX = scrollX
            initialIndicatorPosX =
                ((Resources.getSystem().displayMetrics.widthPixels - indicatorTotalWidth) / 2f) + scrollX
        }

        drawIndicators(canvas, lastCompletelyVisibleItem)
    }

    private fun drawIndicators(canvas: Canvas, visibleItemPosition: Int) {
        var isThisPositionSelect = 0 == visibleItemPosition
        var nextXCoordinate = 0f

        indicators[0].first.initialPoint = initialIndicatorPosX
        canvas.drawRoundedRectangle(indicators[0], isThisPositionSelect)

        for(i in 1 until numberOfIndicators) {
            isThisPositionSelect = i == visibleItemPosition
            nextXCoordinate = indicators[i - 1].first.segmentSizePoint + spacingBetween
            indicators[i].first.initialPoint = nextXCoordinate
            canvas.drawRoundedRectangle(indicators[i], isThisPositionSelect)
        }
    }

    private fun saveStaticCalculations(parent: ContentCarouselLayout) {
        numberOfIndicators = parent.childCount

        indicatorTotalWidth = selectedWidth + (numberOfIndicators - 1) * noSelectedWidth +
                (spacingBetween * (numberOfIndicators - 1))

        indicatorPosY = parent.height.toFloat() - ySegmentSize
        bottom = indicatorPosY + ySegmentSize
        radius = (bottom - indicatorPosY) / 2
        for (i in 0 until numberOfIndicators)
            indicators.add(i, Pair(XSegment(0f, 0f), indicatorPosY))
        previousParentHeight = parent.height.toFloat()
    }

    private fun Canvas.drawRoundedRectangle(indicator: Pair<XSegment, Float>, isSelected: Boolean) {
        val right =
            if(isSelected) indicator.first.initialPoint + selectedWidth
            else indicator.first.initialPoint + noSelectedWidth
        indicator.first.segmentSizePoint = right

        drawRoundRect(
            indicator.first.initialPoint, indicator.second, right, bottom, radius, radius,
            if (isSelected) paintFillSelected else paintFillNoSelected
        )
    }

}