# Specification: Remove sharedLibs Usage

## Overview
The `sharedLibs` object is used for centralizing dependencies. This track will replace all usages of `sharedLibs` in the `build.gradle.kts` files with the local version catalog (`libs.versions.toml`). This will bring the project in line with modern Gradle practices.

## Functional Requirements
1.  **Dependency Migration:** For the `Root`, `composeApp`, `muse`, and `elevenlabs` modules, replace all dependencies that use `sharedLibs` with their counterparts in the `libs` version catalog.
2.  **Version Catalog Population:** If a dependency currently in `sharedLibs` is missing from `libs.versions.toml`, it MUST be added to the version catalog first, using the same version currently used.
3.  **Refactoring Integrity:** The dependencies themselves must not change; only the method of referencing them should be updated. The project must build successfully after the refactoring.

## Non-Functional Requirements
1.  **Maintainability:** Centralizing dependencies in the version catalog improves project maintainability and consistency.
2.  **Modernization:** This migration aligns the project with current Gradle dependency management standards.

## Acceptance Criteria
-   All `build.gradle.kts` files in the specified modules are free of `sharedLibs` references.
-   All dependencies previously managed by `sharedLibs` are now managed by `libs.versions.toml`.
-   The project compiles and builds successfully.

## Out of Scope
-   Updating dependency versions (unless already updated in the catalog).
-   General refactoring of Gradle scripts beyond dependency management.
