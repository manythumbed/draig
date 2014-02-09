package draig.core.store

import draig.core.Identity
import draig.core.Version
import draig.core.entity.Entity
import draig.core.event.Event

data class Stream<T>(val version: Version, val contents: List<T>?)

data class StorageError(val event: Event)

data class StorageResult(val success: Boolean, val version: Version, val errors: List<StorageError> = listOf())

trait EventStore<I : Identity, E : Event> {
	fun stream(id: I): Stream<E>
	fun streamFrom(id: I, version: Version): Stream<E>
	fun store(id: I, version: Version, events: List<E>): StorageResult
}

