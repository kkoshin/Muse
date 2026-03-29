# Implementation Plan: Remove sharedLibs Usage

This plan outlines the steps to migrate dependency management from `sharedLibs` to the Gradle version catalog (`libs.versions.toml`) across the `Root`, `composeApp`, `muse`, and `elevenlabs` modules.

## Phase 1: Preparation and Catalog Population

In this phase, we will identify all dependencies in `sharedLibs` and ensure they are present in `libs.versions.toml` with the correct versions.

- [ ] Task: Audit `sharedLibs` and `libs.versions.toml`.
    - [ ] List all dependencies currently defined in `sharedLibs`.
    - [ ] Compare with `libs.versions.toml` and identify missing entries.
- [ ] Task: Populate `libs.versions.toml`.
    - [ ] Add missing dependencies to `libs.versions.toml` using the versions from `sharedLibs`.
    - [ ] Verify that all added entries follow the project's naming conventions.
- [ ] Task: Conductor - User Manual Verification 'Phase 1: Preparation and Catalog Population' (Protocol in workflow.md)

## Phase 2: Root Module Migration

Migrate the root `build.gradle.kts` to use the version catalog.

- [ ] Task: Refactor root `build.gradle.kts`.
    - [ ] Replace `sharedLibs` references with `libs`.
    - [ ] Run `./gradlew help` to ensure the build script still evaluates correctly.
- [ ] Task: Conductor - User Manual Verification 'Phase 2: Root Module Migration' (Protocol in workflow.md)

## Phase 3: Module Migration (composeApp, muse, elevenlabs)

Migrate the remaining modules one by one.

- [ ] Task: Refactor `composeApp/build.gradle.kts`.
    - [ ] Replace `sharedLibs` references with `libs`.
- [ ] Task: Refactor `muse/build.gradle.kts`.
    - [ ] Replace `sharedLibs` references with `libs`.
- [ ] Task: Refactor `elevenlabs/build.gradle.kts`.
    - [ ] Replace `sharedLibs` references with `libs`.
- [ ] Task: Conductor - User Manual Verification 'Phase 3: Module Migration' (Protocol in workflow.md)

## Phase 4: Cleanup and Final Verification

Remove the now-unused `sharedLibs` and perform a full build.

- [ ] Task: Remove `sharedLibs` definition.
    - [ ] Identify where `sharedLibs` is defined (likely a `buildSrc` or a separate script file).
    - [ ] Remove the definition.
- [ ] Task: Final Build Verification.
    - [ ] Run a full clean build: `./gradlew clean assemble`.
    - [ ] Verify that all dependencies are correctly resolved.
- [ ] Task: Conductor - User Manual Verification 'Phase 4: Cleanup and Final Verification' (Protocol in workflow.md)
