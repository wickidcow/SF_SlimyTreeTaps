package io.github.thebusybiscuit.slimytreetaps;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;

/**
 * Immutable startup snapshot of tappable logs and their stripped variants.
 *
 * <p>The cache is built from the server's live {@code Tag.LOGS} contents during
 * plugin startup. Tree tapping is a hot interaction path, so this avoids doing
 * repeated tag lookups, string prefix checks and material-name reconstruction
 * every time a player uses a tap.</p>
 */
public final class LogCache {

    private static final Snapshot EMPTY = new Snapshot(
            Collections.emptySet(),
            Collections.emptyMap());

    private static volatile Snapshot snapshot = EMPTY;

    private LogCache() {}

    public static void init(Iterable<Material> logs) {
        EnumSet<Material> tappable = EnumSet.noneOf(Material.class);
        EnumMap<Material, Material> stripped = new EnumMap<>(Material.class);

        for (Material material : logs) {
            if (material == null || material.name().startsWith("STRIPPED_")) {
                continue;
            }

            Material strippedVariant = Material.getMaterial("STRIPPED_" + material.name());
            if (strippedVariant != null) {
                tappable.add(material);
                stripped.put(material, strippedVariant);
            }
        }

        snapshot = new Snapshot(
                Collections.unmodifiableSet(tappable),
                Collections.unmodifiableMap(stripped));
    }

    public static boolean isTappable(Material material) {
        return material != null && snapshot.tappableLogs().contains(material);
    }

    public static Material strippedVariant(Material material) {
        return snapshot.strippedVariants().get(material);
    }

    public static Set<Material> tappableLogs() {
        return snapshot.tappableLogs();
    }

    private record Snapshot(Set<Material> tappableLogs, Map<Material, Material> strippedVariants) {}
}
