package draig.core.simple

import junit.framework.TestCase
import kotlin.test.assertEquals
import draig.core.entity.TestIdentity
import draig.core.entity.Message
import draig.core.entity.Error
import draig.core.entity.TestEvent
import draig.core.store.Version
import kotlin.test.assertNotNull

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

	fun testShouldRetrieveStoredItem() {
		withStore { store ->
			val id = TestIdentity(12)
			store.store(id, Version(0), listOf(Message("m"), Error("e")))

			val stream = store.stream(id)
			assertEquals(Version(2), stream.version)
			assertNotNull(stream.events)	{ events ->
				assertEquals(2, events.size)
				assertEquals(Message("m"), events.get(0))
				assertEquals(Error("e"), events.get(1))
			}
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
