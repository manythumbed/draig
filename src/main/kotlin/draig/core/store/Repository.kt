package draig.core.store

import draig.core.entity.Entity
import draig.core.event.Event
import draig.core.Identity

data class VersionedEntity<T : Event>(val version: Version, val entity: Entity<T>)

abstract class Repository<I : Identity, T : Event, E : Entity<T>>(private val store: Store<I, T>)  {
	fun find(id: I): VersionedEntity<T>? {
		val stream = store.stream(id)
		if (stream.events != null) {
			return VersionedEntity(stream.version, build(stream.events))
		}
		return null
	}

	fun store(id: I, version: Version, entity: E): StorageResult {
		return store.store(id, version, entity.changes)
	}

	abstract fun build(events: List<T>): E
}
