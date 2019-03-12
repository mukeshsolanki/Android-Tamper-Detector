package `in`.madapps.tamperdetection

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import android.text.TextUtils
import java.io.ByteArrayInputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateEncodingException
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.security.auth.x500.X500Principal

/**
 * Created by mukeshsolanki on 12/03/19.
 */
class Detector(private val context: Context, private val listener: OnTamperDetectionListener) {

  private val DEBUG_DN = X500Principal("CN=Android Debug,O=Android,C=US")
  private var debugMode = false
  private var runOnEmulator = false
  private val allowDebugKeystore = false
  private var sha1Key: String = ""

  fun getInstaller(): String? {
    return context.packageManager.getInstallerPackageName(context.packageName)
  }

  fun isDebugBuild(): Boolean {
    return (BuildConfig.DEBUG
        || isSignedWithDebugKeystore()
        || 0 != context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE || !allowDebugKeystore && isSignedWithDebugKeystore())
  }

  fun isDebugModeEnabled(): Boolean {
    return debugMode
  }

  fun allowDebugMode(state: Boolean) {
    debugMode = state
  }

  fun isRunOnEmulatorEnabled(): Boolean {
    return this.runOnEmulator
  }

  fun shouldRunOnEmulator(state: Boolean) {
    runOnEmulator = state
  }

  fun isRunningOnEmulator(): Boolean {
    val buildDetails =
      (Build.FINGERPRINT + Build.DEVICE + Build.MODEL + Build.BRAND + Build.PRODUCT + Build.MANUFACTURER + Build.HARDWARE).toLowerCase()
    return (buildDetails.contains("generic")
        || buildDetails.contains("unknown")
        || buildDetails.contains("emulator")
        || buildDetails.contains("sdk")
        || buildDetails.contains("genymotion")
        || buildDetails.contains("x86")
        || buildDetails.contains("goldfish")
        || buildDetails.contains("test-keys"))
  }

  fun releaseSHA1FingerPrint(fingerPrint: String) {
    this.sha1Key = fingerPrint
  }

  fun check() {
    if (TextUtils.isEmpty(sha1Key)) {
      throw IllegalArgumentException("Please call releaseSHA1FingerPrint(fingerPrint: String) before checking")
    }
    if (isDebugBuild() && !isDebugModeEnabled()) {
      listener.onAppTampered("The app is running in DEBUG mode")
      return
    }
    if (isRunningOnEmulator() && !isRunOnEmulatorEnabled()) {
      listener.onAppTampered("The app is running on an emulator")
      return
    }
    if (!isCertificateSHA1FingerPrintValid(sha1Key)) {
      listener.onAppTampered("The certificate is invalid")
      return
    }
    listener.onAppOkay()
  }

  fun isCertificateSHA1FingerPrintValid(sha1Key: String): Boolean {
    return getCertificateSHA1FingerPrint().equals(sha1Key, true)
  }

  @SuppressLint("PackageManagerGetSignatures")
  fun getCertificateSHA1FingerPrint(): String? {
    val pm = context.packageManager
    val packageName = context.packageName
    val flags = if (Build.VERSION.SDK_INT >= 28) {
      PackageManager.GET_SIGNING_CERTIFICATES
    } else {
      PackageManager.GET_SIGNATURES
    }
    var packageInfo: PackageInfo? = null
    try {
      packageInfo = pm.getPackageInfo(packageName, flags)
    } catch (e: PackageManager.NameNotFoundException) {
      e.printStackTrace()
    }

    val signatures = if (Build.VERSION.SDK_INT >= 28) {
      packageInfo!!.signingInfo!!.apkContentsSigners
    } else {
      packageInfo!!.signatures
    }
    val cert = signatures[0].toByteArray()
    val input = ByteArrayInputStream(cert)
    var cf: CertificateFactory? = null
    try {
      cf = CertificateFactory.getInstance("X509")
    } catch (e: CertificateException) {
      e.printStackTrace()
    }

    var c: X509Certificate? = null
    try {
      c = cf!!.generateCertificate(input) as X509Certificate
    } catch (e: CertificateException) {
      e.printStackTrace()
    }

    var hexString: String? = null
    try {
      val md = MessageDigest.getInstance("SHA1")
      val publicKey = md.digest(c!!.encoded)
      hexString = byte2HexFormatted(publicKey)
    } catch (e1: NoSuchAlgorithmException) {
      e1.printStackTrace()
    } catch (e: CertificateEncodingException) {
      e.printStackTrace()
    }

    return hexString
  }

  @SuppressLint("PackageManagerGetSignatures")
  private fun isSignedWithDebugKeystore(): Boolean {
    var debuggable = false

    try {
      val pInfo: PackageInfo
      val signatures: Array<Signature>
      if (Build.VERSION.SDK_INT >= 28) {
        pInfo = context.packageManager
          .getPackageInfo(context.packageName, PackageManager.GET_SIGNING_CERTIFICATES)
        signatures = pInfo.signingInfo.apkContentsSigners
      } else {
        pInfo = context.packageManager
          .getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
        signatures = pInfo.signatures
      }
      val cf = CertificateFactory.getInstance("X.509")
      for (i in signatures.indices) {
        val stream = ByteArrayInputStream(signatures[i].toByteArray())
        val cert = cf.generateCertificate(stream) as X509Certificate
        debuggable = cert.subjectX500Principal == DEBUG_DN
        if (debuggable)
          break
      }
    } catch (e: PackageManager.NameNotFoundException) {
    } catch (e: CertificateException) {
    }

    return debuggable
  }

  private fun byte2HexFormatted(arr: ByteArray): String {
    val str = StringBuilder(arr.size * 2)
    for (i in arr.indices) {
      var h = Integer.toHexString(arr[i].toInt())
      val l = h.length
      if (l == 1) h = "0$h"
      if (l > 2) h = h.substring(l - 2, l)
      str.append(h.toUpperCase())
      if (i < arr.size - 1) str.append(':')
    }
    return str.toString()
  }
}