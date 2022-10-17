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
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Inserts a row into the spreadsheet, returns boolean if successful
 * @throws [SheetTableException], [AuthorizationException]
 * @param [T]
 * @receiver [SheetTable]
 * @return [Boolean]
 */

suspend inline fun <reified T> SheetTable<T>.create(value: T): Boolean{
    val columnName = tableOp {
        client.get(combineUrl(sheetId, SHEET_VALUES, sheet)) {
            bearerAuth(token)
        }
    }.body<Table>().values.first()
    val body = columnName.toStrings(value)
    val appendSheet = AppendSheet(values = listOf(body))
    val response = tableOp {
        client.post("$BASE_URL/$sheetId/$SHEET_VALUES/$sheet:append"){
            contentType(ContentType.Application.Json)
            url {
                parameters.append("valueInputOption", "RAW")
            }
            bearerAuth(token)
            setBody(appendSheet)
        }
    }
    return response.status == HttpStatusCode.OK
}