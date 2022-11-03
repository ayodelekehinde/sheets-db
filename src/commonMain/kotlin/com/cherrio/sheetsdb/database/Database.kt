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
    var token: String
    val sheetId: String
    val sheet: String

    fun setBearerToken(token: String){
        this.token = token
    }
}
@PublishedApi
internal class SheetTableImpl<T>(
    override var token: String,
    override val sheet: String,
    override val sheetId: String): SheetTable<T>


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








