# SF_SlimyTreeTaps

A modern continuation of **SlimyTreeTaps** for **Slimefun Legacy**.

SlimyTreeTaps was originally created by **TheBusyBiscuit** and was inspired by the classic IndustrialCraft 2 tree-tap progression. It adds an alternate path to Slimefun plastic production through resin, rubber, and raw plastic, along with Amber and Magical Mirrors.

This fork keeps the original item and research IDs intact while updating the addon for modern Paper-based servers.

## Compatibility

- Minecraft 1.21.11+
- Paper 26.2
- Purpur 26.2
- Folia 26.2 experimental
- Java 21+ bytecode, built with Java 25
- Slimefun Legacy 4.1.39+

## Modernization

The Legacy fork focuses on compatibility and preserving existing worlds rather than replacing the original progression.

Current work includes:

- Paper 26.2 and Slimefun Legacy build target
- English-only source, configuration, and player-facing text
- Hardened Magical Mirror interactions and teleport handling
- Folia-safe mirror state tracking and entity-scheduler callbacks
- Slimefun protection checks for tree harvesting and Magical Mirror item frames
- Cached log/stripped-log mappings for the tree-tapping hot path
- Modern Slimefun storage-cache checks instead of the legacy BlockStorage API
- Pale Oak support
- Vanilla `RESIN_CLUMP` integration
- Pale Oak Resin Extractor recipes
- Vanilla Resin Clumps, Resin Blocks, Resin Bricks, and Resin Bricks blocks as Rubber Factory inputs
- Resin-value-preserving bulk recipes so compacted vanilla resin does not change production balance
- Existing SlimyTreeTaps item IDs and research IDs preserved

### Vanilla Resin integration

Normal logs continue to produce SlimyTreeTaps **Sticky Resin**.

Pale Oak can instead produce Minecraft's vanilla **Resin Clumps**, which can be processed in the Rubber Factory. Resin Extractors also recognize Pale Oak and can automate the vanilla-resin branch.

The Rubber Factory accepts vanilla resin in all common storage/processed forms. Bulk conversions are calculated from the configured `rubber-recipe-clumps` value so Resin Blocks and Resin Bricks keep the same underlying resin value and production rate as loose Resin Clumps.

This integration can be disabled or rebalanced in `config.yml`.

## Protection support

Tree harvesting and Magical Mirror item-frame use are checked through Slimefun's protection manager. This allows the addon to honor the protection integrations supplied by Slimefun Legacy, including supported Towny, WorldGuard, GriefPrevention, Lands, and similar protection providers.

A denied Magical Mirror interaction is cancelled without rotating or removing the framed mirror.

## Upgrading from older SlimyTreeTaps

The Legacy fork intentionally preserves the original Slimefun item IDs, research IDs, plugin name, and Magical Mirror destination key. Existing SlimyTreeTaps items and bound mirrors are therefore expected to remain recognizable without an item migration.

As with any addon replacement, back up the server before swapping JARs and test the upgrade on a copy of the world first.

## Original project

SlimyTreeTaps was created by TheBusyBiscuit and later maintained by members of the Slimefun community. This fork exists to keep that work usable with Slimefun Legacy and current Minecraft server software.

Project lineage and modernization references:

- TheBusyBiscuit / SlimyTreeTaps
- Slimefun-Addon-Community / SlimyTreeTaps
- Slimefun-Reloaded / SlimyTreeTaps
- Quotidietium / SlimyTreeTaps-1.21.11 (modern interaction and log-cache work used as a reference)
- wickidcow / SF_SlimyTreeTaps

## License

SlimyTreeTaps is licensed under the MIT License. See `LICENSE`.

This project is not affiliated with Mojang Studios or Microsoft.
