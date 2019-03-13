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
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.security.auth.x500.X500Principal

/**
 * Created by mukeshsolanki on 12/03/19.
 */
class Detector private constructor(
  private val context: Context,
  private val listener: OnTamperDetectionListener,
  private val debugMode: Boolean,
  private val runOnEmulator: Boolean,
  private val sha1Key: String,
  private val packageName: String
) {

  private val DEBUG_DN = X500Principal("CN=Android Debug,O=Android,C=US")
  private val allowDebugKeystore = false

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

  fun isRunOnEmulatorEnabled(): Boolean {
    return this.runOnEmulator
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

  fun check() {
    if (TextUtils.isEmpty(sha1Key)) {
      throw IllegalArgumentException("Please call sha1FingerPrint(fingerPrint: String) before checking")
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
    if (packageName != context.packageName) {
      listener.onAppTampered("The package name is invalid")
      return
    }
    listener.onAppOkay()
  }

  private fun isCertificateSHA1FingerPrintValid(sha1Key: String): Boolean {
    return getCertificateSHA1FingerPrint().equals(sha1Key, true)
  }

  @SuppressLint("PackageManagerGetSignatures")
  private fun getCertificateSHA1FingerPrint(): String? {
    val packageManager = context.packageManager
    val flags = getFlags()
    val packageInfo: PackageInfo? = getPackageInfo(packageManager, flags)
    val signatures = getSignatures(packageInfo)
    val cert = signatures[0].toByteArray()
    val inputStream = ByteArrayInputStream(cert)
    val certificateFactory: CertificateFactory
    val certificate: X509Certificate
    var hexString: String? = null
    try {
      certificateFactory = CertificateFactory.getInstance("X509")
      certificate = certificateFactory.generateCertificate(inputStream) as X509Certificate
      val md = MessageDigest.getInstance("SHA1")
      val publicKey = md.digest(certificate.encoded)
      hexString = byte2HexFormatted(publicKey)
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return hexString
  }

  private fun getSignatures(packageInfo: PackageInfo?): Array<Signature> =
    if (Build.VERSION.SDK_INT >= 28) {
      packageInfo!!.signingInfo!!.apkContentsSigners
    } else {
      packageInfo!!.signatures
    }

  private fun getPackageInfo(packageManager: PackageManager, flags: Int): PackageInfo? = try {
    packageManager.getPackageInfo(context.packageName, flags)
  } catch (e: PackageManager.NameNotFoundException) {
    e.printStackTrace()
    null
  }

  private fun getFlags(): Int = if (Build.VERSION.SDK_INT >= 28) {
    PackageManager.GET_SIGNING_CERTIFICATES
  } else {
    PackageManager.GET_SIGNATURES
  }

  @SuppressLint("PackageManagerGetSignatures")
  private fun isSignedWithDebugKeystore(): Boolean {
    var debuggable = false
    try {
      val packageInfo: PackageInfo? = getPackageInfo(context.packageManager, getFlags())
      val signatures: Array<Signature> = getSignatures(packageInfo)
      val certificateFactory = CertificateFactory.getInstance("X.509")
      for (index in signatures.indices) {
        val stream = ByteArrayInputStream(signatures[index].toByteArray())
        val cert = certificateFactory.generateCertificate(stream) as X509Certificate
        debuggable = cert.subjectX500Principal == DEBUG_DN
        if (debuggable)
          break
      }
    } catch (e: Exception) {
      e.printStackTrace()
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

  data class Builder(
    private var context: Context? = null,
    private var listener: OnTamperDetectionListener? = null,
    private var allowDebugMode: Boolean = false,
    private var allowEmulators: Boolean = false,
    private var sha1FingerPrint: String? = null,
    private var packageName: String? = null
  ) {
    fun with(context: Context) = apply { this.context = context }
    fun listener(listener: OnTamperDetectionListener) = apply { this.listener = listener }
    fun enableDebugMode(allowDebugMode: Boolean) = apply { this.allowDebugMode = allowDebugMode }
    fun allowEmulators(allowEmulators: Boolean) = apply { this.allowEmulators = allowEmulators }
    fun sha1FingerPrint(sha1FingerPrint: String) = apply { this.sha1FingerPrint = sha1FingerPrint }
    fun packageName(packageName: String) = apply { this.packageName = packageName }

    fun build(): Detector {
      if (context == null) {
        throw IllegalArgumentException("Context cannot be empty please use with(context: Context)")
      }
      if (listener == null) {
        throw IllegalArgumentException("Listener cannot be empty please use listener(listener: OnTamperDetectionListener)")
      }
      if (TextUtils.isEmpty(sha1FingerPrint)) {
        throw IllegalArgumentException("SHA1FingerPrint cannot be empty please use sha1FingerPrint(sha1FingerPrint: String)")
      }
      if (TextUtils.isEmpty(packageName)) {
        throw IllegalArgumentException("PackageName cannot be empty please use packageName(packageName: String)")
      }
      return Detector(
        context!!,
        listener!!,
        allowDebugMode,
        allowEmulators,
        sha1FingerPrint!!,
        packageName!!
      )
    }
  }
}