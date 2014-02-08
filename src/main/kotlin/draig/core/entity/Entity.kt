package draig.core.entity

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import draig.core.event.Event

abstract class Entity<T : Event>(events: List<T>)   {
	{
		initialise()
		events.forEach { handle(it) }
	}

	fun update(events: List<T>) {
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

class ChangeListExclusionStrategy() : ExclusionStrategy {
	override fun shouldSkipField(p0: FieldAttributes?): Boolean {
		if (p0 != null) {
			return p0.getName().equals("changeList")
		}
		return false
	}
	override fun shouldSkipClass(p0: Class<out Any?>?): Boolean = false
}
