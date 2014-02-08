package draig.core.store.simple

import junit.framework.TestCase
import draig.core.entity.TestEvent
import draig.core.entity.TestIdentity
import draig.core.entity.Entity
import draig.core.store.SnapshotStore
import kotlin.test.assertNull
import kotlin.test.assertTrue
import draig.core.store.VersionedEntity
import draig.core.store.Version
import draig.core.entity.TestEntity
import draig.core.entity.Message
import draig.core.entity.Warning
import kotlin.test.assertNotNull
import kotlin.test.assertEquals
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import draig.core.entity.ChangeListExclusionStrategy

class TestSnapshotStore() : SimpleSnapshotStore<TestIdentity, TestEntity, TestEvent>() {

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
			VersionedEntity<TestEntity>(Version(1), TestEntity(listOf(Message("m"))))
			assertTrue(s.save(TestIdentity(1), VersionedEntity<TestEntity>(Version(1), TestEntity(listOf(Message("m"))))))
		}
	}

	fun testFetchLatestSnapshot() {
		withStore { s ->
			val id = TestIdentity(1)
			val entity = TestEntity(listOf(Message("m"), Warning("e")))
			s.save(id, VersionedEntity(Version(1), entity))

			assertNotNull(s.fetch(id)) { e ->
				assertEquals(Version(1), e.version)
				assertEquals(2, e.entity.count)
			}
		}
	}

	fun withStore(t: (SnapshotStore<TestIdentity, TestEntity, TestEvent>) -> Unit) {
		t(TestSnapshotStore())
	}
}