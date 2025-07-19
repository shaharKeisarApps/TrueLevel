package com.keisardev.truelevel

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform