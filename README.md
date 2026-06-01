### Muse

Text to Speech

[![GitHub release](https://img.shields.io/github/v/release/kkoshin/Muse)](https://github.com/kkoshin/Muse/releases) 
[![F-Droid](https://img.shields.io/f-droid/v/io.github.kkoshin.muse)](https://f-droid.org/packages/io.github.kkoshin.muse/)
[![License](https://img.shields.io/github/license/kkoshin/Muse?color=blue)](LICENSE)
[![GitHub all releases](https://img.shields.io/github/downloads/kkoshin/Muse/total?label=Downloads&logo=github)](https://github.com/kkoshin/Muse/releases/)
[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/kkoshin/Muse)

[<img alt="Get it on F-Droid" src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png" width="240">](https://f-droid.org/packages/io.github.kkoshin.muse)
> It is recommended to use [foxy-droid](https://github.com/kitsunyan/foxy-droid) if you haven't installed the F-Droid client.

#### Preview
<p><img src="fastlane/metadata/android/en-US/images/phoneScreenshots/1.jpg" width="32%" /> <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/2.jpg" width="32%" /> <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/3.jpg" width="32%" />
<p><img src="fastlane/metadata/android/en-US/images/phoneScreenshots/4.jpg" width="32%" /> <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/5.jpg" width="32%" /> <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/6.jpg" width="32%" />

#### iOS Support
The latest version (v0.2.0 and above) supports building for iOS. You can use the `run_ios.sh` script to execute the iOS build process.

#### Development & Release
This project includes an AI agent skill to automate the release process.

A specialized **Muse Release Manager** skill handles the release SOP:
- **Versioning**: Automatic increment of `versionCode` and `versionName`.
- **Changelogs**: Automated generation of Android fastlane metadata.
- **Verification**: Pre-release build and test suite validation.

Skills are located in `.agents/skills/`. Ask the agent to "prepare the next release" to trigger the workflow.

