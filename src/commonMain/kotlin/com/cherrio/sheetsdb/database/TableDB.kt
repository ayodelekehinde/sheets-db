/*
 * Copyright (c) 2022. Ayodele Kehinde
 */

package com.cherrio.sheetsdb.database

import com.cherrio.sheetsdb.client.*
import com.cherrio.sheetsdb.client.BASE_URL
import com.cherrio.sheetsdb.client.client
import com.cherrio.sheetsdb.client.getSpreadSheetUrl
import com.cherrio.sheetsdb.init.SheetTableException
import com.cherrio.sheetsdb.models.*
import com.cherrio.sheetsdb.utils.getToken
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

/**
 * Create a Table aka Sheet on the spreadsheet. Note that there's a performance overhead setting the
 * [withSerializer] to true
 * @param withSerializer [Boolean] defaults to false
 * @receiver [SheetTable]
 * @return [Boolean]
 */
suspend inline fun <reified T> SheetTable<T>.createTable(withSerializer: Boolean = false): Boolean{
    val sheetTab = if (withSerializer) getSheetAndColumnNamesSerializer<T>() else getSheetAndColumnNames<T>()
    val request = creatSheetRequest(sheetTab.tableName)
    val createSheet = creatSheet(sheetId, getToken, request)
    return if (createSheet){
        createColumns(sheetId, sheetTab, getToken)
    }else{
        createSheet
    }
}
@PublishedApi
internal suspend fun creatSheet(sheetId: String, token: String, request: CreateSheet): Boolean{
  val response =  client.post(sheetId.getSpreadSheetUrl.plus(":batchUpdate")){
        contentType(ContentType.Application.Json)
        bearerAuth(token)
        setBody(request)
    }
    if (response.status == HttpStatusCode.BadRequest){
        throw SheetTableException("A table with ${request.requests.get(0).addSheet.properties.title} exists")
    }
    return response.status == HttpStatusCode.OK
}
suspend fun createColumns(sheetId: String, sheetTab: SheetTab, token: String): Boolean{
    val appendSheet = AppendSheet(values = listOf(sheetTab.columnNames))
    val response = client.post("$BASE_URL/$sheetId/$SHEET_VALUES/${sheetTab.tableName}:append"){
        contentType(ContentType.Application.Json)
        url {
            parameters.append("valueInputOption", "RAW")
        }
        bearerAuth(token)
        setBody(appendSheet)
    }
    return response.status == HttpStatusCode.OK
}

@PublishedApi
internal fun creatSheetRequest(name: String): CreateSheet{
    return CreateSheet(listOf(Request(AddSheet(AddSheetProperties(title = name)))))
}

@PublishedApi
internal inline fun <reified T> getSheetAndColumnNames(): SheetTab{
    val tableName = T::class.java.simpleName
    val columnNames = T::class.java.declaredFields.map { it.name }.filter { it != "Companion" }
    if (!columnNames.contains("id"))
        throw SheetTableException("Table must include an \"id\" field")
    return SheetTab(tableName, columnNames.rearrange())
}
@PublishedApi
internal inline fun <reified T> getSheetAndColumnNamesSerializer(): SheetTab{
    val tableName = T::class.java.simpleName
    val newInstance = T::class.java.getDeclaredConstructor().newInstance()
    val columnNames = json.encodeToJsonElement(newInstance).jsonObject.keys.toList()
    if (!columnNames.contains("id"))
        throw SheetTableException("Table must include an \"id\" field")
    return SheetTab(tableName, columnNames.rearrange())
}

@PublishedApi
internal fun List<String>.rearrange(): List<String>{
    return if (get(0) == "id"){
        this
    }else{
        val list = filter { it != "id" }.toMutableList()
        list.add(0, "id")
        list.toList()
    }
}


data class SheetTab(
    val tableName: String,
    val columnNames: List<String>
)