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


@Serializable
internal data class CreateSheet(
    val requests: List<Request>
)

@Serializable
internal data class Request(
    val addSheet: AddSheet
)
@Serializable
internal data class AddSheet(
    val properties: AddSheetProperties
)
@Serializable
internal data class AddSheetProperties(
    val sheetType: String = "GRID",
    val title: String
)

