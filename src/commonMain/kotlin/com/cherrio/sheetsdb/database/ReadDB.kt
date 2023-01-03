/*
 * Copyright (c) 2022. Ayodele Kehinde
 */

package com.cherrio.sheetsdb.database

import com.cherrio.sheetsdb.client.client
import com.cherrio.sheetsdb.client.combineUrl
import com.cherrio.sheetsdb.client.json
import com.cherrio.sheetsdb.models.Table
import com.cherrio.sheetsdb.utils.getToken
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.jsonPrimitive

/**
 * Gets the rows in the sheet without the column names. Returns a list
 * of type T
 * @receiver [SheetTable]
 * @return [List]
 */
suspend inline fun <reified T> SheetTable<T>.get(sheetName: String? = null): List<T> {
    val response = tableOp {
        client.get(combineUrl(sheetId, SHEET_VALUES, sheetName ?: sheet)) {
            bearerAuth(getToken)
        }
    }
    val data = response.body<Table>()
    return data.values.filter { it.isNotEmpty() }.mapResults()
}


/**
 * Searches the table for a match of the filter
 * E.g: find(User::email eq "John@gmail.com")
 * @receiver [SheetTable]
 * @param filter:[Filter]
 * @return [List]
 */
suspend inline fun <reified T> SheetTable<T>.find(filter: Filter, sheetName: String? = null): List<T> {
    val response = tableOp {
        client.get(combineUrl(sheetId, SHEET_VALUES, sheetName?: sheet)) {
            bearerAuth(getToken)
        }
    }
    val data = response.body<Table>().values.filter { it.isNotEmpty() }
    return data.find(filter.key, filter.query)
}

/**
 * Implementation that searches the keys for a particular value
 * @return [List]
 * @receiver [List]
 * @param key:[String]
 * @param value:[String]
 */
@PublishedApi
internal inline fun <reified T> List<List<String>>.find(key: String, value: String): List<T>{
    val keys = first()
    val jsonObjects =  removeColumnNames().map { it.toJsonObject(keys) }
    val result = jsonObjects.filter {
        val jsonKey = it[key]
        jsonKey != null && it.getValue(key).jsonPrimitive.content == value
    }
    val encodedValues = json.encodeToString(result)
    return json.decodeFromString(encodedValues)
}

/**
 *
 */

@PublishedApi
internal inline fun <reified T> List<List<String>>.mapResults(): List<T>{
    val keys = first()
    val jsonObjects =  removeColumnNames().map { it.toJsonObject(keys) }
    val encodedValues = json.encodeToString(jsonObjects)
    return json.decodeFromString(encodedValues)
}

@PublishedApi
internal fun <T> List<T>.removeColumnNames() =
    subList(1, size)


@PublishedApi
internal suspend fun <T> SheetTable<T>.getColumnNames(sheetName: String? = null): List<String> {
    val response = tableOp {
        client.get(combineUrl(sheetId, SHEET_VALUES, sheetName?:sheet)) {
            bearerAuth(getToken)
        }
    }
    return response.body<Table>().values.first()
}