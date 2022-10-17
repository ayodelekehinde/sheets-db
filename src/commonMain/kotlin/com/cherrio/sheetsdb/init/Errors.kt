package com.cherrio.sheetsdb.init


open class InitializationException(override val message: String): Exception()
open class SheetTableException(override val message: String): Exception()
open class AuthorizationException(override val message: String): Exception()