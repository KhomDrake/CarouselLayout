package com.vinicius.carousel

import androidx.recyclerview.widget.DiffUtil
import com.vinicius.carousel.UpdateCarousel
import com.vinicius.carousel.UpdateCarouselType
import org.junit.Assert
import org.junit.Test

class TestItem(val id: Int, val name: String)

class TestDiffUtil: DiffUtil.ItemCallback<TestItem>() {

    override fun areContentsTheSame(oldItem: TestItem, newItem: TestItem): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areItemsTheSame(oldItem: TestItem, newItem: TestItem): Boolean {
        return oldItem.id == newItem.id
    }

}

class UpdateCarouselTest {

    private val diffUtil = TestDiffUtil()

    private fun <T>assertEquals(result: T, expected: T) {
        Assert.assertEquals(expected, result)
    }

    @Test
    fun `when Adding One Item, should only have one update carousel Add`() {
        val oldList = listOf<TestItem>()
        val newList = listOf(TestItem(1, "abc"))

        val updates = com.vinicius.carousel.UpdateCarousel.calculateUpdate(diffUtil, oldList, newList)

        val expectedList = listOf(
            com.vinicius.carousel.UpdateCarousel(
                0,
                com.vinicius.carousel.UpdateCarouselType.ADD,
                -1
            )
        )

        assertEquals(updates, expectedList)
    }

    @Test
    fun `when Adding Three Items, should only have three update carousel Add`() {
        val oldList = listOf<TestItem>()
        val newList = listOf(TestItem(1, "abc"), TestItem(2, "abc"), TestItem(3, "abc"))

        val updates = com.vinicius.carousel.UpdateCarousel.calculateUpdate(diffUtil, oldList, newList)

        val expectedList = listOf(
            com.vinicius.carousel.UpdateCarousel(
                0,
                com.vinicius.carousel.UpdateCarouselType.ADD,
                -1
            ),
            com.vinicius.carousel.UpdateCarousel(
                1,
                com.vinicius.carousel.UpdateCarouselType.ADD,
                -1
            ),
            com.vinicius.carousel.UpdateCarousel(
                2,
                com.vinicius.carousel.UpdateCarouselType.ADD,
                -1
            )
        )

        assertEquals(updates, expectedList)
    }

    @Test
    fun `when already has one and remove one item, should only have Remove`() {
        val oldList = listOf(TestItem(1, "abc"))
        val newList = listOf<TestItem>()

        val updates = com.vinicius.carousel.UpdateCarousel.calculateUpdate(diffUtil, oldList, newList)

        val expectedList = listOf(
            com.vinicius.carousel.UpdateCarousel(
                0,
                com.vinicius.carousel.UpdateCarouselType.REMOVE,
                0
            )
        )

        assertEquals(updates, expectedList)
    }

    @Test
    fun `when already has one item and add two items, should only have Add`() {
        val oldList = listOf(TestItem(1, "abc"))
        val newList = listOf(TestItem(1, "abc"), TestItem(2, "abc"), TestItem(3, "abc"))

        val updates = com.vinicius.carousel.UpdateCarousel.calculateUpdate(diffUtil, oldList, newList)

        val expectedList = listOf(
            com.vinicius.carousel.UpdateCarousel(
                1,
                com.vinicius.carousel.UpdateCarouselType.ADD,
                -1
            ),
            com.vinicius.carousel.UpdateCarousel(
                2,
                com.vinicius.carousel.UpdateCarouselType.ADD,
                -1
            )
        )

        assertEquals(updates, expectedList)
    }

    @Test
    fun `when already has two items and remove one, should only have Remove`() {
        val oldList = listOf(TestItem(1, "abc"), TestItem(2, "abc"))
        val newList = listOf(TestItem(2, "abc"))

        val updates = com.vinicius.carousel.UpdateCarousel.calculateUpdate(diffUtil, oldList, newList)

        val expectedList = listOf(
            com.vinicius.carousel.UpdateCarousel(
                0,
                com.vinicius.carousel.UpdateCarouselType.REMOVE,
                0
            )
        )

        assertEquals(updates, expectedList)
    }

    @Test
    fun `when has three items and first and last change positions, should only have Move`() {
        val oldList = listOf(TestItem(1, "abc"), TestItem(2, "abc"), TestItem(3, "abc"))
        val newList = listOf(TestItem(3, "abc"), TestItem(2, "abc"), TestItem(1, "abc"))

        val updates = com.vinicius.carousel.UpdateCarousel.calculateUpdate(diffUtil, oldList, newList)

        val expectedList = listOf(
            com.vinicius.carousel.UpdateCarousel(
                0,
                com.vinicius.carousel.UpdateCarouselType.MOVE,
                2
            ),
            com.vinicius.carousel.UpdateCarousel(
                2,
                com.vinicius.carousel.UpdateCarouselType.MOVE,
                0
            )
        )

        assertEquals(updates, expectedList)
    }

    @Test
    fun `when has two items and first item change content, should only have Update`() {
        val oldList = listOf(TestItem(1, "abc"), TestItem(2, "abc"))
        val newList = listOf(TestItem(1, "abce"), TestItem(2, "abc"))

        val updates = com.vinicius.carousel.UpdateCarousel.calculateUpdate(diffUtil, oldList, newList)

        val expectedList = listOf(
            com.vinicius.carousel.UpdateCarousel(
                0,
                com.vinicius.carousel.UpdateCarouselType.UPDATE,
                -1
            )
        )

        assertEquals(updates, expectedList)
    }

    @Test
    fun `when has three items, then first and last change positions and second is removed, should have Move and Remove`() {
        val oldList = listOf(TestItem(1, "abc"), TestItem(2, "abc"), TestItem(3, "abc"))
        val newList = listOf(TestItem(3, "abc"), TestItem(1, "abc"))

        val updates = com.vinicius.carousel.UpdateCarousel.calculateUpdate(diffUtil, oldList, newList)

        val expectedList = listOf(
            com.vinicius.carousel.UpdateCarousel(
                0,
                com.vinicius.carousel.UpdateCarouselType.MOVE,
                2
            ),
            com.vinicius.carousel.UpdateCarousel(
                1,
                com.vinicius.carousel.UpdateCarouselType.MOVE,
                0
            ),
            com.vinicius.carousel.UpdateCarousel(
                1,
                com.vinicius.carousel.UpdateCarouselType.REMOVE,
                1
            )
        )

        assertEquals(updates, expectedList)
    }

    @Test
    fun `when has two items, then first and last change positions and one is added, should have Move and Add`() {
        val oldList = listOf(TestItem(3, "abc"), TestItem(1, "abc"))
        val newList = listOf(TestItem(1, "abc"), TestItem(2, "abc"), TestItem(3, "abc"))

        val updates = com.vinicius.carousel.UpdateCarousel.calculateUpdate(diffUtil, oldList, newList)

        val expectedList = listOf(
            com.vinicius.carousel.UpdateCarousel(
                0,
                com.vinicius.carousel.UpdateCarouselType.MOVE,
                1
            ),
            com.vinicius.carousel.UpdateCarousel(
                1,
                com.vinicius.carousel.UpdateCarouselType.ADD,
                -1
            ),
            com.vinicius.carousel.UpdateCarousel(
                2,
                com.vinicius.carousel.UpdateCarouselType.MOVE,
                0
            )
        )

        assertEquals(updates, expectedList)
    }

}