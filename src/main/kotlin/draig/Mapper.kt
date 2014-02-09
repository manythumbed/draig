package draig

import java.util.HashSet

abstract class Value()

data class SimpleValue(val value: String) : Value()

data class ListValue(val values: List<Value>) : Value()

data class CompositeValue(val values: Set<LabelledValue>) : Value()

data class LabelledValue(val label: String, val value: Value)

fun String.withValue(value: Value): LabelledValue {
	return LabelledValue(this, value)
}

data class Mapping

data class Risk

//fun forward(values: CompositeValue, mapping: Mapping): Risk {
//	return Risk()
//}
//
//fun backward(risk: Risk, mapping: Mapping): CompositeValue {
//	return CompositeValue(setOf())
//}

trait Mapper{
	fun forward(values: CompositeValue): CompositeFact
	fun backward(facts: CompositeFact): CompositeValue
}

open class Forest<T>(val items: Set<T>)

fun add<T>(a: Forest<T>, b: Forest<T>): Forest<T> {
	val set = HashSet<T>()
	set.addAll(a.items)
	set.addAll(b.items)
	return Forest<T>(set)
}

open class Labelled<T>(val label: String, val value: T)

abstract class Fact

data class TextFact(val text: String) : Fact()

data class BooleanFact(val flag: Boolean) : Fact()

data class ListFact(val facts: List<Fact>) : Fact()

data class LabelledFact(label: String, value: Fact) : Labelled<Fact>(label, value)

data class CompositeFact(values: Set<Fact>) : Forest<Fact>(values)