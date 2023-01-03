/*
 * Copyright (c) 2023. Ayodele Kehinde
 */

package com.cherrio.sheetsdb.utils

import java.util.concurrent.ConcurrentHashMap

//Todo: replace with a pure Kotlin code
private val cache: MutableMap<String, String> = ConcurrentHashMap()

fun cache(key: String, value: String) {
    cache[key] = value
}

fun getCached(key: String): String? {
    return cache[key]
}

val getToken: String get() = getCached("bearerToken") ?: ""
