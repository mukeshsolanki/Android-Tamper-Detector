package com.mukesh.example.androidtamperdetector

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import com.mukesh.example.androidtamperdetector.ui.theme.AndroidTamperDetectorTheme
import com.mukesh.tamperdetector.Installer
import com.mukesh.tamperdetector.guardDebugger
import com.mukesh.tamperdetector.validateSignature
import com.mukesh.tamperdetector.verifyInstaller


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.validateSignature("wbKfw2DF1uofrTXr+mCvlZd0RHY=")
        this.verifyInstaller(Installer.GOOGLE_PLAY_STORE)
        setContent {
            AndroidTamperDetectorTheme {
                Column(modifier = Modifier.fillMaxSize()) {
                    Button(onClick = {
                        guardDebugger {
                            Toast.makeText(
                                this@MainActivity,
                                "Debugger is not Attached",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }) {
                        Text(text = "Check Debugger Attached")
                    }
                }
            }
        }
    }
}