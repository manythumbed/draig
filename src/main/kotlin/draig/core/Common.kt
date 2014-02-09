package draig.core

abstract class Identity()

data class Version(val version: Int)	{
	fun later(other: Version): Boolean = this.version > other.version
}

data class Versioned<T>(val version: Version, val entity: T)

