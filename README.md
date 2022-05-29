<h1 align="center">Android Tamper Detector</h1>
<p align="center">
  <a href="https://jitpack.io/#mukeshsolanki/Android-Tamper-Detector"> <img src="https://jitpack.io/v/mukeshsolanki/Android-Tamper-Detector/month.svg" /></a>
  <a href="https://jitpack.io/#mukeshsolanki/Android-Tamper-Detector"> <img src="https://jitpack.io/v/mukeshsolanki/Android-Tamper-Detector.svg" /></a>
  <a href="https://github.com/mukeshsolanki/Android-Tamper-Detector/actions"> <img src="https://github.com/mukeshsolanki/Android-Tamper-Detector/workflows/Build/badge.svg" /></a>
  <a href="https://opensource.org/licenses/MIT"><img src="https://img.shields.io/badge/License-MIT-blue.svg"/></a>
  <br /><br />
    A simple library that can help you detect if you app is modded or tampered with or debugger tools are being used to break your app and checks install source.
</p>

# Supporting Tamper Detector

Tamper Detector is an independent project with ongoing development and support made possible thanks to donations made by you.
- [Become a backer](https://www.paypal.me/mukeshsolanki)

## How to integrate into your app?
To integrate tamper detector to your project, Follow the below steps

Step 1. Add the JitPack repository to your build file. Add it in your root build.gradle at the end of repositories:

```groovy
allprojects {
  repositories {
    ...
    maven { url "https://jitpack.io" }
  }
}
```
Step 2. Add the dependency
```groovy
dependencies {
        debugImplementation 'com.github.mukeshsolanki.Android-Tamper-Detector:tamperdetector-no-op:1.0.0'
        releaseImplementation 'com.github.mukeshsolanki.Android-Tamper-Detector:tamperdetector:1.0.0'
}
```

## How to use the library?
Okay seems like you integrated the library in your project but **how do you use it**? Well its really easy
1. Debugger Check
You can make a debugger check before running any function by calling `guardDebugger()`
```kotlin
guardDebugger({
    //No debugger tools detected continue executing the code.
}, {
    //Some debugger tools were detected.
})
```

2. Verify Installer
To verify whether your app is downloaded from the right source call `verifyInstaller()`
```kotlin
this.verifyInstaller(Installer.GOOGLE_PLAY_STORE)?.let {
    if (it) {
        // App is installed from Google Play
    } else {
        // App is not installed from Google Play
    }
}
```

3. Verify Signature
To verify whether your app has been modified or not you can use `validateSignature()`. You can get your release signature from [here](#how-to-get-release-signature)
```kotlin
if (this.validateSignature("INSERT YOUR RELEASE SIGNATURE HERE") == Result.VALID) {
    // Signature is valid continue using the app
} else {
    // Signature is invalid likely a modded version of the app
}
```

## How to get release signature?
This would be a onetime process. Inorder to get your signature you would have to temporarily enable debugging on the release version of the app and sign the app. Don't worry you don't have to publish the app you just want to take a look at the logs for the release version.
Step 1. Change the releaseImplementation to point to 'com.github.mukeshsolanki.Android-Tamper-Detector:tamperdetector-no-op:1.0.0'
Step 2. Adding the `debuggable true` to your `app/build.gradle` file
```groovy
    ...
    buildTypes {
        debug {

        }
        release {
            debuggable true // This is the temporary flag we added. Please remove this once you have got the signature
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
    ...
```
Next we are going to call the `getSignature()` to get and log our signature.
```Kotlin
    Log.d("Signature", context.getSignature())
```
Sign the app and run the release version and look at logcat. You should be able to see your release signature on the console. Note it down and then we can use this to securely validate your apk using `validateSignature()`.
**Please remove the debuggable flag and change the releaseImplementation to the original one back once you have noted down the signature. Also the getSignature() method only works on the debug version and not on production**

## Author
Maintained by [Mukesh Solanki](https://www.github.com/mukeshsolanki)

## Contribution
[![GitHub contributors](https://img.shields.io/github/contributors/mukeshsolanki/Android-Tamper-Detector.svg)](https://github.com/mukeshsolanki/Android-Tamper-Detector/graphs/contributors)

* Bug reports and pull requests are welcome.

## License
```
MIT License

Copyright (c) 2019 Mukesh Solanki

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
