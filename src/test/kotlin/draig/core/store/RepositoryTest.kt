package draig.core.store

import draig.core.entity.TestEntity
import draig.core.entity.TestEvent
import draig.core.Identity
import junit.framework.TestCase
import draig.core.store.simple.SimpleEventStore
import kotlin.test.assertNull
import draig.core.Version
import draig.core.entity.Itchy
import kotlin.test.assertEquals

data class TestIdentity(val identity: Int) : Identity()

class TestRepository(store: EventStore<TestIdentity, TestEvent>) : Repository<TestIdentity, TestEvent, TestEntity>(store) {
	override fun build(events: List<TestEvent>): TestEntity {
		return TestEntity(events)
	}

	override fun extract(entity: TestEntity): List<TestEvent> = entity.changes
}

class RepositoryTest() : TestCase()  {
	private val id = TestIdentity(1)
	private val entity = TestEntity(listOf(Itchy("1")))

	fun testFindNonExistent() {
		withRepository { r ->
			assertNull(r.find(id))
		}
	}

	fun testShouldNotStoreIfNoChanges() {
		withRepository { r ->
			val storageResult = r.store(id, Version(0), entity)
			assertEquals(false, storageResult.success)
			assertEquals(Version(0), storageResult.version)
			assertEquals(true, storageResult.errors.empty)
		}
	}

	private fun withRepository(t: (Repository<TestIdentity, TestEvent, TestEntity>) -> Unit) {
		t(TestRepository(SimpleEventStore("test")))
	}
}