/*
 * Copyright (c) 2022. Ayodele Kehinde
 */

package com.cherrio.sheetsdb.models

import kotlinx.serialization.Serializable

@Serializable
data class Table(
    val values: List<List<String>>
)

@Serializable
data class AppendSheet(
    val majorDimension: String = "ROWS",
    val values: List<List<String?>>
)
