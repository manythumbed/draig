package draig.core.store.simple

import junit.framework.TestCase
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import draig.core.Version
import draig.core.entity.Itchy
import draig.core.entity.Scratchy
import draig.core.entity.TestEvent
import draig.core.store.TestIdentity

class SimpleEventStoreTest() : TestCase()    {

	fun testEmptyStreamForMissingEntity() {
		withStore { store ->
			assertNotNull(store.stream(TestIdentity(12))) { s ->
				assertEquals(Version(0), s.version)
				assertNull(s.payload)
			}
		}
	}

	fun testShouldStoreNewItemWithNoErrors() {
		withStore { store ->
			val result = store.store(TestIdentity(12), Version(0).withPayload(listOf(Itchy("m"), Scratchy("e"))))

			assertEquals(true, result.success)
			assertEquals(Version(2), result.version)
			assertEquals(0, result.errors.size())
		}
	}

	fun testShouldRetrieveStoredItem() {
		withStore { store ->
			val id = TestIdentity(12)
			store.store(id, Version(0).withPayload(listOf(Itchy("m"), Scratchy("e"))))

			assertNotNull(store.stream(id)) { stream ->
				assertEquals(Version(2), stream.version)
				assertEquals(2, stream.payload.size)
				assertEquals(Itchy("m"), stream.payload.get(0))
				assertEquals(Scratchy("e"), stream.payload.get(1))
			}
		}
	}

	fun testShouldProduceErrorResultForConcurrency() {
		withStore { store ->
			store.store(TestIdentity(12), Version(0), listOf(Itchy("m"), Scratchy("e")))
			val result = store.store(TestIdentity(12), Version(1), listOf(Itchy("m"), Scratchy("e")))

			assertEquals(false, result.success)
			assertEquals(Version(2), result.version)
			assertEquals(2, result.errors.size())
		}
	}

	fun testShouldStoreAdditionalEvents() {
		withStore { store ->
			store.store(TestIdentity(12), Version(0), listOf(Itchy("m"), Scratchy("e")))
			val result = store.store(TestIdentity(12), Version(2), listOf(Itchy("m"), Scratchy("e")))

			assertEquals(true, result.success)
			assertEquals(Version(4), result.version)
			assertEquals(0, result.errors.size())
		}
	}

	fun withStore(test: (SimpleEventStore<TestIdentity, TestEvent>) -> Unit) {
		val s = SimpleEventStore<TestIdentity, TestEvent>("test")
		test(s)
	}
}
