package io.github.thebusybiscuit.slimytreetaps;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND || !(event.getRightClicked() instanceof ItemFrame frame)) {
            return;
        }

        ItemStack item = frame.getItem();
        if (!mirror.isMirrorItem(item)) {
            return;
        }

        Player player = event.getPlayer();
        event.setCancelled(true);
        if (!Slimefun.getProtectionManager().hasPermission(player, frame.getLocation(), Interaction.INTERACT_ENTITY)) {
            return;
        }
        if (isDuplicateThisTick(player, frame)) {
            return;
        }
        if (mirror.canUse(player, true)) {
            mirror.teleport(player, item);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        handledFrames.remove(playerId);
        mirror.clearPendingInput(playerId);
    }

    private boolean isDuplicateThisTick(Player player, ItemFrame frame) {
        int tick = Bukkit.getCurrentTick();
        Map<UUID, Integer> perPlayer = handledFrames.computeIfAbsent(
            player.getUniqueId(), ignored -> new ConcurrentHashMap<>());
        UUID frameId = frame.getUniqueId();
        Integer previous = perPlayer.get(frameId);
        if (previous != null && previous == tick) {
            return true;
        }
        if (previous == null && perPlayer.size() >= 64) {
            perPlayer.clear();
        }
        perPlayer.put(frameId, tick);
        return false;
    }
}
