package com.mukesh.example.androidtamperdetector

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mukesh.example.androidtamperdetector.ui.theme.AndroidTamperDetectorTheme
import com.mukesh.tamperdetector.*


class MainActivity : ComponentActivity() {
    override
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidTamperDetectorTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DebuggerCheck()
                    SignatureValidator()
                    InstallerValidator()
                }
            }
        }
    }

    private fun showToast(@StringRes message: Int) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }

    @Composable
    fun InstallerValidator() {
        Button(onClick = {
            this.verifyInstaller(Installer.GOOGLE_PLAY_STORE)?.let {
                if (it) {
                    showToast(R.string.installer_check_success)
                } else {
                    showToast(R.string.installer_check_error)
                }
            }
        }) {
            Text(text = stringResource(id = R.string.installer_check))
        }
    }

    @Composable
    fun SignatureValidator() {
        Button(onClick = {
            if (this.validateSignature("INSERT YOUR RELEASE SIGNATURE HERE") == Result.VALID) {
                showToast(R.string.signature_check_success)
            } else {
                showToast(R.string.signature_check_error)
            }
        }) {
            Text(text = stringResource(id = R.string.signature_check))
        }
    }

    @Composable
    fun DebuggerCheck() {
        Button(onClick = {
            guardDebugger({
                showToast(R.string.debugger_check_success)
            }, {
                showToast(R.string.debugger_check_error)
            })
        }) {
            Text(text = stringResource(id = R.string.debugger_check))
        }
    }
}