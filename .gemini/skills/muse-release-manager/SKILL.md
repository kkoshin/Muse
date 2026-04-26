---
name: muse-release-manager
description: Manage the release process for the Muse project, including versioning, changelog generation, and pre-release verification. Use when preparing a new version tag or release for Android/iOS.
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

### 1. Automated Tests
Run the test suite:
```bash
./gradlew test
```

### 2. iOS Framework Verification
Ensure the KMP iOS framework builds:
```bash
./gradlew :muse:linkReleaseFrameworkIosArm64
```
*(Note: Check if `:muse` or `:composeApp` is the correct module for the iOS framework based on project evolution).*

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

## Reference Material
For the full manual checklist, see [references/release-guide.md](references/release-guide.md).
