package draig.core.store.simple

import draig.core.Identity
import draig.core.event.Event
import draig.core.store.JsonSnapshotStore
import draig.core.store.Version
import draig.core.store.Versioned
import java.util.HashMap
import draig.core.entity.Entity

data class SnapshotKey<I : Identity>(val id: I, val version: Version)

abstract class SimpleSnapshotStore<I: Identity, T>(): JsonSnapshotStore<I, T>()	{
	private val latest = HashMap<I, Version>()
	private val snapshots = HashMap<SnapshotKey<I>, String>()

	override fun fetch(id: I): Versioned<T>? {
		val version = latest(id)
		if (version != null) {
			return fetch(id, version)
		}

		return null
	}
	override fun fetch(id: I, version: Version): Versioned<T>? {
		val snapshot = snapshots.get(SnapshotKey(id, version))
		if (snapshot != null) {
			val entity = fromJson(snapshot)
			if (entity != null) {
				return Versioned(version, entity)
			}
		}
		return null
	}
	override fun latest(id: I): Version? = latest.get(id)

	override fun save(id: I, entity: Versioned<T>): Boolean {
		val json = toJson(entity.entity)
		if (json != null) {
			snapshots.put(SnapshotKey(id, entity.version), json)
			val latestVersion = latest(id)
			if (latestVersion != null) {
				if (entity.version.later(latestVersion)) {
					latest.put(id, entity.version)
				}
			}
			else {
				latest.put(id, entity.version)
			}
			return true
		}

		return false
	}

}
