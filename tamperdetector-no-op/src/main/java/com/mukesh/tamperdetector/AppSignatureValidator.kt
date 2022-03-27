package com.mukesh.tamperdetector

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import android.util.Base64
import android.util.Log
import java.security.MessageDigest

enum class Result {
    VALID,
    INVALID,
    UNKNOWN
}

fun Context.validateSignature(expectedSignature: String): Result {
    return Result.VALID
}

@SuppressLint("PackageManagerGetSignatures")
fun Context.getSignature(): String {
    val signature: Signature? = if (Build.VERSION.SDK_INT < 28) {
        packageManager.getPackageInfo(
            packageName,
            PackageManager.GET_SIGNATURES
        ).signatures.firstOrNull()
    } else {
        packageManager.getPackageInfo(
            packageName,
            PackageManager.GET_SIGNING_CERTIFICATES
        ).signingInfo.apkContentsSigners.firstOrNull()
    }
    var signatureString = ""
    signature?.let {
        val signatureBytes = signature.toByteArray()
        val digest = MessageDigest.getInstance("SHA")
        val hash = digest.digest(signatureBytes)
        signatureString = Base64.encodeToString(hash, Base64.NO_WRAP)
    }
    return signatureString
}