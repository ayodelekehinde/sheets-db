/*
 * Copyright (c) 2022. Ayodele Kehinde
 */

package com.cherrio.sheetsdb.database

import com.cherrio.sheetsdb.client.BASE_URL
import com.cherrio.sheetsdb.client.client
import com.cherrio.sheetsdb.client.combineUrl
import com.cherrio.sheetsdb.init.AuthorizationException
import com.cherrio.sheetsdb.init.SheetTableException
import com.cherrio.sheetsdb.models.AppendSheet
import com.cherrio.sheetsdb.models.Table
import com.cherrio.sheetsdb.utils.cache
import com.cherrio.sheetsdb.utils.getCached
import com.cherrio.sheetsdb.utils.getToken
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.StringJoiner

/**
 * Inserts a row into the spreadsheet, returns boolean if successful
 * @throws [SheetTableException], [AuthorizationException]
 * @param [T]
 * @receiver [SheetTable]
 * @return [Boolean]
 */

suspend inline fun <reified T> SheetTable<T>.create(value: T, sheetName: String? = null): Boolean{
    val sheetToSave = sheetName?: sheet
    val columnName = getColumnNameFromCacheOrRemote(sheetToSave, sheetId, getToken)
    val body = columnName.toStrings(value)
    val appendSheet = AppendSheet(values = listOf(body))
    val response = tableOp {
        client.post("$BASE_URL/$sheetId/$SHEET_VALUES/$sheetToSave:append"){
            contentType(ContentType.Application.Json)
            url {
                parameters.append("valueInputOption", "RAW")
            }
            bearerAuth(getToken)
            setBody(appendSheet)
        }
    }
    return response.status == HttpStatusCode.OK
}

suspend fun getColumnNameFromCacheOrRemote(
    sheetName: String,
    sheetId: String,
    token: String
): List<String>{
    val columnNames = getCached(sheetName)
    return columnNames?.split(",")?.toList() ?: getColumnNamesFromNetwork(sheetName, sheetId, token)
}

suspend fun getColumnNamesFromNetwork(
    sheetName: String,
    sheetId: String,
    token: String
): List<String>{
    val response = client.get(combineUrl(sheetId, SHEET_VALUES, sheetName)) {
        bearerAuth(token)
    }.body<Table>().values.first()
    cache(sheetName, response.joinToString(","))
    return response
}