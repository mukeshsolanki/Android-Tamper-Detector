package `in`.madapps.tamperdetection

/**
 * Created by mukeshsolanki on 12/03/19.
 */
interface OnTamperDetectionListener {
  /**
   * Triggered when the apk is tampered with along with the error message as to what the issue was
   */
  fun onAppTampered(errorMessage: String)

  /**
   * Triggered when the apk is perfectly fine meaning nothing was tampered
   */
  fun onAppOkay()
}