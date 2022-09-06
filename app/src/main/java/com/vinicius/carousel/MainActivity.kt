package com.vinicius.carousel

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.constraintlayout.helper.widget.Carousel
import com.google.android.material.card.MaterialCardView

class MainActivity : AppCompatActivity(R.layout.layout) {

    var colors = intArrayOf(
        Color.parseColor("#ffd54f"),
        Color.parseColor("#ffca28"),
        Color.parseColor("#ffc107"),
        Color.parseColor("#ffb300"),
        Color.parseColor("#ffa000"),
        Color.parseColor("#ff8f00"),
        Color.parseColor("#ff6f00"),
        Color.parseColor("#c43e00")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findViewById<Carousel>(R.id.carousel).apply {
            setAdapter(object : Carousel.Adapter {
                override fun count(): Int {
                    return 5
                }

                override fun populate(view: View?, index: Int) {
                    if (view is MaterialCardView) {
                        view.setBackgroundColor(colors[index])
                    }
                }

                override fun onNewItem(index: Int) {
                    Log.i("Vini", "newItem $index")
                }

            })
        }
    }
}