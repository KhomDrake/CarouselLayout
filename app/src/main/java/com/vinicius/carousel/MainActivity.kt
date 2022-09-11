package com.vinicius.carousel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.AppCompatTextView
import kotlin.random.Random

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private var adapter = MyAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupAdd()
        setupRemove()
        setupMove()
        setupUpdate()
        setupChangeItems()

        findViewById<CarouselLayout>(R.id.carousel).apply {
            setConfigCarouselLayout(
                ConfigLayoutCarousel(percentageOfScreen = 1f)
            )
            setCarouselAdapter(adapter)
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
            val oldList = adapter.currentList
            val randomIndex = Random.nextInt(0, adapter.currentList.size)
            val item = generateItem()
            val currentList = oldList.mapIndexed { index, myObject ->
                MyObject(myObject.id, if(randomIndex == index) item.name else myObject.name)
            }
            adapter.submitList(currentList)
        }
    }

    private fun setAdapter(myObjectType: MyObjectType) {
        adapter.currentList
        findViewById<CarouselLayout>(R.id.carousel)?.apply {
//            val newAdapter = MyAdapter(myObjectType)
//            setCarouselAdapter(newAdapter)
//            newAdapter.submitList(adapter.currentList)
//            adapter = newAdapter
            adapter.type = myObjectType
            adapter.clearData()
        }
    }

    private fun setupRemove() {
        val remove = findViewById<AppCompatTextView>(R.id.remove)
        remove.setOnClickListener {
            val currentList = adapter.currentList.toMutableList()
            val randomIndex = Random.nextInt(0, currentList.size)
            currentList.removeAt(randomIndex)
            adapter.submitList(currentList)
        }
    }

    private fun setupMove() {
        val move = findViewById<AppCompatTextView>(R.id.move)
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

    private fun setupAdd() {
        val add = findViewById<AppCompatTextView>(R.id.add)
        add.setOnClickListener {
            val currentList = adapter.currentList.toMutableList()
            currentList.add(generateItem())
            adapter.submitList(currentList)
        }
    }

    private fun generateItem() : MyObject {
        val initialText = "generate"

        val quantity = Random.nextInt(2, 10)

        var text = "W ${Random.nextInt(0, 1000)} $quantity \n"

        for(i in 0 until quantity) {
            text += initialText
            if(i < quantity - 1) {
                text += "\n"
            }
        }

        return MyObject(Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE), text)
    }

    override fun onStart() {
        super.onStart()
        adapter.submitList(adapter.currentList)
    }
}