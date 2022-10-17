/*
 * Copyright (c) 2022. Ayodele Kehinde
 */

package com.cherrio.sheetsdb.init

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class SheetsDBKtTest : FunSpec(){
    init {
        test("SheetDb should throw error if sheetId is not added"){
            val exception = shouldThrow<InitializationException> {
                SheetsDb {  }
            }
            exception.message shouldBe "Sheet-ID is required"
        }
        test("SheetDb should throw error if bearerToken is not added"){
            val exception = shouldThrow<InitializationException> {
                SheetsDb {
                    sheetId = "Hello sheet"
                }
            }
            exception.message shouldBe "You need either a Bearer Token or a Refresh token"
        }
    }
}
