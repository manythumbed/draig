package draig.core.simple

import junit.framework.TestCase
import kotlin.test.assertEquals
import draig.core.entity.TestIdentity
import draig.core.entity.Message
import draig.core.entity.Error
import draig.core.entity.TestEvent
import draig.core.store.Version

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
