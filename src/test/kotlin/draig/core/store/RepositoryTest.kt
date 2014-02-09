package draig.core.store

import draig.core.entity.TestEntity
import draig.core.entity.TestEvent
import draig.core.Identity
import java.util.ArrayList

data class TestIdentity(val identity:Int): Identity()

class TestRepository(store: Store<TestIdentity, TestEvent>) : Repository<TestIdentity, TestEvent, TestEntity>(store) {
	override fun build(events: List<TestEvent>): TestEntity {
		return TestEntity(events)
	}

	override fun extract(entity: TestEntity): List<TestEvent> = entity.changes
}