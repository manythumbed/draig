package draig.json

import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import java.lang.reflect.Type
import com.google.gson.JsonSerializer
import draig.CompositeValue
import draig.SimpleValue
import com.google.gson.JsonPrimitive
import draig.ListValue
import com.google.gson.JsonArray

class CompositeValueSerializer() : JsonSerializer<CompositeValue> {
    override fun serialize(p0: CompositeValue?, p1: Type?, p2: JsonSerializationContext?): JsonElement? {
        if (p0 != null) {
            val jsonObject = JsonObject()

            p0.values.forEach {
                val jsonElement = p2?.serialize(it.value)
                if (jsonElement != null) {
                    jsonObject.add(it.label, jsonElement)
                }
            }

            return jsonObject
        }

        return JsonNull()
    }
}

class SimpleValueSerializer() : JsonSerializer<SimpleValue>  {
    override fun serialize(p0: SimpleValue?, p1: Type?, p2: JsonSerializationContext?): JsonElement? {
        if (p0 != null) {
            return JsonPrimitive(p0.value)
        }

        return JsonNull()
    }
}

class ListValueSerializer() : JsonSerializer<ListValue> {
    override fun serialize(p0: ListValue?, p1: Type?, p2: JsonSerializationContext?): JsonElement? {
        if (p0 != null) {
            val jsonArray = JsonArray()

            p0.values.forEach {
                jsonArray.add(p2?.serialize(it))
            }

            return jsonArray
        }

        return JsonNull()
    }

}