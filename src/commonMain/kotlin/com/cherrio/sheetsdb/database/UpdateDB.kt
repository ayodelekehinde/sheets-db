/*
 * Copyright (c) 2022. Ayodele Kehinde
 */

package com.cherrio.sheetsdb.database

import com.cherrio.sheetsdb.client.BASE_URL
import com.cherrio.sheetsdb.client.client
import com.cherrio.sheetsdb.client.json
import com.cherrio.sheetsdb.init.SheetTableException
import com.cherrio.sheetsdb.models.AppendSheet
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

/**
 * update a value on the Sheet, expecting the body to have a field called id
 * @receiver [SheetTable]
 * @param value: [T]
 * @return [Boolean]
 */

suspend inline fun <reified T> SheetTable<T>.update(value: T): Boolean{
    val jsonElement = json.encodeToJsonElement(value)
    val id = jsonElement.jsonObject["id"]?.parseId()
    val keys = jsonElement.jsonObject.keys
    return when (id) {
        0 -> {
            throw SheetTableException("ID cannot be 0. Has to be greater than 0")
        }
        null -> {
            throw SheetTableException("No ID found for update op. Make sure your table in google sheet has required table")
        }
        else -> {
            val notation = getNotation(id + 1, keys.size, sheet)
            val columnName = getColumnNames()
            val body = columnName.toStrings(value)
            val appendSheet = AppendSheet(values = listOf(body))
            val response = tableOp {
                client.put("$BASE_URL/$sheetId/$SHEET_VALUES/$notation"){
                    contentType(ContentType.Application.Json)
                    url {
                        parameters.append("valueInputOption", "RAW")
                        parameters.append("includeValuesInResponse", "false")
                    }
                    bearerAuth(token)
                    setBody(appendSheet)
                }
            }
            response.status == HttpStatusCode.OK
        }
    }
}

/**
 * Returns A1 notation
 * @see: https://developers.google.com/sheets/api/guides/concepts#cell
 * @param id:[String]
 * @param rowSize:[Int]
 * @param sheet:[String]
 */
@PublishedApi
internal fun getNotation(id: Int, rowSize: Int, sheet: String): String{
    val alphabets = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val startAlphabet = "$sheet!A$id:"
    val endAlphabet = alphabets[rowSize].toString().plus(id)
    return startAlphabet + endAlphabet
}