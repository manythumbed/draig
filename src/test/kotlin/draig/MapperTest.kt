package draig

import junit.framework.TestCase
import kotlin.test.assertEquals
import com.google.gson.GsonBuilder
import com.google.gson.Gson
import draig.json.CompositeValueSerializer
import draig.json.SimpleValueSerializer
import draig.json.ListValueSerializer
import java.util.HashSet

class MapperTest() : TestCase() {
    val gson = gson()

    fun testAnswers()   {
        val a1 = CompositeValue(setOf("a" withValue SimpleValue("a")))
        val json1 = gson.toJson(a1)
        assertEquals("""{"a":"a"}""", json1)

        val a2 = CompositeValue(setOf("b" withValue ListValue(listOf(SimpleValue("1"), SimpleValue("2")))))
        val json2 = gson.toJson(a2)
        assertEquals("""{"b":["1","2"]}""", json2)
    }

    fun testSets()  {
        val a = setOf("a", "b")
        val b = setOf("c", "d")

        val c = HashSet<String>()

        c.addAll(a)
        c.addAll(b)

        println(c)
    }

    fun gson(): Gson    {
        val builder = GsonBuilder()
        builder.registerTypeAdapter(javaClass<CompositeValue>(), CompositeValueSerializer())
        builder.registerTypeAdapter(javaClass<SimpleValue>(), SimpleValueSerializer())
        builder.registerTypeAdapter(javaClass<ListValue>(), ListValueSerializer())

        val gson = builder.create()

        if(gson != null)    {
            return gson
        }

        return Gson()
    }
}
