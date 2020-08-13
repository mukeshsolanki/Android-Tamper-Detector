<h1 align="center">Android Tamper Detector</h1>
<p align="center">
  <a href="https://codeclimate.com/github/mukeshsolanki/Android-Tamper-Detector/maintainability"><img src="https://api.codeclimate.com/v1/badges/cbcf70f1f6dd85432504/maintainability" /></a>
  <a href="https://www.codacy.com/app/mukeshsolanki/Android-Tamper-Detector?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=mukeshsolanki/Android-Tamper-Detector&amp;utm_campaign=Badge_Grade"><img src="https://api.codacy.com/project/badge/Grade/c86e3c0ade6849ff9b10b3b06591a75c"/></a>
  <a href="https://jitpack.io/#mukeshsolanki/Android-Tamper-Detector"> <img src="https://jitpack.io/v/mukeshsolanki/Android-Tamper-Detector/month.svg" /></a>
  <a href="https://jitpack.io/#mukeshsolanki/Android-Tamper-Detector"> <img src="https://jitpack.io/v/mukeshsolanki/Android-Tamper-Detector.svg" /></a>
  <a href="https://github.com/mukeshsolanki/Android-Tamper-Detector/actions"> <img src="https://github.com/mukeshsolanki/Android-Tamper-Detector/workflows/Build/badge.svg" /></a>
  <a href="https://opensource.org/licenses/MIT"><img src="https://img.shields.io/badge/License-MIT-blue.svg"/></a>
  <br /><br />
    A simple library that can help you detect if you app is modded or tampered with. This adds a security level that makes it difficult to crack your app.
</p>

# Supporting Tamper Detector

Tamper Detector is an independent project with ongoing development and support made possible thanks to donations made by [these awesome backers](BACKERS.md#sponsors). If you'd like to join them, please consider:

- [Become a backer or sponsor on Patreon](https://www.patreon.com/mukeshsolanki).
- [One-time donation via PayPal](https://www.paypal.me/mukeshsolanki)

<a href="https://www.patreon.com/bePatron?c=935498" alt="Become a Patron"><img src="https://c5.patreon.com/external/logo/become_a_patron_button.png" /></a>

## How to integrate into your app?
Integrating the project is simple a refined all you need to do is follow the below steps

Step 1. Add the JitPack repository to your build file. Add it in your root build.gradle at the end of repositories:

```java
allprojects {
  repositories {
    ...
    maven { url "https://jitpack.io" }
  }
}
```
Step 2. Add the dependency
```java
dependencies {
        implementation 'com.github.mukeshsolanki:Android-Tamper-Detector:<latest-version>'
}
```

## How to use the library?
Okay seems like you integrated the library in your project but **how do you use it**? Well its really easy just do this in the app launch

```java
.....
 detector =
      Detector.Builder().packageName("your actual package name get this via an api call").listener(object: OnTamperDetectionListener{
       override fun onAppTampered(errorMessage: String) {
          Log.d("onAppTampered->", errorMessage)
        }

        override fun onAppOkay() {
          Log.d("onAppTampered->", "App is okay")
        }
      })
        .with(context)
        .sha1FingerPrint("release sha 1 finger print get this via an api call as well").build()

 detector.check()
.....
```
You simple create a Detector object using the builder and set the details and call the `check()` and that should be it you will receive a call back with the details in the OnTamperDetectionListener.

To enable debug for development you can do `detector.enableDebugMode(true)` and `detector.allowEmulators(true)`
That's pretty much it and your all wrapped up.

## Author
Maintained by [Mukesh Solanki](https://www.github.com/mukeshsolanki)

## Contribution
[![GitHub contributors](https://img.shields.io/github/contributors/mukeshsolanki/Android-Tamper-Detector.svg)](https://github.com/mukeshsolanki/Android-Tamper-Detector/graphs/contributors)

* Bug reports and pull requests are welcome.
* Make sure you use [square/java-code-styles](https://github.com/square/java-code-styles) to format your code.

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
