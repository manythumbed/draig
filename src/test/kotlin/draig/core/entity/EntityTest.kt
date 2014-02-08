package draig.core.entity

import draig.core.event.Event
import draig.core.Identity
import junit.framework.TestCase
import com.google.gson.Gson
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import com.google.gson.GsonBuilder

abstract class TestEvent() : Event()

data class Message(val message: String) : TestEvent()
data class Error(val message: String) : TestEvent()

data class TestIdentity(val identity: Long) : Identity()

class TestEntity(events: List<TestEvent>) : Entity<TestEvent>(events)    {

	override fun initialise() {
		this.messageList = arrayListOf<String>()
	}

	private var messageList = arrayListOf<String>()
	val messages: List<String>
		get() = messageList

	val count: Int
		get() = messageList.size()

	override fun handle(event: TestEvent) {
		when (event) {
			is Message -> messageList.add(event.message)
			is Error -> messageList.add("*" + event.message)
		}
	}

	fun storeMessage(message: String) {
		if (message.isNotEmpty()) {
			apply(Message(message))
		}
	}

	fun storeError(error: String) {
		if (error.isNotEmpty()) {
			apply(Error(error))
		}
	}
}

class EntityTest() : TestCase()  {
	val gson: Gson = gson()

	fun testEntity() {
		val t = TestEntity(listOf(Message("m1"), Error("e1")))

		t.storeMessage("m1")
		t.storeError("e1")

		assertEquals(2, t.count)
		assertEquals("m1", t.messages.get(0))
		assertEquals("*e1", t.messages.get(1))

		assertEquals(2, t.changes.size)
	}

	fun testSerialisation() {
		val t = TestEntity(listOf(Message("m1"), Error("e1")))
		t.storeMessage("m1")
		t.storeError("e1")

		val json = gson.toJson(t)
		assertEquals("""{"messageList":["m1","*e1"]}""", json)

		assertNotNull(gson.fromJson(json, javaClass<TestEntity>()), { t2 ->
			assertEquals(2, t2.count)
			assertEquals("m1", t2.messages.get(0))
			assertEquals("*e1", t2.messages.get(1))
			assertNull(t2.changes)
		})
	}

	fun testUpdateSnapshot() {
		assertNotNull(gson.fromJson("""{"messageList":["m1","*e1"]}""", javaClass<TestEntity>()), { t2 ->
			t2.update(listOf(Error("e1")))
			assertEquals(3, t2.count)
			assertEquals(0, t2.changes.size)

			t2.storeError("e1")
			t2.storeError("e1")

			assertEquals(5, t2.count)
			assertEquals(2, t2.changes.size)
		})
	}
}

fun gson(): Gson {
	val builder = GsonBuilder()
	builder.setExclusionStrategies(ChangeListExclusionStrategy())
	val gson = builder.create()

	if (gson != null) {
		return gson
	}

	return Gson()
}

