package draig.core.event

abstract class Event(val key: String)    {
	{
		require(key.isNotEmpty(), "An event must have a unique key")
	}
	open fun conflict(event: Event): Boolean {
		return true
	}
}
