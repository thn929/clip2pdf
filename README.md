# Clip2PDF

Clip2PDF is a minimal Android utility app that turns shared text into a simple PDF.

It is intentionally small: no accounts, no storage permissions, no preview screen, no editor, no database, no networking, and no app workflow to manage. Share text to it, choose where to save the PDF, and it exits.

## Share Workflow

1. Select text in Chrome or another Android app.
2. Tap **Share**.
3. Choose **Clip2PDF** from the Android share sheet.
4. Android opens the system file-save picker.
5. Choose a location and filename.
6. Clip2PDF writes a plain black-on-white PDF and closes.

Clip2PDF registers for:

- `ACTION_SEND` with MIME type `text/plain`
- `ACTION_PROCESS_TEXT` with MIME type `text/plain`, so it can appear in text-selection actions on Android versions and apps that support that flow

The app reads shared text from `Intent.EXTRA_TEXT`, and process-text input from `Intent.EXTRA_PROCESS_TEXT`. Empty or missing text is handled with a small error message.

## PDF Output

The PDF writer uses Android platform APIs:

- `PdfDocument` for PDF creation
- `StaticLayout` for Unicode-aware text shaping, wrapping, paragraph breaks, and pagination
- ordinary letter-size pages, readable margins, and 12 pt sans-serif text

The default filename is timestamped, for example:

```text
clip-2026-08-27-1015.pdf
```

## Building

You do not need Android Studio, the Android SDK, Gradle, Java, Kotlin, ADB, or an emulator installed locally.

GitHub Actions builds the app on every push and on manual workflow dispatch:

- workflow: `.github/workflows/android.yml`
- artifact name: `Clip2PDF-debug-apk`
- APK path inside the artifact: `Clip2PDF-debug.apk`

When a tag beginning with `v` is pushed, for example `v0.1.0`, the workflow also creates a GitHub Release and attaches `Clip2PDF-debug.apk` as a downloadable release asset. You can also publish a release manually from the workflow page by enabling the `publish_release` input.

For now this is an installable debug-signed APK. That is fine for sideloading and sharing a small utility from GitHub. It is not Play Store release signing.

## Downloading the APK from GitHub

Recommended public download flow:

1. Open the repository on GitHub.
2. Tap **Releases**.
3. Open the latest release.
4. Download **Clip2PDF-debug.apk**.
5. Install it on your Android phone.

Workflow artifact download flow:

1. Open the repository on GitHub.
2. Tap **Actions**.
3. Open the latest successful **Android** workflow run.
4. Scroll to **Artifacts**.
5. Download **Clip2PDF-debug-apk**.
6. Unzip the downloaded artifact.
7. Install `Clip2PDF-debug.apk` on your Android phone.

## Installing on Android

Because this is a debug APK downloaded outside the Play Store, Android may ask you to allow installation from your browser or file manager.

Typical phone flow:

1. Open `app-debug.apk` on the phone.
2. If prompted, allow installs from that source.
3. Continue the install.
4. Select text in another app, tap **Share**, and choose **Clip2PDF**.

No broad storage permission is needed. Saving uses Android's system document creation picker.

## Tests

The project includes a small JVM unit test for the shared PDF layout math:

```bash
./gradlew testDebugUnitTest
```

GitHub Actions runs this before building the debug APK.
