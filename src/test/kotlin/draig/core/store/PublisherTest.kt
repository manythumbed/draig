package draig.core.store

import junit.framework.TestCase
import draig.core.event.Event
import draig.core.entity.Itchy
import draig.core.entity.Scratchy

class TestSubscriber(var scratches: Int = 0) : Subscriber  {
	override fun receive(event: Event) {
		when(event) {
			is Scratchy -> scratches++
		}
	}
}

class PublisherTest() : TestCase()  {
	fun testListener() {
		val x = javaClass<Itchy>()
		println(x)
	}
}