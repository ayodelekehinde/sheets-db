/*
 * Copyright (c) 2022. Ayodele Kehinde
 */

package com.cherrio.sheetsdb.database

import com.cherrio.sheetsdb.client.BASE_URL
import com.cherrio.sheetsdb.client.client
import com.cherrio.sheetsdb.client.json
import com.cherrio.sheetsdb.init.SheetTableException
import com.cherrio.sheetsdb.models.AppendSheet
import com.cherrio.sheetsdb.utils.getToken
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject


suspend inline fun <reified T> SheetTable<T>.delete(value: T, sheetName: String? = null): Boolean{
    val jsonElement = json.encodeToJsonElement(value)
    val id = jsonElement.jsonObject["id"]?.parseId()
    val keys = getParamsCount<T>()
    return when (id) {
        0 -> {
            throw SheetTableException("ID cannot be 0. Has to be greater than 0")
        }
        null -> {
            throw SheetTableException("No ID found for update op. Make sure your table in google sheet has required table")
        }
        else -> {
            val notation = getNotation(id + 1, keys, sheetName ?: sheet)
            val response = tableOp {
                client.post("$BASE_URL/$sheetId/$SHEET_VALUES/$notation:clear"){
                    contentType(ContentType.Application.Json)
                    bearerAuth(getToken)
                }
            }
            response.status == HttpStatusCode.OK
        }
    }
}
@PublishedApi
internal inline fun <reified T> getParamsCount(): Int{
    val params = T::class.java.declaredFields.map { it.name }
    return params.size - 1
}