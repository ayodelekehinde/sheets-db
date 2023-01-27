package com.cherrio.sheetsdb.init

import com.cherrio.sheetsdb.client.client
import com.cherrio.sheetsdb.client.getSpreadSheetUrl
import com.cherrio.sheetsdb.database.SheetTable
import com.cherrio.sheetsdb.database.SheetTableImpl
import com.cherrio.sheetsdb.models.SpreadSheet
import com.cherrio.sheetsdb.utils.cache
import com.cherrio.sheetsdb.utils.getToken
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * [SheetsDbBuilder.sheetId] is compulsory
 * while other init values are optional
 */
fun SheetsDb(block: SheetsDbBuilder.() -> Unit): SheetDb{
    val sheetsDbBuilder = SheetsDbBuilder().apply(block)
    sheetsDbBuilder.validate()
    return SheetsDBImpl(sheetsDbBuilder.sheetId)
}

private class SheetsDBImpl(override val sheetId: String): SheetDb{

    override suspend fun getSheetDetails(): SpreadSheet{
        val response = client.get(sheetId.getSpreadSheetUrl){
            bearerAuth(getToken)
        }
        println("Status: ${response.status}")
        if (response.status == HttpStatusCode.Unauthorized || response.status == HttpStatusCode.Forbidden){
            throw AuthorizationException("Bearer token has expired")
        }
        return response.body()
    }

}

interface SheetDb{
    val sheetId: String
    suspend fun getSheetDetails(): SpreadSheet
    fun setBearerToken(token: String){
        cache("bearerToken", token)
    }
}

inline fun <reified T> SheetDb.getTable(): SheetTable<T> {
    return  SheetTableImpl(T::class.simpleName!!, sheetId)
}

data class SheetsDbBuilder(
    var sheetId: String = ""
){
    fun validate(){
        when{
            sheetId.isEmpty() -> {
                throw InitializationException("Sheet-ID is required")
            }
        }
    }
}
data class RefreshToken(
    val token: String,
    val clientId: String,
    val clientSecret: String
)