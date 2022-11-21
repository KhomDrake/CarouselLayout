package com.vinicius.carousel.collapse

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.random.Random

private val characters : List<Character> = listOf(
    Character(1, "Harry James Potter", "Harry James Potter é um personagem fictício protagonista da série homônima de livros da autora britânica J. K. Rowling. Na tradução brasileira, recebeu o nome completo de Harry Tiago Potter."),
    Character(2, "Severus Snape", "Severus Snape é um personagem fictício da série Harry Potter, da autora J. K. Rowling. Na tradução brasileira recebeu o nome de Severo Snape. Ele é caracterizado como uma pessoa de grande complexidade, cujo exterior controlado e friamente sarcástico oculta angústia e emoções profundas."),
    Character(3, "Lord Voldermort", "Traduzido do inglês-Lord Voldemort é um apelido para Tom Servolo Riddle, um personagem e o principal antagonista da série de livros de Harry Potter de J. K. Rowling"),
    Character(4, "Hermione Jean Granger", "Hermione Jean Granger é uma personagem fictícia na série Harry Potter de J. K. Rowling. Aparece pela primeira vez em Harry Potter e a Pedra Filosofal, como uma nova estudante em direção a Hogwarts.")
)

class CharacterViewModel : ViewModel() {

    var collapse = false
        private set
    private val _data = MutableLiveData<List<Character>>()

    val data: LiveData<List<Character>>
        get() = _data

    var i = 0

    fun resetCount() {
        i = 0
    }

    init {
        _data.postValue(characters)
    }

    fun changeCollapse() {
        collapse = !collapse
    }

    private fun getItem() = run {
        val a = characters[i]
        i += 1
        a
    }

    private val dataToMutableList: MutableList<Character>
        get() = (_data.value ?: listOf()).toMutableList()

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