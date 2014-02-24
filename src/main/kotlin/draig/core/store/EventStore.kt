package draig.core.store

import draig.core.Identity
import draig.core.Version
import draig.core.event.Event
import draig.core.Versioned

data class Stream<T>(val version: Version, val contents: List<T>?)

data class StorageError(val event: Event)

data class StorageResult(val success: Boolean, val version: Version, val errors: List<StorageError> = listOf())

trait EventStore<I : Identity, E : Event> {
	fun stream(id: I): Versioned<List<E>>?
	fun streamFrom(id: I, version: Version): Versioned<List<E>>?
	fun store(id: I, events: Versioned<List<E>>): StorageResult
}

