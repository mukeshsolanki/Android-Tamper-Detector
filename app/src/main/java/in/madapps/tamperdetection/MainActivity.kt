package `in`.madapps.tamperdetection

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.checkAppButton
import kotlinx.android.synthetic.main.activity_main.debugToggle
import kotlinx.android.synthetic.main.activity_main.emulatorToggle
import kotlinx.android.synthetic.main.activity_main.installSource
import kotlinx.android.synthetic.main.activity_main.shaKey

class MainActivity : AppCompatActivity(), OnTamperDetectionListener {
  var detector: Detector? = null
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    detector = Detector(this@MainActivity, this@MainActivity)
    detector?.releaseSHA1FingerPrint("YourKey")
    installSource.text = detector?.getInstaller()
    shaKey.text = detector?.getCertificateSHA1FingerPrint()
    setListeners()
  }

  private fun setListeners() {
    checkAppButton.setOnClickListener { detector?.check() }
    debugToggle.setOnCheckedChangeListener { _, isChecked ->
      detector?.allowDebugMode(isChecked)
    }
    emulatorToggle.setOnCheckedChangeListener { _, isChecked ->
      detector?.shouldRunOnEmulator(isChecked)
    }
  }

  override fun onAppTampered(errorMessage: String) {
    Log.d("onAppTampered->", errorMessage)
  }

  override fun onAppOkay() {
    Log.d("onAppTampered->", "App is okay")
  }
}
