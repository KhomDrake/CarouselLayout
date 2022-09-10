package com.vinicius.carousel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.random.Random

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val adapter = MyAdapter()
        val add = findViewById<AppCompatTextView>(R.id.add)
        val remove = findViewById<AppCompatTextView>(R.id.remove)
        val move = findViewById<AppCompatTextView>(R.id.move)
        add.setOnClickListener {
            val currentList = adapter.currentList.toMutableList()
            currentList.add(MyObject(Random.nextInt(0, Int.MAX_VALUE), "W ${Random.nextInt(0, 1000)}    "))
            adapter.submitList(currentList)
        }

        remove.setOnClickListener {
            val currentList = adapter.currentList.toMutableList()
            val randomIndex = Random.nextInt(0, currentList.size)
            currentList.removeAt(randomIndex)
            adapter.submitList(currentList)
        }

        move.setOnClickListener {
            val currentList = adapter.currentList.toMutableList()
            val randomIndex = Random.nextInt(0, currentList.size)
            val item = currentList[randomIndex]
            val randomIndex2 = Random.nextInt(0, currentList.size)

            Log.i("Vini", "${item.name} OldIndex: $randomIndex, newIndex: $randomIndex2")
            currentList.remove(item)
            currentList.add(randomIndex2, item)
            adapter.submitList(currentList)
        }

        findViewById<CarouselLayout>(R.id.carousel).apply {
            setCarouselAdapter(adapter)
        }
    }
}