package draig.core

abstract class Identity()

data class Version(val version: Int)  {
	{
		require(version >= 0, "Version must be zero or postive")
	}

	fun later(other: Version): Boolean = this.version > other.version

	fun increment(value: Int): Version {
		require(value >= 0, "A version cannot be incremented by a negative value")
		return Version(version + value)
	}

	fun withPayload<T>(payload: T): Versioned<T> = Versioned(this.version, payload)
}

data class Versioned<T>(version: Int, val payload: T)  {
	val version = Version(version)
}

