package draig.core.event

import java.util.ArrayList
import java.util.HashMap

trait Subscriber  {
	fun receive(event: Event)
	fun handles(): Set<EventKey>
}

trait Publisher  {
	fun publish(event: Event)
}

class SimplePublisher(subscribers: List<Subscriber>) : Publisher  {

	val all = subscribers.fold(arrayListOf<Subscriber>()) { l, s ->
		registerForAllEvents(s, l)
	}

	private fun registerForAllEvents(subscriber: Subscriber, list: ArrayList<Subscriber>): ArrayList<Subscriber> {
		if (subscriber.handles().containsItem(AllEvents)) {
			list.add(subscriber)
		}

		return list
	}

	val subscribers = subscribers.fold(hashMapOf<EventKey, ArrayList<Subscriber>>()) { m, s ->
		register(s, m)
	}

	private fun register(subscriber: Subscriber, map: HashMap<EventKey, ArrayList<Subscriber>>): HashMap<EventKey, ArrayList<Subscriber>> {
		subscriber.handles().forEach { k ->
			val list = map.get(k)
			when {
				list != null -> list.add(subscriber)
				else -> map.put(k, arrayListOf(subscriber))
			}
		}

		return map
	}

	override fun publish(event: Event) {
		all.forEach { it.receive(event) }

		val list = subscribers.get(event.key)
		if (list != null) {
			list.forEach { it.receive(event) }
		}
	}

}
