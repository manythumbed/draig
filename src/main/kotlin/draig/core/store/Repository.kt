package draig.core.store

import draig.core.entity.Entity
import draig.core.event.Event
import draig.core.Identity

data class VersionedEntity<T : Entity<*>>(val version: Version, val entity: T)

abstract class Repository<I : Identity, E : Event, T : Entity<E>>(private val store: Store<I, E>)  {
	fun find(id: I): VersionedEntity<T>? {
		val stream = store.stream(id)
		if (stream.events != null) {
			return VersionedEntity(stream.version, build(stream.events))
		}
		return null
	}

	fun store(id: I, version: Version, entity: T): StorageResult {
		return store.store(id, version, entity.changes)
	}

	abstract fun build(events: List<E>): T
}
