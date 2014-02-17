package draig.core.store

import junit.framework.TestCase
import draig.core.event.Event
import draig.core.entity.Itchy
import draig.core.entity.Scratchy

class TestListener(var scratches: Int = 0) : Listener  {
	override fun receive(event: Event) {
		when(event)	{
			is Scratchy -> scratches++
		}
	}

	override fun canHandle(event: Event): Boolean {
		return when(event) {
			is Itchy	-> false
			is Scratchy -> true
			else -> false
		}
	}

}
class NotifierTest() : TestCase()  {

	fun testListener() {

	}
}