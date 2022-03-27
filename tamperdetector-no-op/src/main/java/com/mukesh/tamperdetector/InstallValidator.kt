package com.mukesh.tamperdetector

import android.content.Context
import android.os.Build

enum class Installer(val id: String) {
    GOOGLE_PLAY_STORE(id = "com.android.vending"),
    AMAZON_APP_STORE(id = "com.amazon.venezia")
}

fun Context.verifyInstaller(installer: Installer): Boolean? {
    return true
}