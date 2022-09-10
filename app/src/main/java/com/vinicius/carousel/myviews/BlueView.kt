package com.vinicius.carousel.myviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import com.vinicius.carousel.MyObject
import com.vinicius.carousel.R

class BlueView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), ViewBinder {

    init {
        LayoutInflater.from(context).inflate(R.layout.blue_item_layout, this)
        layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )
        setBackgroundResource(R.drawable.background_blue)
        outlineProvider = ViewOutlineProvider.BACKGROUND
        clipToOutline = true
    }

    override fun bind(data: MyObject) {
        findViewById<AppCompatTextView>(R.id.text).text = data.name
    }

}