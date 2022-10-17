/*
 * Copyright (c) 2022. Ayodele Kehinde
 */

package com.cherrio.sheetsdb.database

import com.cherrio.sheetsdb.client.json
import com.cherrio.sheetsdb.init.SheetTableException
import kotlinx.serialization.json.*

@PublishedApi
internal const val SHEET_VALUES = "values"

@PublishedApi
internal fun List<String>.toJsonObject(keys: List<String>): JsonObject {
    val jsonObject = buildJsonObject {
        forEachIndexed { index, s ->
            put(keys[index], s)
        }
    }
    return jsonObject
}
@PublishedApi
internal inline fun <reified T> List<String>.toStrings(t: T): List<String?>{
    val jsonElement = json.encodeToJsonElement(t)
    val list = mutableListOf<String?>()
    forEach {
        val value = jsonElement.jsonObject[it]
        value?.let { v ->
            val cleaned = v.toString().removeQuotes
            list.add(cleaned)
        }
    }
    return list
}
@PublishedApi
internal val String.removeQuotes: String? get() = replace("\"","").ifBlankOrNull { null }

fun String.ifBlankOrNull(block: () -> String?): String? {
   return if (isEmpty() || this == "null") block() else this
}

@PublishedApi
internal fun JsonElement?.parseId() = try {
    toString().toInt()
}catch (e: NumberFormatException){
    throw SheetTableException("IDs have to be type Int")
}catch (e: NullPointerException){
    null
}