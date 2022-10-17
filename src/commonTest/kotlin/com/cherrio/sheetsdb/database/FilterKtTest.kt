/*
 * Copyright (c) 2022. Ayodele Kehinde
 */

package com.cherrio.sheetsdb.database

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FilterKtTest : FunSpec(){
    init {
        test("eq should return Filter class"){
            val filter = User::name eq "John"
            filter shouldBe Filter("name","John")
        }
    }


    data class User(val name: String)
}
