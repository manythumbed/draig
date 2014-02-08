package draig.core.entity

import draig.core.event.Event

abstract class BetterEntity<E : Event, S>(events: List<E>)  {

	protected var state: S = events.fold(initialState()) { s, e -> handle(s, e) }

	protected fun apply(event: E) {
		state = handle(state, event)
		changeList.add(event)
	}

	fun update(events: List<E>) {
		changeList = arrayListOf<E>()
		state = events.fold(state) { s, e -> handle(s, e) }
	}

	private var changeList = arrayListOf<E>()
	val changes: List<E>
		get() = changeList

	abstract protected fun handle(state: S, event: E): S
	abstract protected fun initialState(): S
}
