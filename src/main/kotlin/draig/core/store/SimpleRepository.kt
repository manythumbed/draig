package draig.core.store

import draig.core.Identity
import draig.core.Version
import draig.core.Versioned
import draig.core.event.Event

trait Repository<I : Identity, T>  {
	fun find(id: I): Versioned<T>?
	fun store(id: I, version: Version, entity: T): StorageResult
}

abstract class SimpleRepository<I : Identity, E : Event, T>(private val store: EventStore<I, E>) : Repository<I, T>  {
	override fun find(id: I): Versioned<T>? {
		val stream = store.stream(id)
		if (stream.contents != null) {
			return Versioned(stream.version, build(stream.contents))
		}
		return null
	}

	override fun store(id: I, version: Version, entity: T): StorageResult {
		return store.store(id, version, extract(entity))
	}

	abstract fun build(events: List<E>): T
	abstract fun extract(entity: T): List<E>
}

abstract class SimpleRepositoryWithSnapshots<I : Identity, E : Event, T>(private val store: EventStore<I, E>, private val snapshots: SnapshotStore<I, T>) : SimpleRepository<I, E, T>(store)  {
	override fun find(id: I): Versioned<T>? {
		val snapshot = snapshots.fetch(id)
		if (snapshot != null) {

			val stream = store.streamFrom(id, snapshot.version)
			if (stream.contents != null) {
				return Versioned(Version(snapshot.version.version + stream.contents.size), buildFromSnapshot(snapshot.entity, stream.contents))
			}

			return Versioned(snapshot.version, snapshot.entity)
		}

		return super.find(id)
	}

	abstract fun buildFromSnapshot(snapshot: T, events: List<E>): T
}