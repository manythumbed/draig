package draig.core.store

import junit.framework.TestCase
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VersionTest(): TestCase()	{
	fun testLater()	{
		assertFalse(Version(0).later(Version(0)))
		assertFalse(Version(0).later(Version(1)))
		assertTrue(Version(1).later(Version(0)))
	}
}
