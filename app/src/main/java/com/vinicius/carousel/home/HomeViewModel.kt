package com.vinicius.carousel.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.random.Random

class HomeViewModel : ViewModel() {

    private val _data = MutableLiveData<List<MyObject>>()

    val data: LiveData<List<MyObject>>
        get() = _data

    var i = 0

    fun resetCount() {
        i = 0
    }

    private fun getItem() = run {
        val a = generateItem(i)
        i += 2
        a
    }

    private fun generateItem(fixedQuantity: Int = -1) : MyObject {
        val initialText = "generate"

        val quantity = if(fixedQuantity == -1) Random.nextInt(2, 10) else fixedQuantity

        var text = "W ${Random.nextInt(0, 1000)} $quantity \n"

        for(i in 0 until quantity) {
            text += initialText
            if(i < quantity - 1) {
                text += "\n"
            }
        }

        return MyObject(Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE), text)
    }

    private val dataToMutableList: MutableList<MyObject>
        get() = (_data.value ?: listOf()).toMutableList()

    fun addRandom() {
        val newItem = generateItem()
        val newList = dataToMutableList.apply {
            add(newItem)
        }
        _data.postValue(newList)
    }

    fun add() {
        val newItem = getItem()
        val newList = dataToMutableList.apply {
            add(newItem)
        }
        _data.postValue(newList)
    }

    fun removeRandom() {
        val currentList = dataToMutableList.toMutableList()
        val randomIndex = Random.nextInt(0, currentList.size)
        currentList.removeAt(randomIndex)
        _data.postValue(currentList)
    }

    fun updateRandom() {
        val oldList = dataToMutableList
        val randomIndex = Random.nextInt(0, oldList.size)
        val item = generateItem()
        val currentList = oldList.mapIndexed { index, myObject ->
            MyObject(myObject.id, if(randomIndex == index) item.name else myObject.name)
        }
        _data.postValue(currentList)
    }

    fun moveRandom() {
        val currentList = dataToMutableList.toMutableList()
        val randomIndex = Random.nextInt(0, currentList.size)
        val item = currentList[randomIndex]
        val randomIndex2 = Random.nextInt(0, currentList.size)

        currentList.remove(item)
        currentList.add(randomIndex2, item)
        _data.postValue(currentList)
    }

}