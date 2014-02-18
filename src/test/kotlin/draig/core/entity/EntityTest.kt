package draig.core.entity

import draig.core.event.Event
import java.util.ArrayList
import junit.framework.TestCase
import kotlin.test.assertEquals

abstract class TestEvent(key: String) : Event(key)

data class Itchy(val message: String) : TestEvent(javaClass<Itchy>().getName())
data class Scratchy(val message: String) : TestEvent(javaClass<Itchy>().getName())

class TestEntity(events: List<TestEvent>) : Entity<TestEvent, ArrayList<String>>(events) {

	override fun handle(state: ArrayList<String>, event: TestEvent): ArrayList<String> {
		return when(event) {
			is Itchy -> {
				state.add("Itchy " + event.message)
				return state
			}
			is Scratchy -> {
				state.add("Scratchy " + event.message)
				return state
			}
			else -> state
		}
	}

	override fun initialState() = arrayListOf<String>()

	val count: Int
		get() = state.size

	fun storeItchy(message: String) {
		apply(Itchy(message))
	}

	fun storeScratchy(message: String) {
		apply(Scratchy("* " + message))
	}
}

class EntityTest() : TestCase()  {

	fun testConstruction() {
		val b = TestEntity(listOf(Itchy("1"), Scratchy("2")))

		assertEquals(2, b.count)
		assertEquals(0, b.changes.size)

		b.storeItchy("3")

		assertEquals(3, b.count)
		assertEquals(1, b.changes.size)
	}

	fun testUpdate() {
		val b = TestEntity(listOf(Itchy("1"), Scratchy("2")))

		assertEquals(2, b.count)
		assertEquals(0, b.changes.size)

		b.update(listOf(Itchy("3"), Scratchy("4")))
		assertEquals(4, b.count)
		assertEquals(0, b.changes.size)

		b.storeScratchy("5")
		assertEquals(5, b.count)
		assertEquals(1, b.changes.size)
	}
}