package draig.core.store

import draig.core.event.Event
import java.util.ArrayList

trait Subscriber  {
	fun receive(event: Event)
}

trait Publisher  {
	fun register(key: String, subscriber: Subscriber)
	fun publish(event: Event)
}

class SimplePublisher() : Publisher  {
	val subscribers = hashMapOf<String, ArrayList<Subscriber>>()

	override fun register(key: String, subscriber: Subscriber) {
		val list = subscribers.get(key)
		when {
			list != null -> {
				list.add(subscriber)
			}
			else -> {
				subscribers.put(key, arrayListOf(subscriber))
			}
		}
	}

	override fun publish(event: Event) {
		val list = subscribers.get(event.key)
		if(list != null)	{
			list.forEach { s ->
				s.receive(event)
			}
		}
	}

}
