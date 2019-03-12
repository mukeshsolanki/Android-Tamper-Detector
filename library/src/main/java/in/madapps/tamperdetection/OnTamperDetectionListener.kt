package `in`.madapps.tamperdetection

/**
 * Created by mukeshsolanki on 12/03/19.
 */
interface OnTamperDetectionListener {
  fun onAppTampered(errorMessage: String)
  fun onAppOkay()
}