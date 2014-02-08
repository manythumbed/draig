package draig.core.store

import draig.core.entity.TestEntity
import draig.core.entity.TestEvent
import draig.core.entity.TestIdentity


class TestRepository(store: Store<TestIdentity, TestEvent>) : Repository<TestIdentity, TestEvent, TestEntity>(store) {
	override fun build(events: List<TestEvent>): TestEntity {
		return TestEntity(events)
	}
}