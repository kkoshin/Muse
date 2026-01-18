Pre-release Checklist

Please confirm each item on this checklist before pushing a new version tag and release.

#### ✅ Pre-release Checks

• Update Version Number: Manually update the version number in the project (e.g., in build.gradle.kts).

• Check Database Version: If there are any database schema changes, confirm the version number has been updated and migration scripts are prepared.

• Update Changelog: Add the latest version description file in fastlane/metadata/android/en-US/changelogs/, clearly describing new features, changes, and fixes.

• (Optional) Update Screenshots: Update screenshots in fastlane/metadata/android/en-US/images/phoneScreenshots.

#### ✅ Build & Verification

• Run Full Test Suite: Execute ./gradlew test (or the appropriate command) to ensure all tests pass.

• Manually Trigger Workflow: Trigger the GitHub Actions workflow to generate a release build.

• Verify Core Features: Manually test the main workflows on Android and iOS emulators/devices.

• iOS Build: Confirm the Kotlin Multiplatform iOS framework builds successfully (e.g., ./gradlew :shared:linkReleaseFrameworkIosX64).

#### ✅ Final Release

• Commit All Changes: Ensure all modifications (version number, changelog, etc.) are committed to the main branch.

• Manually Create a GitHub Release:

    ◦   Go to the repository's Releases page and click "Draft a new release".

    ◦   Create a Tag with the new version number (e.g., v1.2.0).

    ◦   Important: Publishing this Release will automatically trigger the GitHub Actions workflow to handle the build and packaging.

• Subsequent Automated Process:

    ◦   GitHub Actions will automatically handle the packaging tasks.

    ◦   F-Droid's release channel will detect and fetch this new version after a period of time.