package draig.domain

import java.util.HashMap

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

    private val changeList = arrayListOf<T>()
    val changes: List<Event>
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
                if(version.version == list.size())  {
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



