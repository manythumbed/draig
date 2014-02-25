package draig.core.store

import draig.core.Identity
import draig.core.Version
import draig.core.event.Event
import draig.core.Versioned

data class Key(val key: String)	{
	{
		require(key.isNotEmpty(), "A stream key cannot be empty")
	}
}

data class StorageError(val event: Event)

data class StorageResult(val success: Boolean, val version: Version, val errors: List<StorageError> = listOf())

trait EventStore<I : Identity, E : Event> {
	fun stream(id: I, key: Key): Versioned<List<E>>?
	fun streamFrom(id: I, version: Version, key: Key): Versioned<List<E>>?
	fun store(id: I, key: Key, events: Versioned<List<E>>): StorageResult
}

