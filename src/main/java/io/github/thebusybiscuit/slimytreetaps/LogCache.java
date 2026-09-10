package io.github.thebusybiscuit.slimytreetaps;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;

/**
 * Immutable lookup cache for tappable logs and their stripped variants.
 *
 * <p>The old Albion-compatible snapshot built this once during startup instead
 * of constructing stripped-material names on every click. Keeping the cache
 * also avoids exceptions if a future log-like material has no stripped form.</p>
 */
public final class LogCache {

    private static final Snapshot EMPTY = new Snapshot(Collections.emptySet(), Collections.emptyMap());
    private static volatile Snapshot snapshot = EMPTY;

    private LogCache() {}

    public static void init(Iterable<Material> materials) {
        EnumSet<Material> tappable = EnumSet.noneOf(Material.class);
        EnumMap<Material, Material> stripped = new EnumMap<>(Material.class);

        for (Material material : materials) {
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
            Collections.unmodifiableMap(stripped)
        );
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
