/*
 * Copyright (c) 2022. Ayodele Kehinde
 */

package com.cherrio.sheetsdb.models

import kotlinx.serialization.Serializable

@Serializable
data class SpreadSheet(
    val spreadsheetId: String,
    val properties: Properties,
    val sheets: List<Sheets>

)

@Serializable
data class Properties(
    val title: String,
    val gridProperties: GridProperties? = null
)
@Serializable
data class GridProperties(
    val rowCount: Int,
    val columnCount: Int
)
@Serializable
data class Sheets(
    val properties: Properties
)
