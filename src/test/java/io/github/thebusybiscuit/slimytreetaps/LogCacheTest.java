package io.github.thebusybiscuit.slimytreetaps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.bukkit.Material;
import org.junit.jupiter.api.Test;

class LogCacheTest {

    @Test
    void cachesNormalAndPaleOakLogs() {
        LogCache.init(List.of(Material.OAK_LOG, Material.PALE_OAK_LOG, Material.STRIPPED_OAK_LOG));

        assertTrue(LogCache.isTappable(Material.OAK_LOG));
        assertTrue(LogCache.isTappable(Material.PALE_OAK_LOG));
        assertFalse(LogCache.isTappable(Material.STRIPPED_OAK_LOG));

        assertEquals(Material.STRIPPED_OAK_LOG, LogCache.strippedVariant(Material.OAK_LOG));
        assertEquals(Material.STRIPPED_PALE_OAK_LOG, LogCache.strippedVariant(Material.PALE_OAK_LOG));
        assertNull(LogCache.strippedVariant(Material.STRIPPED_OAK_LOG));
    }

    @Test
    void ignoresMaterialsWithoutStrippedVariants() {
        LogCache.init(List.of(Material.STONE, Material.OAK_LOG));

        assertFalse(LogCache.isTappable(Material.STONE));
        assertNull(LogCache.strippedVariant(Material.STONE));
        assertTrue(LogCache.tappableLogs().contains(Material.OAK_LOG));
    }

    @Test
    void replacesTheSnapshotWhenReinitialized() {
        LogCache.init(List.of(Material.OAK_LOG));
        assertTrue(LogCache.isTappable(Material.OAK_LOG));

        LogCache.init(List.of(Material.BIRCH_LOG));

        assertFalse(LogCache.isTappable(Material.OAK_LOG));
        assertTrue(LogCache.isTappable(Material.BIRCH_LOG));
        assertEquals(Material.STRIPPED_BIRCH_LOG, LogCache.strippedVariant(Material.BIRCH_LOG));
    }
}
