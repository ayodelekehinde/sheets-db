# <img src="https://kotlinlang.org/assets/images/favicon.svg" height="23"/> Sheets-DB
[![Production actions](https://github.com/Cherrio-LLC/sheets-db/actions/workflows/release.yml/badge.svg)](https://github.com/Cherrio-LLC/sheets-db/actions/workflows/release.yml)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.7.10-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.cherrio-llc/sheets-db?color=blue)](https://search.maven.org/search?q=g:io.github.cherrio-llc)


![badge-jvm](http://img.shields.io/badge/platform-jvm-DB413D.svg?style=flat)


A lightweight Kotlin multiplatform library that turns Google Sheets into a remote mini-database.

## Features
-  Basic database functions: CRUD
-  Type safe
-  Fast and light
- Multiplatform (soon)

## Adding to your project

Sheets-DB is published on Maven Central
```kotlin
repositories { 
  mavenCentral()
}
```

Include the dependency. Latest version [![Maven Central](https://img.shields.io/maven-central/v/io.github.cherrio-llc/sheets-db?color=blue)](https://search.maven.org/search?q=g:io.github.cherrio-llc)
```kotlin
implementation("io.github.cherrio-llc:sheets-db:<version>")
```

## Usage
Init Sheets DB
```kotlin
val sheetDb = SheetsDb {
    bearerToken = "your_token"
    sheetId = "google_sheet_id" //it is always embedded in the URL
}
```

Create a data class that will map to a sheet column names. This data class must be annotated with
`@Serializable` from the kotlinx.serialization library. You can map the Google sheet column names to 
the created data class, or you can provide `@SerialName()`
```kotlin
@Serializable
data class User(
    @SerialName("firstName")
    val name: String,
    val id: Int,
    val email: String?
)
```
> **NOTE:** The class name must match the sheet's name on the spreadsheet. In this example,
> the class `User` is a name of a sheet. Check the image below.
> Also your first column of a row should be an `id` for better queries.

![alt text](https://github.com/Cherrio-LLC/sheets-db/blob/master/art/spreadsheet.png?raw=true)

### Create a table
```kotlin
val table = sheetDb.getTable<User>()
```
This `getTable` function returns a `SheetTable<T>` that you will subsequently use for CRUD ops

### Create Read Update and Delete ops

### Read

Read your table

```kotlin
val table = sheetDb.get() //returns List<User>
```

Query your table
```kotlin
val result = table.find(User::id eq 1) //returns a List or empty list if no match
```

### Write
Add data to your table.
```kotlin
val user = User("Guy Merve", 11, "guy@email.com")
val result = table.create(user)
```

### Update
Update a specific row using its `id` you have to pass in the id.
You can pass in the only fields that needs updating. Here we don't want to update the email
```kotlin
val user = User("Capt Marvel", 5, null)
val result = table.create(user)
```



### RoadMap
There's a lot of updates to come.
1. Delete api
2. More platforms
3. E.t.c

## License

    Copyright 2022 Ayodele Kehinde

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.