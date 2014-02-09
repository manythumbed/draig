package draig.core.store

import draig.core.entity.Entity
import draig.core.event.Event
import draig.core.Identity

abstract class SnapshotStore<I: Identity, T>()	{
	abstract fun fetch(id: I): Versioned<T>?
	abstract fun fetch(id: I, version: Version): Versioned<T>?
	abstract fun latest(id: I): Version?
	abstract fun save(id: I, entity: Versioned<T>) : Boolean
}

abstract class JsonSnapshotStore<I : Identity, T:Any>() : SnapshotStore<I, T>()  {
	abstract fun fromJson(json: String): T?
	abstract fun toJson(entity: T): String?
}
