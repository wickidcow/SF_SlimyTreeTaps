package io.github.thebusybiscuit.slimytreetaps;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;

public class MagicalMirrorListener implements Listener {

    private final MagicalMirror mirror;
    private final Map<UUID, Map<UUID, Integer>> handledFrames = new ConcurrentHashMap<>();

    public MagicalMirrorListener(TreeTaps plugin, MagicalMirror mirror) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.mirror = mirror;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (!(event.getRightClicked() instanceof ItemFrame frame)) {
            return;
        }

        ItemStack item = frame.getItem();
        if (!mirror.isMirrorItem(item)) {
            return;
        }

        Player player = event.getPlayer();
        if (!Slimefun.getProtectionManager()
                .hasPermission(player, frame.getLocation(), Interaction.INTERACT_ENTITY)) {
            // Prevent rotating/removing the mirror even when a protection plugin
            // denied the custom teleport interaction.
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);

        if (isDuplicateThisTick(player, frame)) {
            return;
        }

        if (mirror.canUse(player, true)) {
            mirror.teleport(player, item);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        handledFrames.remove(uuid);
        mirror.clearPendingInput(uuid);
    }

    private boolean isDuplicateThisTick(Player player, ItemFrame frame) {
        int tick = Bukkit.getCurrentTick();
        Map<UUID, Integer> frames = handledFrames.computeIfAbsent(
                player.getUniqueId(), ignored -> new ConcurrentHashMap<>());

        UUID frameId = frame.getUniqueId();
        Integer lastTick = frames.get(frameId);
        if (lastTick != null && lastTick.intValue() == tick) {
            return true;
        }

        // A player normally interacts with only a handful of mirrors. Keep the
        // defensive cache bounded without introducing a scheduled cleanup task.
        if (lastTick == null && frames.size() >= 64) {
            frames.clear();
        }

        frames.put(frameId, tick);
        return false;
    }
}
