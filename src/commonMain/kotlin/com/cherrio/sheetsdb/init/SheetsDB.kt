package com.cherrio.sheetsdb.init

import com.cherrio.sheetsdb.client.client
import com.cherrio.sheetsdb.client.getSpreadSheetUrl
import com.cherrio.sheetsdb.database.SheetTable
import com.cherrio.sheetsdb.database.SheetTableImpl
import com.cherrio.sheetsdb.models.SpreadSheet
import io.ktor.client.call.*
import io.ktor.client.request.*

fun SheetsDb(block: SheetsDbBuilder.() -> Unit): SheetDb{
    val sheetsDbBuilder = SheetsDbBuilder().apply(block)
    sheetsDbBuilder.validate()
    return SheetsDBImpl(sheetsDbBuilder.bearerToken, sheetsDbBuilder.sheetId)
}

private class SheetsDBImpl(override var token: String, override val sheetId: String): SheetDb{

    override suspend fun getSheetDetails(): SpreadSheet{
        val response = client.get(sheetId.getSpreadSheetUrl){
            bearerAuth(token)
        }
        return response.body()
    }

}

interface SheetDb{
    var token: String
    val sheetId: String
    suspend fun getSheetDetails(): SpreadSheet
}

inline fun <reified T> SheetDb.getTable(): SheetTable<T> {
    return  SheetTableImpl(token, T::class.simpleName!!, sheetId)
}

data class SheetsDbBuilder(
    var sheetId: String = "",
    var bearerToken: String = "",
    var refreshToken: RefreshToken? = null
){
    fun validate(){
        when{
            sheetId.isEmpty() -> {
                throw InitializationException("Sheet-ID is required")
            }
            bearerToken.isEmpty() && refreshToken  == null ->{
                throw InitializationException("You need either a Bearer Token or a Refresh token")
            }
            bearerToken.isNotEmpty() && refreshToken != null ->{
                throw InitializationException("Pass either Bearer token or Refresh token, not both")
            }
        }
    }
}
data class RefreshToken(
    val token: String,
    val clientId: String,
    val clientSecret: String
)