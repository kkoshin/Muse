# Implementation Plan: Remove sharedLibs Usage

This plan outlines the steps to migrate dependency management from `sharedLibs` to the Gradle version catalog (`libs.versions.toml`) across the `Root`, `composeApp`, `muse`, and `elevenlabs` modules.

## Phase 1: Preparation and Catalog Population

In this phase, we will identify all dependencies in `sharedLibs` and ensure they are present in `libs.versions.toml` with the correct versions.

- [x] Task: Audit `sharedLibs` and `libs.versions.toml`.
    - [x] List all dependencies currently defined in `sharedLibs`.
    - [x] Compare with `libs.versions.toml` and identify missing entries.
- [x] Task: Populate `libs.versions.toml`.
    - [x] Add missing dependencies to `libs.versions.toml` using the versions from `sharedLibs`.
    - [x] Verify that all added entries follow the project's naming conventions.
- [x] Task: Conductor - User Manual Verification 'Phase 1: Preparation and Catalog Population' (Protocol in workflow.md)

## Phase 2: Root Module Migration

Migrate the root `build.gradle.kts` to use the version catalog.

- [x] Task: Refactor root `build.gradle.kts`.
    - [x] Replace `sharedLibs` references with `libs`.
    - [x] Run `./gradlew help` to ensure the build script still evaluates correctly.
- [x] Task: Conductor - User Manual Verification 'Phase 2: Root Module Migration' (Protocol in workflow.md)

## Phase 3: Module Migration (composeApp, muse, elevenlabs)

Migrate the remaining modules one by one.

- [x] Task: Refactor `composeApp/build.gradle.kts`.
    - [x] Replace `sharedLibs` references with `libs`.
- [x] Task: Refactor `muse/build.gradle.kts`.
    - [x] Replace `sharedLibs` references with `libs`.
- [x] Task: Refactor `elevenlabs/build.gradle.kts`.
    - [x] Replace `sharedLibs` references with `libs`.
- [x] Task: Conductor - User Manual Verification 'Phase 3: Module Migration' (Protocol in workflow.md)

## Phase 4: Cleanup and Final Verification

Remove the now-unused `sharedLibs` and perform a full build.

- [x] Task: Remove `sharedLibs` definition.
    - [x] Identify where `sharedLibs` is defined (likely a `buildSrc` or a separate script file).
    - [x] Remove the definition.
- [x] Task: Final Build Verification.
    - [x] Run a full clean build: `./gradlew clean assemble`.
    - [x] Verify that all dependencies are correctly resolved.
- [x] Task: Conductor - User Manual Verification 'Phase 4: Cleanup and Final Verification' (Protocol in workflow.md)
