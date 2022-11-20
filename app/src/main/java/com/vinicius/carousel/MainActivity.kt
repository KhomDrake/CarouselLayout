package com.vinicius.carousel

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import br.com.arch.toolkit.delegate.viewProvider
import com.vinicius.carousel.collapse.CollapseCarouselActivity
import com.vinicius.carousel.normal.NormalCarouselActivity

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val normal: AppCompatButton by viewProvider(R.id.normal)
    private val collapse: AppCompatButton by viewProvider(R.id.collapse)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        normal.setOnClickListener {
            startActivity(
                Intent(this, NormalCarouselActivity::class.java)
            )
        }

        collapse.setOnClickListener {
            startActivity(
                Intent(this, CollapseCarouselActivity::class.java)
            )
        }
    }
}