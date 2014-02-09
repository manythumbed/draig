package draig.core.entity

import draig.core.event.Event
import java.util.ArrayList
import junit.framework.TestCase
import kotlin.test.assertEquals

abstract class TestEvent() : Event()

data class Itchy(val message: String) : TestEvent()
data class Scratchy(val message: String) : TestEvent()

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

//import draig.core.event.Event
//import draig.core.Identity
//import junit.framework.TestCase
//import com.google.gson.Gson
//import kotlin.test.assertEquals
//import kotlin.test.assertNotNull
//import kotlin.test.assertNull
//import com.google.gson.GsonBuilder
//
//abstract class TestEvent() : Event()
//
//data class Message(val message: String) : TestEvent()
//data class Warning(val message: String) : TestEvent()
//
//data class TestIdentity(val identity: Long) : Identity()
//
//// Should this really fold over the entity state using a function (s:State, e: Event) -> s:State
//// then state can be declared as var state: S = events.fold(initialState) { s,e -> handle(s, e)
//// ?????????
//// If this worked it would also seperate state from behaviour!!
//class TestEntity(events: List<TestEvent>) : Entity<TestEvent>(events)    {
//	{
//		events.forEach { handle(it) }
//	}
//
//	private var messageList = arrayListOf<String>()
//
//	override fun initialise() {
//		this.messageList = arrayListOf<String>()
//	}
//
//	val messages: List<String>
//		get() = messageList
//
//	val count: Int
//		get() = messageList.size()
//
//	override fun handle(event: TestEvent) {
//		when (event) {
//			is Message -> messageList.add(event.message)
//			is Warning -> messageList.add("*" + event.message)
//		}
//	}
//
//	fun storeMessage(message: String) {
//		if (message.isNotEmpty()) {
//			apply(Message(message))
//		}
//	}
//
//	fun storeError(error: String) {
//		if (error.isNotEmpty()) {
//			apply(Warning(error))
//		}
//	}
//}
//
//class EntityTest() : TestCase()  {
//	val gson: Gson = gson()
//
//	fun testEntity() {
//		val t = TestEntity(listOf(Message("m1"), Warning("e1")))
//
//		assertEquals(2, t.count)
//
//		t.storeMessage("m1")
//		t.storeError("e1")
//
//		assertEquals(4, t.count)
//		assertEquals("m1", t.messages.get(0))
//		assertEquals("*e1", t.messages.get(1))
//		assertEquals("m1", t.messages.get(2))
//		assertEquals("*e1", t.messages.get(3))
//
//		assertEquals(2, t.changes.size)
//	}
//
//	fun testSerialisation() {
//		val t = TestEntity(listOf(Message("m1"), Warning("e1")))
//		t.storeMessage("m1")
//		t.storeError("e1")
//
//		val json = gson.toJson(t)
//		assertEquals("""{"messageList":["m1","*e1"]}""", json)
//
//		assertNotNull(gson.fromJson(json, javaClass<TestEntity>())) { t2 ->
//			assertEquals(2, t2.count)
//			assertEquals("m1", t2.messages.get(0))
//			assertEquals("*e1", t2.messages.get(1))
//			assertNull(t2.changes)
//		}
//	}
//
//	fun testUpdateSnapshot() {
//		assertNotNull(gson.fromJson("""{"messageList":["m1","*e1"]}""", javaClass<TestEntity>())) { t2 ->
//			t2.update(listOf(Warning("e1")))
//			assertEquals(3, t2.count)
//			assertEquals(0, t2.changes.size)
//
//			t2.storeError("e1")
//			t2.storeError("e1")
//
//			assertEquals(5, t2.count)
//			assertEquals(2, t2.changes.size)
//		}
//	}
//}
//
//fun gson(): Gson {
//	val builder = GsonBuilder()
//	builder.setExclusionStrategies(ChangeListExclusionStrategy())
//	val gson = builder.create()
//
//	if (gson != null) {
//		return gson
//	}
//
//	return Gson()
//}
//
