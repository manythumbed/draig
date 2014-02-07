package draig.domain

import junit.framework.TestCase
import kotlin.test.assertEquals

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
    fun testEntity() {
        val t = TestEntity(listOf(Message("m1"), Error("e1")))

        t.storeMessage("m1")
        t.storeError("e1")

        assertEquals(2, t.count)
        assertEquals("m1", t.messages.get(0))
        assertEquals("*e1", t.messages.get(1))

        assertEquals(2, t.changes.size)
    }
}

class SimpleStoreTest() : TestCase()    {

    fun testEmptyStreamForMissingEntity() {
        withStore { store ->
            val stream = store.stream(TestIdentity(12))

            assertEquals(Version(0), stream.version)
            assertEquals(null, stream.events)
        }
    }

    fun testShouldStoreNewItemWithNoErrors() {
        withStore { store ->
            val result = store.store(TestIdentity(12), Version(0), listOf(Message("m"), Error("e")))

            assertEquals(true, result.success)
            assertEquals(Version(2), result.version)
            assertEquals(0, result.errors.size())
        }
    }

    fun testShouldProduceErrorResultForConcurrency() {
        withStore { store ->
            store.store(TestIdentity(12), Version(0), listOf(Message("m"), Error("e")))
            val result = store.store(TestIdentity(12), Version(1), listOf(Message("m"), Error("e")))

            assertEquals(false, result.success)
            assertEquals(Version(2), result.version)
            assertEquals(2, result.errors.size())
        }
    }

    fun testShouldStoreAdditionalEvents() {
        withStore { store ->
            store.store(TestIdentity(12), Version(0), listOf(Message("m"), Error("e")))
            val result = store.store(TestIdentity(12), Version(2), listOf(Message("m"), Error("e")))

            assertEquals(true, result.success)
            assertEquals(Version(4), result.version)
            assertEquals(0, result.errors.size())
        }
    }

    fun withStore(test: (SimpleStore<TestIdentity, TestEvent>) -> Unit) {
        val s = SimpleStore<TestIdentity, TestEvent>("test")
        test(s)
    }
}