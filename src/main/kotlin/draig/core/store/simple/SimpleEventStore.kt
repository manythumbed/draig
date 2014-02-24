package draig.core.store.simple

import java.util.HashMap
import draig.core.Identity
import draig.core.Version
import draig.core.event.Event
import draig.core.store.EventStore
import draig.core.store.StorageResult
import draig.core.store.StorageError
import draig.core.Versioned

class SimpleEventStore<I : Identity, T : Event>(val stream: String) : EventStore<I, T>   {
	val backingStore: HashMap<I, List<T>> = hashMapOf()

	override fun stream(id: I): Versioned<List<T>>? {
		val events = backingStore.get(id)
		if (events != null) {
			return Versioned(events.size(), events)
		}
		return null
	}

	override fun streamFrom(id: I, version: Version): Versioned<List<T>>? {
		val events = backingStore.get(id)
		if (events != null) {
			if (events.size() > version.version) return Versioned(events.size(), events.subList(version.version, events.size()))
		}
		return null
	}

	override fun store(id: I, events: Versioned<List<T>>): StorageResult {
		if (backingStore.containsKey(id)) {
			val stored = backingStore.get(id)
			if (stored != null) {
				if (events.payload.isEmpty()) {
					return StorageResult(false, Version(stored.size()))
				}

				if (events.version.version == stored.size()) {
					backingStore.put(id, stored.plus(events.payload))
					return StorageResult(true, Version(stored.size() + events.payload.size()))
				}

				return StorageResult(false, Version(stored.size()), events.payload.map { StorageError(it) })
			}
		}

		if (events.payload.isEmpty()) {
			return StorageResult(false, Version(0))
		}

		backingStore.put(id, events.payload)

		return StorageResult(true, Version(events.payload.size()), listOf())
	}
}



