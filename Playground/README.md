### Playground

This module serves as an experimental desktop application (Compose Multiplatform) for testing and prototyping UI components and core features before integrating them into the main app.

#### Current Features
- **Caption Rendering:** Drawing text and captions on a Canvas using `DrawScope` without being tied to a Composable context.
- **Selection UI:** Interactive selection box UI components for transforming elements (e.g., scale, delete actions).
- **Export Capabilities:** Exporting the Canvas content to a PNG image using Skia (`org.jetbrains.skia.Image`) and native desktop file dialogs (`java.awt.FileDialog`).

#### Run
```bash
./gradlew :Playground:run
```

#### Build installer for your OS
```bash
./gradlew :Playground:packageDistributionForCurrentOS
```