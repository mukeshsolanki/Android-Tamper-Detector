package `in`.madapps.tamperdetection.example

import `in`.madapps.tamperdetection.Detector.Builder
import `in`.madapps.tamperdetection.OnTamperDetectionListener
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.checkAppButton
import kotlinx.android.synthetic.main.activity_main.debugToggle
import kotlinx.android.synthetic.main.activity_main.emulatorToggle

class MainActivity : AppCompatActivity(), OnTamperDetectionListener {
  var detector: Builder? = null
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    detector =
      Builder().packageName("your actual package name").listener(this@MainActivity)
        .with(this@MainActivity)
        .sha1FingerPrint("release sha 1 finger print")
//    installSource.text = detector?.getInstaller()
    setListeners()
  }

  private fun setListeners() {
    checkAppButton.setOnClickListener { detector?.build()?.check() }
    debugToggle.setOnCheckedChangeListener { _, isChecked ->
      detector?.enableDebugMode(isChecked)
    }
    emulatorToggle.setOnCheckedChangeListener { _, isChecked ->
      detector?.allowEmulators(isChecked)
    }
  }

  override fun onAppTampered(errorMessage: String) {
    Log.d("onAppTampered->", errorMessage)
  }

  override fun onAppOkay() {
    Log.d("onAppTampered->", "App is okay")
  }
}
