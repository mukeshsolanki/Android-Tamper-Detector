package com.mukesh.tamperdetector

fun guardDebugger(error: (() -> Unit) = {}, function: (() -> Unit)) {
    function.invoke()
}