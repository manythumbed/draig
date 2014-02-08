package draig.core.store

import draig.core.entity.Entity
import draig.core.event.Event
import draig.core.Identity

trait SnapshotStore<I : Identity, T : Event>  {
	fun fetch(id: I): VersionedEntity<T>?
	fun fetch(id:I, version: Version): VersionedEntity<T>?
	fun latest(id: I): Version?
	fun save(id: I, entity: VersionedEntity<T>) : Boolean
}

abstract class JsonSnapshotStore<I : Identity, T : Event>() : SnapshotStore<I, T>  {
	abstract fun fromJson(json: String): Entity<T>?
	abstract fun toJson(entity: Entity<T>): String?
}
