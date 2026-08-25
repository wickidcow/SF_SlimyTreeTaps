# Changelog

## Unreleased - 1.0.0

First Slimefun Legacy release of the modernized SlimyTreeTaps fork.

### Compatibility

- Updated for Paper 26.2 and Minecraft 1.21.11+.
- Compiles against Slimefun Legacy 4.1.39.
- Requires Java 25, matching Paper 26.2's Java 25 API/runtime baseline.
- CI verifies that the produced addon uses Java 25 class-file version 69.
- Purpur support follows Paper compatibility.
- Folia support is enabled and remains experimental pending wider runtime testing.

### Vanilla Resin

- Added explicit Pale Oak support.
- Tree Taps can produce vanilla Resin Clumps from Pale Oak when vanilla resin integration is enabled.
- Resin Extractors recognize Pale Oak and can produce vanilla Resin Clumps.
- Rubber Factory accepts Resin Clumps, Resin Blocks, Resin Bricks, and Resin Bricks blocks.
- Bulk vanilla-resin recipes preserve equivalent resin value and processing rate.
- Added configuration for Pale Oak output, extractor output, and resin-to-rubber balance.

### Performance and safety

- Added a startup cache for tappable logs and stripped-log mappings.
- Replaced deprecated BlockStorage checks in the tree-tapping hot path with the modern Slimefun storage cache API.
- Tree tapping now preserves the original log axis when converting a log to its stripped variant.
- Added null-safe harvest drop placement.
- Added Slimefun protection-manager checks for both tree harvesting and Magical Mirror item frames.
- Tree-tap block changes are reported through Slimefun's protection loggers so supported CoreProtect/LogBlock installations can audit the change.
- Hardened Magical Mirror item-frame interactions against duplicate interaction events.
- Made Magical Mirror interaction state thread-safe for Folia-style region scheduling.
- Routed mirror chat-input and teleport-completion callbacks through the player entity scheduler.
- Invalid or corrupted mirror destination data, including non-finite coordinates, is handled safely instead of bubbling exceptions through the interaction event.
- Failed or exceptional mirror teleports refund the consumed Ender Pearl while the player remains available.

### Project modernization

- Restored English-only source, configuration, metadata, README, and issue templates.
- Preserved the original SlimyTreeTaps Slimefun item IDs and research IDs for existing worlds.
- Preserved the Magical Mirror destination key for compatibility with previously bound mirrors.
- Restored the canonical MIT license text and original copyright attribution.
- Removed dependency on the old upstream auto-updater.
- Updated GitHub Actions to current Node 24-based action releases.
- CI now cancels superseded PR builds and publishes a raw JAR artifact.
- Added JUnit coverage for the cached log mapping behavior.
- Added tag-driven GitHub Releases with generated release notes and a raw JAR release asset.
