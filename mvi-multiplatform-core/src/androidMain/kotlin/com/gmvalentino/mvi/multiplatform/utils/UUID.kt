package com.gmvalentino.mvi.multiplatform.utils

import java.util.UUID

actual object UUID {
    actual fun randomUUID(): String = UUID.randomUUID().toString()
}
