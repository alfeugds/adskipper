## Ad Skipper: Privacy Policy

Welcome to the Ad Skipper app for Android, an open-source application developed by Vision Meta. You can find the source code on GitHub under the MIT license, and the app is also available on Google Play.

As an enthusiastic Android user, I prioritize your privacy. I understand the frustration of apps collecting data without your knowledge.

I want to assure you that, to the best of my knowledge, this app is not designed to collect any personally identifiable information. Any data created by you (the user) is stored solely on your device and can be easily erased by clearing the app's data or uninstalling it.

### Explanation of Permissions Requested in the App

For details about the permissions requested by the app, please refer to the `AndroidManifest.xml` file.

[https://github.com/alfeugds/adskipper/blob/main/app/src/main/AndroidManifest.xml](https://github.com/alfeugds/adskipper/blob/main/app/src/main/AndroidManifest.xml)

<br/>

| Permission | Why it is required |
| :---: | --- |
| `android.permission.MODIFY_AUDIO_SETTINGS` | This permission is needed to control the device's media volume and mute Ad videos. If revoked, Ad videos won't be muted. |
| `android.permission.BIND_ACCESSIBILITY_SERVICE` | This permission ensures that the Accessibility Service can detect when an Ad is playing on YouTube and interact with the Ad skip button. Without this permission, the app won't be able to mute or skip Ads automatically.|

 <hr style="border:1px solid gray">

If you discover any security vulnerabilities inadvertently caused by this app or have questions about how the app protects your privacy, please reach out to me via email or initiate a discussion on GitHub. I am committed to addressing and resolving any issues promptly.

Yours sincerely,
Vision Meta.
alfeudev@gmail.com
