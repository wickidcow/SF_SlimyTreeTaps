# Changelog

## Unreleased - 1.0.0

First Slimefun Legacy release of the modernized SlimyTreeTaps fork.

### Compatibility

- Updated for Paper 26.2 and Minecraft 1.21.11+.
- Compiles against Slimefun Legacy 4.1.39.
- Built with Java 25 while targeting Java 21 bytecode.
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
- Added null-safe harvest drop placement.
- Added Slimefun protection-manager checks for both tree harvesting and Magical Mirror item frames.
- Hardened Magical Mirror item-frame interactions against duplicate interaction events.
- Made Magical Mirror interaction state thread-safe for Folia-style region scheduling.
- Routed mirror chat-input callbacks through the player entity scheduler.
- Invalid or corrupted mirror destination data is handled safely instead of bubbling exceptions through the interaction event.
- Failed mirror teleports refund the consumed Ender Pearl.

### Project modernization

- Restored English-only source, configuration, metadata, README, and issue templates.
- Preserved the original SlimyTreeTaps Slimefun item IDs and research IDs for existing worlds.
- Preserved the Magical Mirror destination key for compatibility with previously bound mirrors.
- Restored the canonical MIT license text and original copyright attribution.
- Removed dependency on the old upstream auto-updater.
- Updated GitHub Actions to current Node 24-based action releases.
- CI now cancels superseded PR builds and publishes a raw JAR artifact.
- Added tag-driven GitHub Releases with generated release notes and a raw JAR release asset.
