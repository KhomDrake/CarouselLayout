package com.vinicius.carousel.collapse

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import br.com.arch.toolkit.delegate.viewProvider
import com.vinicius.carousel.CarouselLayout
import com.vinicius.carousel.CollapseHelper
import com.vinicius.carousel.CollapseView
import com.vinicius.carousel.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class CollapseCarouselActivity : AppCompatActivity(R.layout.activity_collapse) {

    private val carouselLayout: CarouselLayout by viewProvider(R.id.carousel_layout)
    private val collapse: AppCompatButton by viewProvider(R.id.collapse)
    private val add: AppCompatButton by viewProvider(R.id.add)
    private val remove: AppCompatButton by viewProvider(R.id.remove)
    private val adapter = CollapseAdapter()
    private val viewModel: CharacterViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("Collapsed", Context.MODE_PRIVATE)

        viewModel.setSharedPreferences(sharedPref)

        updatedCollapsedText()
        carouselLayout.setCollapseHelper(
            CollapseHelper(this, .5f, 2f, R.dimen.min_height)
        )
        carouselLayout.setCarouselAdapter(adapter)
        carouselLayout.setCollapse(viewModel.collapse)
        viewModel.data.observe(this) {
            adapter.submitList(it)
        }
        add.setOnClickListener {
            viewModel.addRandom()
        }
        remove.setOnClickListener {
            viewModel.removeRandom()
        }
        collapse.setOnClickListener {
            viewModel.changeCollapse()
            updatedCollapsedText()
            carouselLayout.setCollapse(viewModel.collapse)
            carouselLayout.collapse()
        }
    }

    private fun updatedCollapsedText() {
        collapse.text = if(viewModel.collapse) "Ver Mais" else "Ver Menos"
    }
}