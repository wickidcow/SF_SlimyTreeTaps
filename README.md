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
- Safer tree stripping without assuming every log has a matching material name
- Pale Oak support
- Vanilla `RESIN_CLUMP` integration
- Pale Oak Resin Extractor recipes
- Vanilla Resin Clumps as an alternate Rubber Factory input
- Existing SlimyTreeTaps item IDs and research IDs preserved

### Vanilla Resin integration

Normal logs continue to produce SlimyTreeTaps **Sticky Resin**.

Pale Oak can instead produce Minecraft's vanilla **Resin Clumps**, which can be processed in the Rubber Factory. Resin Extractors also recognize Pale Oak and can automate the vanilla-resin branch.

This integration can be disabled or rebalanced in `config.yml`.

## Original project

SlimyTreeTaps was created by TheBusyBiscuit and later maintained by members of the Slimefun community. This fork exists to keep that work usable with Slimefun Legacy and current Minecraft server software.

Original project lineage:

- TheBusyBiscuit / SlimyTreeTaps
- Slimefun-Addon-Community / SlimyTreeTaps
- Slimefun-Reloaded / SlimyTreeTaps
- wickidcow / SF_SlimyTreeTaps

## License

SlimyTreeTaps is licensed under the MIT License. See `LICENSE`.

This project is not affiliated with Mojang Studios or Microsoft.
