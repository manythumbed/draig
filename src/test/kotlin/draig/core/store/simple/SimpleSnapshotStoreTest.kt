package draig.core.store.simple

import junit.framework.TestCase
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import draig.core.Version
import draig.core.Versioned
import draig.core.entity.Itchy
import draig.core.entity.Entity
import draig.core.entity.Scratchy
import draig.core.entity.TestEntity
import draig.core.entity.TestEvent
import draig.core.entity.ChangeListExclusionStrategy
import draig.core.store.SnapshotStore
import draig.core.store.TestIdentity

class TestSnapshotStore() : SimpleSnapshotStore<TestIdentity, TestEntity>() {

	private val gson = gson()

	override fun fromJson(json: String): TestEntity? {
		return gson.fromJson(json, javaClass<TestEntity>())
	}

	override fun toJson(entity: TestEntity): String? {
		return gson.toJson(entity)
	}

	private fun gson(): Gson {
		val builder = GsonBuilder()
		builder.setExclusionStrategies(ChangeListExclusionStrategy())
		val gson = builder.create()

		if (gson != null) {
			return gson
		}

		return Gson()
	}
}
class SimpleSnapshotStoreTest() : TestCase()  {

	fun testFetchNonExistentSnapshot() {
		withStore() { s ->
			assertNull(s.fetch(TestIdentity(1)))
		}
	}

	fun testSaveSnapshot() {
		withStore { s ->
			Versioned<TestEntity>(Version(1), TestEntity(listOf(Itchy("m"))))
			assertTrue(s.save(TestIdentity(1), Versioned<TestEntity>(Version(1), TestEntity(listOf(Itchy("m"))))))
		}
	}

	fun testFetchLatestSnapshot() {
		withStore { s ->
			val id = TestIdentity(1)
			val entity = TestEntity(listOf(Itchy("m"), Scratchy("e")))
			s.save(id, Versioned(Version(1), entity))

			assertNotNull(s.fetch(id)) { e ->
				assertEquals(Version(1), e.version)
				assertEquals(2, e.entity.count)
			}
		}
	}

	fun withStore(t: (SnapshotStore<TestIdentity, TestEntity>) -> Unit) {
		t(TestSnapshotStore())
	}
}