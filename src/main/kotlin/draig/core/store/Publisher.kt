package draig.core.store

import draig.core.event.Event
import java.util.ArrayList

trait Subscriber  {
	fun receive(event: Event)
}

trait Publisher  {
	fun register(key: String, subscriber: Subscriber)
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

}
