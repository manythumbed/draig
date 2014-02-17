package draig.core.store

import draig.core.event.Event

trait Listener	{
	fun receive(event: Event)
	fun canHandle(event: Event): Boolean
}

trait Notifier	{
	fun register(listener: Listener)
}
