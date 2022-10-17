/*
 * Copyright (c) 2022. Ayodele Kehinde
 */

package com.cherrio.sheetsdb.database

import kotlin.reflect.KProperty1


data class Filter(val key: String, val query: String)

/**
 * Match a value with a parameter. Accepts [Any] but
 * will be resolved to a string
 * @receiver [KProperty1]
 * @param query:[Any]
 */
infix fun <T> KProperty1<T, *>.eq(query: Any): Filter {
    return Filter(toString().getClassParameter(), query.toString())
}

private fun String.getClassParameter(): String {
    return substringBefore(":").substringAfterLast(".")
}