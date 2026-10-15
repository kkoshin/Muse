---
name: muse-release-manager
description: Manage the release process for the Muse project, including versioning, changelog generation, and pre-release verification. Use when preparing a new version tag or release for Android/iOS.
agents: [claude, gemini, cursor, copilot]
---

# Muse Release Manager

This skill automates the Standard Operating Procedure (SOP) for releasing new versions of the Muse application.

## Workflow: Prepare Release

Use this workflow to handle versioning and documentation for a new release.

### 1. Update Version Information
The version is controlled in `composeApp/build.gradle.kts` within the `finalizeDsl` block.

- **Action**: Locate `setUpStableVersion` and increment `major`, `minor`, or `patch` based on changes.
- **Action**: Increment `code` (versionCode) by 1.
- **Example**:
  ```kotlin
  extension.defaultConfig.setUpStableVersion(
      major = 0,
      minor = 2,
      patch = 1, // Incremented from 0
      code = 7,   // Incremented from 6
  )
  ```

### 2. Update Muse Module Version (CocoaPods)
In `muse/build.gradle.kts`, ensure the `cocoapods.version` matches the semantic version (`major.minor.patch`).

### 3. Generate Changelog
- **Path**: `fastlane/metadata/android/en-US/changelogs/`
- **Action**: Create a new file named `<versionCode>.txt` (e.g., `7.txt`).
- **Content**: Summarize key changes since the last release. Use `git log` to identify new features and fixes.

### 4. Database Check
Check if any `.sq` files in `muse/src/commonMain/sqldelight/` have changed. If so:
- Ensure the database version in the `sqldelight` config or relevant migration code is updated.

## Workflow: Build & Verification

Use this workflow to ensure the project is stable before tagging.

### 1. Android Unit Tests
Run the Android unit tests only:
```bash
./gradlew :muse:testReleaseUnitTest
```

## Workflow: Finalize & Publish

### 1. Commit Changes
Commit the version updates and changelog:
```bash
git add composeApp/build.gradle.kts muse/build.gradle.kts fastlane/metadata/android/en-US/changelogs/
git commit -m "chore(release): Prepare release version <versionName> (<code>)"
```

### 2. Create Tag & Release Instructions
After pushing to main:
1. Create a Git Tag: `git tag -a v<versionName> -m "Release v<versionName>"`
2. Push Tag: `git push origin v<versionName>`
3. Draft GitHub Release on the repository page.

## Workflow: Publish to Google Play

Google Play uses the signed **AAB** produced by the `package.yml` workflow (manual upload to Play Console; no Play API integration).

The AAB comes from the **`play` build type** (`./gradlew :composeApp:bundlePlay`), not `release`: the `release` variant keeps xcrash for GitHub Releases / F-Droid, while xcrash's prebuilt `.so` files are not 16 KB page aligned and Google Play rejects such bundles. Crash reporting for the Play build therefore comes from Google Play's **Android vitals** instead of the in-app crash log. The workflow asserts the Play AAB contains no xcrash and fails the build if it reappears.

1. Download the `play-aab` artifact from the release workflow run (`Muse-<version>.aab` + `mapping.txt`).
2. Play Console → Testing → Internal testing (first) or Production → create a new release → upload the AAB.
3. Upload `mapping.txt` as the deobfuscation file.
4. Paste release notes from `fastlane/metadata/android/en-US/changelogs/<versionCode>.txt`.
5. Review and roll out.

One-time Play Console setup, required GitHub secrets (`PLAY_KEYSTORE_*`), review tips, and Android developer verification (package-name + signing-key registration — submission is blocked until every signing key of the package name is registered): see [references/play-store-guide.md](references/play-store-guide.md).

## Reference Material
- Full manual checklist: [references/release-guide.md](references/release-guide.md).
- Google Play one-time setup, publish flow & Android developer verification: [references/play-store-guide.md](references/play-store-guide.md).
