package draig.core.store

import draig.core.entity.Entity
import draig.core.event.Event
import draig.core.Identity

trait SnapshotStore<I : Identity, T : Entity<E>, E: Event>  {
	fun fetch(id: I): VersionedEntity<T>?
	fun fetch(id:I, version: Version): VersionedEntity<T>?
	fun latest(id: I): Version?
	fun save(id: I, entity: VersionedEntity<T>) : Boolean
}

abstract class JsonSnapshotStore<I : Identity, T : Entity<E>, E: Event>() : SnapshotStore<I, T, E>  {
	abstract fun fromJson(json: String): T?
	abstract fun toJson(entity: T): String?
}
