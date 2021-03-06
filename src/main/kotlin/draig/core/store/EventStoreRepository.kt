package draig.core.store

import draig.core.Identity
import draig.core.Version
import draig.core.Versioned
import draig.core.event.Event

trait Repository<I : Identity, T>  {
	fun find(id: I): Versioned<T>?
	fun store(id: I, entity: Versioned<T>): StorageResult
}

abstract class EventStoreRepository<I : Identity, E : Event, T>(private val store: EventStore<I, E>, private val key: Key) : Repository<I, T>  {
	override fun find(id: I): Versioned<T>? {
		val stream = store.stream(id, key)
		if (stream != null) {
			return stream.version.withPayload(build(stream.payload))
		}

		return null
	}

	override fun store(id: I, entity:Versioned<T>): StorageResult {
		return store.store(id, key, entity.version.withPayload(extract(entity.payload)))
	}

	abstract fun build(events: List<E>): T
	abstract fun extract(entity: T): List<E>
}

abstract class EventStoreRepositoryWithSnapshots<I : Identity, E : Event, T>(private val store: EventStore<I, E>, private val snapshots: SnapshotStore<I, T>, private val key: Key) : EventStoreRepository<I, E, T>(store, key)  {
	override fun find(id: I): Versioned<T>? {
		val snapshot = snapshots.fetch(id)
		when {
			snapshot != null -> {

				val stream = store.streamFrom(id, snapshot.version, key)
				if (stream != null) {
					return snapshot.version.increment(stream.payload.size()).withPayload(buildFromSnapshot(snapshot.payload, stream.payload))
				}

				return snapshot.version.withPayload(snapshot.payload)
			}
			else -> return super.find(id)
		}
	}

	abstract fun buildFromSnapshot(snapshot: T, events: List<E>): T
}