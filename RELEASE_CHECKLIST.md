# Android Release Regression Checklist

Run these checks on the release candidate before submission. Record the device or emulator model, Android version, and result for each section. A successful build alone does not confirm Google Play submission eligibility or runtime compatibility. Confirm the current requirements and this app's eligibility in Play Console before submitting.

## Android 16 / API 36

- [ ] Verify predictive back from the main screen and nested screens, including canceling and completing the gesture.
- [ ] Open and dismiss app dialogs and system permission prompts; confirm they remain visible, correctly positioned, and usable with edge-to-edge layout.
- [ ] Dismiss bottom sheets with Back and verify the keyboard, top app bar, and bottom actions with gesture and three-button navigation.
- [ ] Generate audio, then export and share it; confirm the saved file plays and the share sheet completes successfully.

## Android 15 / API 35

- [ ] Repeat the main navigation, dialog and permission prompt, audio generation, export, and share checks.

## Tablet and window changes

- [ ] On a tablet, rotate the device and enter split-screen mode; confirm navigation, dialogs, audio generation, export, and sharing continue to work after resizing.

## Verification record

- Automated checks on 2026-10-11: `:muse:testReleaseUnitTest` passed (2 tests, 0 failures); `:composeApp:bundleRelease` passed, including release shrinking and vital lint.
- Merged release manifest: target SDK 36, minimum SDK 29, version name 0.2.2, version code 8.
- `bundletool` 1.18.1 validated the final AAB; its manifest reports target SDK 36, version name 0.2.2, and version code 8. This local bundle still requires signing with the Play upload key through the release workflow.
- Device or emulator and Android API: ____________________
- Date and tester: ____________________
- Results or issues: ____________________

## Known release risks

- The existing `arm64-v8a` `libxcrash.so` and `libxcrash_dumper.so` have ELF `PT_LOAD` alignment of `0x1000`, which does not satisfy 16 KB page-size requirements. This target SDK change does not resolve that issue; confirm Play Console acceptance and address the libraries before claiming 16 KB compatibility.
- `TAndroidLame` has ELF `PT_LOAD` alignment of `0x10000`, but its runtime behavior has not been verified on a 16 KB page-size device; this is not confirmation of full 16 KB compatibility.
- The R8 Kotlin metadata warning remains unresolved.
- No connected devices were available, so none of the manual regression checks above have been run for this release candidate.

Check [Android's page size guidance](https://developer.android.com/guide/practices/page-sizes) and the app-specific Play Console status at submission time. The general policy timeline does not guarantee acceptance for a particular app or console account.
