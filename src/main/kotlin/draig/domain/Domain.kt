package draig.domain

import java.util.HashMap
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes

abstract class Event    {
	open fun conflict(event: Event): Boolean {
		return true
	}
}

abstract class Entity<T : Event>(events: List<T>)   {
	{
		initialise()
		events.forEach { handle(it) }
	}

	fun update(events: List<T>)	{
		changeList = arrayListOf<T>()
		events.forEach { handle(it) }
	}

	private var changeList = arrayListOf<T>()
	val changes: List<T>
		get() = changeList

	abstract protected fun initialise()

	abstract protected fun handle(event: T)

	protected fun apply(event: T) {
		handle(event)
		change(event)
	}

	private fun change(event: T) {
		changeList.add(event)
	}
}

class ChangeListExclusionStrategy(): ExclusionStrategy {
	override fun shouldSkipField(p0: FieldAttributes?): Boolean {
		if(p0 != null)	{
			return p0.getName().equals("changeList")
		}
		return false
	}
	override fun shouldSkipClass(p0: Class<out Any?>?): Boolean = false
}

abstract class Identity()
data class Version(val version: Int)
data class Stream<T>(val version: Version, val events: List<T>?)
data class StorageError(val event: Event)
data class StorageResult(val success: Boolean, val version: Version, val errors: List<StorageError>)

trait Store<I : Identity, T : Event> {
	fun stream(id: I): Stream<T>
	fun streamFrom(id: I, version: Version): Stream<T>
	fun store(id: I, version: Version, events: List<T>): StorageResult
}

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

data class VersionedEntity<T : Event>(val version: Version, val entity: Entity<T>)

abstract class Repository<I : Identity, T: Event, E : Entity<T>>(private val store: Store<I, T>)	{
	fun find(id: I): VersionedEntity<T>?	{
		val stream = store.stream(id)
		if(stream.events != null)	{
			return VersionedEntity(stream.version, build(stream.events))
		}
		return null
	}

	fun store(id: I, version: Version, entity: E)	{
		store.store(id, version, entity.changes)
	}

	abstract fun build(events: List<T>): E
}



