package draig.core.store

import draig.core.event.Event
import draig.core.Identity
import draig.core.entity.Entity

data class Version(val version: Int)	{
	fun later(other: Version): Boolean = this.version > other.version
}

data class Stream<T>(val version: Version, val events: List<T>?)
data class StorageError(val event: Event)
data class StorageResult(val success: Boolean, val version: Version, val errors: List<StorageError>)

trait Store<I : Identity, T : Event> {
	fun stream(id: I): Stream<T>
	fun streamFrom(id: I, version: Version): Stream<T>
	fun store(id: I, version: Version, events: List<T>): StorageResult
}
