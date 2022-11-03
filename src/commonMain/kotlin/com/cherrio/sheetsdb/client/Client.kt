package com.cherrio.sheetsdb.client

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

val json = Json {
    ignoreUnknownKeys = true
}
internal fun client(engine: HttpClientEngine): HttpClient{
    return HttpClient(engine){
        install(ContentNegotiation){
            json(json)
        }
    }
}
@PublishedApi
internal val client = HttpClient{
    install(ContentNegotiation){
        json(json)
    }
}

@PublishedApi
internal const val BASE_URL = "https://sheets.googleapis.com/v4/spreadsheets"

@PublishedApi
internal val String.getSpreadSheetUrl: String get(){
    return "$BASE_URL/$this"
}
@PublishedApi
internal fun combineUrl(vararg params: String): String{
    return BASE_URL + "/" + params.joinToString("/")
}