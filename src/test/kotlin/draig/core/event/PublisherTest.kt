package draig.core.event

import junit.framework.TestCase
import draig.core.entity.Itchy
import draig.core.entity.Scratchy
import draig.core.entity.EventKeys
import kotlin.test.assertEquals

class TestSubscriber(var scratches: Int = 0) : Subscriber  {
	override fun handles(): Set<EventKey> {
		return setOf(EventKeys.scratchy)
	}
	override fun receive(event: Event) {
		when(event) {
			is Scratchy -> scratches++
		}
	}
}

class PublisherTest() : TestCase()  {
	fun testListener() {
		val subscriber = TestSubscriber()
		val publisher = SimplePublisher(listOf(subscriber))

		publisher.publish(Itchy("1"))
		assertEquals(0, subscriber.scratches)

		publisher.publish(Scratchy("1"))
		assertEquals(1, subscriber.scratches)

		publisher.publish(Scratchy("1"))
		assertEquals(2, subscriber.scratches)
	}
}