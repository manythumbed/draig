package draig.core.entity

import draig.core.event.Event

abstract class BetterEntity<E: Event, S>(events: List<E>)	{

	protected var state: S = events.fold(initialState()) { s, e ->
		handle(s, e)
	}

	protected fun apply(event: E)	{
		state = handle(state, event)
	}

	abstract protected fun handle(state: S, event: E): S
	abstract protected fun initialState(): S
}
