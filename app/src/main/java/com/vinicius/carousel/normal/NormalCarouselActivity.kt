package com.vinicius.carousel.normal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.vinicius.carousel.CarouselLayout
import com.vinicius.carousel.ConfigLayoutCarousel
import com.vinicius.carousel.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class NormalCarouselActivity : AppCompatActivity(R.layout.activity_normal) {

    private var adapter = MyAdapter()
    private val viewModel: NormalViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupAdd()
        setupRemove()
        setupMove()
        setupUpdate()
        setupChangeItems()

        findViewById<CarouselLayout>(R.id.carousel_layout).apply {
            setConfigCarouselLayout(
                ConfigLayoutCarousel(percentageOfScreen = 1f)
            )
            setCarouselAdapter(adapter)
        }

        viewModel.data.observe(this) {
            adapter.submitList(it)
        }
    }

    private fun setupChangeItems() {
        val blue = findViewById<AppCompatTextView>(R.id.blue)
        blue.setOnClickListener {
            setAdapter(MyObjectType.BLUE)
        }

        val purple = findViewById<AppCompatTextView>(R.id.purple)
        purple.setOnClickListener {
            setAdapter(MyObjectType.PURPLE)
        }

        val red = findViewById<AppCompatTextView>(R.id.red)
        red.setOnClickListener {
            setAdapter(MyObjectType.RED)
        }
    }

    private fun setupUpdate() {
        val update = findViewById<AppCompatTextView>(R.id.update)
        update.setOnClickListener {
            viewModel.updateRandom()
        }
    }

    private fun setAdapter(myObjectType: MyObjectType) {
        adapter.currentList
        findViewById<CarouselLayout>(R.id.carousel_layout)?.apply {
            adapter.type = myObjectType
            adapter.clearData()
        }
    }

    private fun setupRemove() {
        val remove = findViewById<AppCompatTextView>(R.id.remove)
        remove.setOnClickListener {
            viewModel.removeRandom()
        }
    }

    private fun setupMove() {
        val move = findViewById<AppCompatTextView>(R.id.move)
        move.setOnClickListener {
            viewModel.moveRandom()
        }

        findViewById<CarouselLayout>(R.id.carousel_layout).apply {
            setCarouselAdapter(adapter)
        }
    }

    private fun setupAdd() {
        val add = findViewById<AppCompatTextView>(R.id.add)
        add.setOnClickListener {
            viewModel.add()
        }
    }
}