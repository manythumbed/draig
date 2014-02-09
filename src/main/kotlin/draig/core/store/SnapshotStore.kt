package draig.core.store

import draig.core.Identity
import draig.core.Version
import draig.core.Versioned
import draig.core.entity.Entity
import draig.core.event.Event

trait SnapshotStore<I: Identity, T>	{
	fun fetch(id: I): Versioned<T>?
	fun fetch(id: I, version: Version): Versioned<T>?
	fun latest(id: I): Version?
	fun save(id: I, entity: Versioned<T>) : Boolean
}

abstract class JsonSnapshotStore<I : Identity, T:Any>() : SnapshotStore<I, T>  {
	abstract fun fromJson(json: String): T?
	abstract fun toJson(entity: T): String?
}
