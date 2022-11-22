package com.vinicius.carousel.collapse

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.random.Random

private val characters : MutableList<Character> = mutableListOf(
    Character(1, "Harry James Potter", "Harry James Potter é um personagem fictício protagonista da série homônima de livros da autora britânica J. K. Rowling. Na tradução brasileira, recebeu o nome completo de Harry Tiago Potter."),
    Character(2, "Severus Snape", "Severus Snape é um personagem fictício da série Harry Potter, da autora J. K. Rowling. Na tradução brasileira recebeu o nome de Severo Snape. Ele é caracterizado como uma pessoa de grande complexidade, cujo exterior controlado e friamente sarcástico oculta angústia e emoções profundas."),
    Character(3, "Lord Voldermort", "Traduzido do inglês-Lord Voldemort é um apelido para Tom Servolo Riddle, um personagem e o principal antagonista da série de livros de Harry Potter de J. K. Rowling"),
    Character(4, "Hermione Jean Granger", "Hermione Jean Granger é uma personagem fictícia na série Harry Potter de J. K. Rowling. Aparece pela primeira vez em Harry Potter e a Pedra Filosofal, como uma nova estudante em direção a Hogwarts."),
    Character(5, "Draco Lúcio Malfoy", "Draco Lúcio Malfoy é um personagem da saga Harry Potter de J. K. Rowling. Ele é estudante do mesmo ano de Harry Potter na casa da Sonserina. Ele está frequentemente acompanhado de seus dois colegas, Vincent Crabbe e Gregory Goyle, que atuam como capangas."),
    Character(6, "Dobby", "Em Harry Potter, Dobby é um elfo doméstico que é liberto da servidão ao acidentalmente ganhar uma meia de presente de seu antigo dono, Lucius Malfoy. Na maioria das pedras que estão na praia britânica, os fãs escrevem a frase \"aqui jaz Dobby, um elfo livre\"."),
    Character(7, "Bellatrix Lestrange", "Bellatrix Lestrange é uma personagem fictícia da série de livros Harry Potter escrita por J. K. Rowling. Ela evoluiu de um personagem periférico sem nome em Harry Potter e o Cálice de Fogo para um grande antagonista em romances subsequentes"),
    Character(8, "Sirius Black", "Sirius Black é um personagem da série Harry Potter de J. K. Rowling. Sirius foi mencionado pela primeira vez brevemente em Harry Potter e a Pedra Filosofal como um bruxo que emprestou a Rubeus Hagrid uma moto voadora logo após Lord Voldemort matar Tiago e Lílian Potter."),
    Character(9, "Dolores Jane Umbridge", "Traduzido do inglês-Dolores Jane Umbridge é uma personagem fictícia da série Harry Potter criada por J.K. Rowling. Umbridge é a principal antagonista do quinto romance de Harry Potter e a Ordem da Fênix e foi colocada em ."),
    Character(10, "Gerardo Grindelwald", "Gerardo Grindelwald, como ficou conhecido no Brasil, é um dos Bruxos mais poderosos da saga Harry Potter. Os feitos de Grindelwald já eram conhecidos, antes do Lord Voldemort, se tornar o antagonista do Universo de Harry Potter."),
    Character(11, "Albus Dumbledore", "Alvo Percival Wulfric Brian Dumbledore é um personagem fictício da série Harry Potter de J. K. Rowling. Na maior parte da série, ele é o diretor da escola de magia Hogwarts.")
)

private val selectedCharacters : MutableList<Character> = mutableListOf()

class CharacterViewModel : ViewModel() {

    val collapse: Boolean
        get() {
            return sharedPref?.getBoolean("Collapse", false) ?: false
        }
    private val _data = MutableLiveData<List<Character>>()

    private var sharedPref: SharedPreferences? = null

    val data: LiveData<List<Character>>
        get() = _data

    var i = 0

    fun resetCount() {
        i = 0
    }

    init {
        addRandom()
    }

    fun changeCollapse() {
        sharedPref?.edit()?.apply {
            this.putBoolean("Collapse", !collapse)
        }?.apply()
    }

    private fun getItem(characters: MutableList<Character>) = run {
        val pos = Random.nextInt(0, characters.size - 1)
        characters[pos]
    }

    private val dataToMutableList: MutableList<Character>
        get() = (_data.value ?: listOf()).toMutableList()

    fun addRandom(quantity: Int = 1) {
        val news = mutableListOf<Character>()
        for(i in 0 until quantity) {
            val newItem = getItem(characters)
            characters.remove(newItem)
            selectedCharacters.add(newItem)
            news.add(newItem)
        }
        val newList = dataToMutableList.apply {
            addAll(news)
        }
        _data.postValue(newList)
    }

    fun removeRandom() {
        val currentList = dataToMutableList.toMutableList()
        val newItem = getItem(selectedCharacters)
        selectedCharacters.remove(newItem)
        characters.add(newItem)
        currentList.remove(newItem)
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

    fun setSharedPreferences(sharedPref: SharedPreferences?) {
        this.sharedPref = sharedPref
    }

}