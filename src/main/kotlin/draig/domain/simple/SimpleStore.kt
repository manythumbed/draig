package draig.domain.simple

import draig.domain.Identity
import draig.domain.Event
import java.util.HashMap
import draig.domain.Stream
import draig.domain.Version
import draig.domain.StorageResult
import draig.domain.Store
import draig.domain.StorageError

class SimpleStore<I : Identity, T : Event>(val stream: String) : Store<I, T>   {
	val backingStore: HashMap<I, List<T>> = hashMapOf()

	override fun stream(id: I): Stream<T> {
		val events = backingStore.get(id)
		if (events != null) {
			return Stream(Version(events.size()), events)
		}
		return Stream(Version(0), null)
	}

	override fun streamFrom(id: I, version: Version): Stream<T> {
		val events = backingStore.get(id)
		if (events != null) {
			if (events.size() > version.version) return Stream(Version(events.size()), events.subList(version.version, events.size()))
		}
		return Stream(Version(0), null)
	}

	override fun store(id: I, version: Version, events: List<T>): StorageResult {
		if (backingStore.containsKey(id)) {
			val list = backingStore.get(id)
			if (list != null) {
				if (version.version == list.size()) {
					backingStore.put(id, list.plus(events))
					return StorageResult(true, Version(list.size() + events.size()), listOf())
				}

				return StorageResult(false, Version(list.size()), events.map { StorageError(it) })
			}
		}
		backingStore.put(id, events)

		return StorageResult(true, Version(events.size()), listOf())
	}
}



