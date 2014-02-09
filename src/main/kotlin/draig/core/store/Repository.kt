package draig.core.store

import draig.core.entity.Entity
import draig.core.event.Event
import draig.core.Identity

data class Versioned<T>(val version: Version, val entity: T)

abstract class Repository<I : Identity, E : Event, T>(private val store: Store<I, E>)  {
	fun find(id: I): Versioned<T>? {
		val stream = store.stream(id)
		if (stream.contents != null) {
			return Versioned(stream.version, build(stream.contents))
		}
		return null
	}

	fun store(id: I, version: Version, entity: T): StorageResult {
		return store.store(id, version, extract(entity))
	}

	abstract fun build(events: List<E>): T
	abstract fun extract(entity: T): List<E>
}