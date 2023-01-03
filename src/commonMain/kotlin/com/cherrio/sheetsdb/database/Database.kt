/*
 * Copyright (c) 2022. Ayodele Kehinde
 */

package com.cherrio.sheetsdb.database

import com.cherrio.sheetsdb.init.AuthorizationException
import com.cherrio.sheetsdb.init.SheetTableException

import io.ktor.client.statement.*
import io.ktor.http.*


/**
 * Abstraction
 */
interface SheetTable<T>{
    val sheetId: String
    val sheet: String

}
@PublishedApi
internal class SheetTableImpl<T>(
    override val sheet: String,
    override val sheetId: String
): SheetTable<T>


/**
 * Extension functions on [SheetTable] to handle recurrent states
 * @return [HttpResponse]
 */
@PublishedApi
internal suspend fun <T> SheetTable<T>.tableOp(block: suspend () -> HttpResponse): HttpResponse{
    val response = block()
   return when(response.status){
       HttpStatusCode.OK -> {
           response
       }
       HttpStatusCode.BadRequest ->{
           throw SheetTableException("Table: $sheet not found")
       }
       HttpStatusCode.Forbidden ->{
           throw AuthorizationException("Bearer token has expired")
       }
       else -> {
           throw Exception(response.bodyAsText())
       }
    }
}








