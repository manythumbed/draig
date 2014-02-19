package draig.core.event

data class EventKey(val value:  String)	{
	{
		require(value.isNotEmpty(), "An event must have a unique key")
	}
}

val AllEvents = EventKey("ALL")

abstract class Event(val key: EventKey)    {
	open fun conflict(event: Event): Boolean {
		return true
	}
}
