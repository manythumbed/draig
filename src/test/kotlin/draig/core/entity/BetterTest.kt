package draig.core.entity

import draig.core.event.Event
import java.util.ArrayList
import junit.framework.TestCase
import kotlin.test.assertEquals

abstract class BetterEvent() : Event()

data class Wink(val message: String) : BetterEvent()
data class Wonk(val message: String) : BetterEvent()

class TestBetter(events: List<BetterEvent>) : BetterEntity<BetterEvent, ArrayList<String>>(events) {

	override fun handle(state: ArrayList<String>, event: BetterEvent): ArrayList<String> {
		return when(event) {
			is Wink -> {
				state.add("Wink" + event.message)
				return state
			}
			is Wonk -> {
				state.add("Wonk" + event.message)
				return state
			}
			else -> state
		}
	}

	override fun initialState() = arrayListOf<String>()

	val count: Int
		get() = state.size

	fun storeWink(message: String) {
		apply(Wink(message))
	}

	fun storeWonk(message: String) {
		apply(Wonk("*" + message))
	}
}

class BetterTest() : TestCase()  {

	fun testConstruction() {
		val b = TestBetter(listOf(Wink("1"), Wonk("2")))

		assertEquals(2, b.count)
		assertEquals(0, b.changes.size)

		b.storeWink("3")

		assertEquals(3, b.count)
		assertEquals(1, b.changes.size)
	}

	fun testUpdate() {
		val b = TestBetter(listOf(Wink("1"), Wonk("2")))

		assertEquals(2, b.count)
		assertEquals(0, b.changes.size)

		b.update(listOf(Wink("3"), Wonk("4")))
		assertEquals(4, b.count)
		assertEquals(0, b.changes.size)

		b.storeWonk("5")
		assertEquals(5, b.count)
		assertEquals(1, b.changes.size)
	}
}
