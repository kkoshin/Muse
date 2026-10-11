---
layout: default
title: Privacy Policy
permalink: /privacy-policy.html
---

# Muse Privacy Policy

Last updated: October 11, 2026

This policy describes how Muse, maintained by **kkoshin**, handles information in its Android application (`io.github.kkoshin.muse`). For privacy questions, contact **koshin.1116@gmail.com**. Muse does not require a separate Muse account; its online audio features use an ElevenLabs API key supplied by you.

## Information used by Muse

- **API key and preferences:** Muse stores your ElevenLabs API key, subscription status, selected voices, and application preferences locally on your device. The API key is sent to ElevenLabs to authenticate requests, including requests for available voices and subscription quota. Muse does not ask for your ElevenLabs password.
- **Text and prompts:** When you use text-to-speech or sound-effect generation, the relevant text or prompt and generation settings are sent to ElevenLabs to produce the requested audio. Text and project information may also be stored locally to support editing and reuse.
- **Audio and file information:** When you use audio isolation or speech-to-text, the audio file you select and its file name are sent to ElevenLabs to perform that operation. Muse reads selected files and stores generated or exported audio on your device.
- **Diagnostic information:** The current Android network client can write request and response details, including headers and bodies, to Android device logs. These details can contain authentication information, submitted content, and service responses. Muse does not automatically upload these device logs to a developer-operated server. Their retention is managed by the Android logging system. If you choose to provide logs for support, remove API keys and private content first.
- **Crash diagnostics:** The Google Play build does not bundle Muse's native crash-log feature; Android and Google Play may provide platform crash diagnostics under their own privacy practices. Builds distributed through GitHub Releases or F-Droid can save crash diagnostics locally for viewing in the app. Muse does not automatically upload those local crash logs to the developer.
- **Support communications:** If you contact the developer by email or open a GitHub issue, the information you voluntarily provide is available to the recipient or service you use. Public GitHub issues can be read by other people; do not include API keys or private text/audio in them.

Muse uses this information to provide audio generation, playback, editing, transcription, isolation, export, settings, and support. The current Android application does not include advertising or an automatic analytics service. Muse does not operate its own cloud storage service for your projects or audio.

## Third-party processing

Muse sends the information needed for the features above directly to **ElevenLabs** over HTTPS. ElevenLabs also receives connection information, such as your IP address, when your device contacts its service. Its handling, retention, processing locations, and available data controls are governed by your relationship with ElevenLabs and its [Privacy Policy](https://elevenlabs.io/privacy-policy). Muse does not control or promise a fixed deletion period for information retained by ElevenLabs.

When you export or share a file, Android and the destination application or service handle that operation. If you open external links, those websites apply their own privacy practices. Muse does not sell your information or send it to advertising services.

## Storage and security

The API key, preferences, and local project database are stored in the application's device storage. Generated audio can be stored in shared device media locations, and exports can be saved in locations accessible through your file manager. A file name may contain text used to generate the audio. Device access and the applications you authorize can therefore affect who can access these files.

Requests to ElevenLabs use HTTPS. Muse does not claim that the locally stored API key has an additional application-level encryption layer. Android's device and application storage protections apply. No storage or transmission method can guarantee absolute security.

## Retention and deletion

- You can remove Muse's locally stored API key, settings, and project database by clearing the application's storage in Android settings or uninstalling the application.
- Audio files and exports in shared storage may remain after clearing application storage or uninstalling. Delete these separately using your file manager or media application. Files shared with other applications or people must be managed separately.
- You can revoke or replace an API key through your ElevenLabs account. Removing a local key or uninstalling Muse does not delete your ElevenLabs account or information already processed by its service. Use ElevenLabs' account controls or contact ElevenLabs for those requests.
- For information voluntarily sent to the Muse developer for support, contact the email above with a deletion request. The developer can address information under their control; requests about third-party services must be directed to those services. Do not email your API key.

Muse does not specify automatic expiration periods for its locally stored projects and settings; these remain until removed or replaced. Device logs are subject to Android's log retention rather than a Muse cloud retention period.

## Changes and contact

This policy will be updated when Muse's information handling changes. The date above identifies the latest revision. Contact **koshin.1116@gmail.com** with questions about this policy or information handled by the developer.
