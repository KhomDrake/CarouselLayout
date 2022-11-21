package com.vinicius.carousel.collapse

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import br.com.arch.toolkit.delegate.viewProvider
import com.vinicius.carousel.CarouselLayout
import com.vinicius.carousel.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class CollapseCarouselActivity : AppCompatActivity(R.layout.activity_collapse) {

    private val carouselLayout: CarouselLayout by viewProvider(R.id.carousel_layout)
    private val collapse: AppCompatButton by viewProvider(R.id.collapse)
    private val adapter = CollapseAdapter()
    private val viewModel: CharacterViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        carouselLayout.setCarouselAdapter(adapter)
        viewModel.data.observe(this) {
            adapter.submitList(it)
        }
        collapse.setOnClickListener {
            viewModel.changeCollapse()
            carouselLayout.setCollapse(viewModel.collapse)
        }
    }
}