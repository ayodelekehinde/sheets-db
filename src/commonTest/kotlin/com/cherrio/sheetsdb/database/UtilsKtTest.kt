/*
 * Copyright (c) 2022. Ayodele Kehinde
 */

package com.cherrio.sheetsdb.database

import com.cherrio.sheetsdb.client.json
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class UtilsKtTest : FunSpec(){
    init {
        val list = listOf("Ayodele","Kehinde","Male")
        val keys = listOf("name","lastname","gender")
        test("toJsonObject returns json"){
            val jsonObj = list.toJsonObject(keys)
            val name = jsonObj.get("name")!!.jsonPrimitive.content
            name shouldBe  "Ayodele"
        }
        test("removeQuotes should return string"){
            val name = "\"John\"".removeQuotes
            name shouldBe "John"
        }
        test("ifBlankOrNull should return null"){
            val text = "".ifBlankOrNull { null }
            text shouldBe null
        }
        test("ifBlankOrNull should return text"){
            val text: String = "null"
            val result = text.ifBlankOrNull { "text" }
            result shouldBe "text"
        }
        test("parseId should return Int"){
            val jsonElement = json.parseToJsonElement("""{"id": 1 }""")
            val id = jsonElement.jsonObject["id"]?.parseId()
            id shouldBe 1
        }

    }
}
