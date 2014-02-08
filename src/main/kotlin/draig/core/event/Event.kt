package draig.core.event

abstract class Event    {
	open fun conflict(event: Event): Boolean {
		return true
	}
}
