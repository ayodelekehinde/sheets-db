package com.cherrio.sheetsdb.client

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

val json = Json {
    ignoreUnknownKeys = true
}
fun client(engine: HttpClientEngine): HttpClient{
    return HttpClient(engine){
        install(ContentNegotiation){
            json(json)
        }
    }
}
val client = HttpClient{
    install(ContentNegotiation){
        json(json)
    }
}

const val BASE_URL = "https://sheets.googleapis.com/v4/spreadsheets"

val String.getSpreadSheetUrl: String get(){
    return "$BASE_URL/$this"
}

fun combineUrl(vararg params: String): String{
    return BASE_URL + "/" + params.joinToString("/")
}