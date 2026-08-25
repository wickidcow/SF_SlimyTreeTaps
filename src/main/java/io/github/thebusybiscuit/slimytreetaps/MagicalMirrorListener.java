package io.github.thebusybiscuit.slimytreetaps;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class MagicalMirrorListener implements Listener {

    private final MagicalMirror mirror;
    private final Map<UUID, Map<Integer, Integer>> handledFrames = new HashMap<>();

    public MagicalMirrorListener(TreeTaps plugin, MagicalMirror mirror) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.mirror = mirror;
    }

    @EventHandler(ignoreCancelled = true)
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

        event.setCancelled(true);

        if (isDuplicateThisTick(event.getPlayer(), frame)) {
            return;
        }

        if (mirror.canUse(event.getPlayer(), true)) {
            mirror.teleport(event.getPlayer(), item);
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
        Map<Integer, Integer> frames =
                handledFrames.computeIfAbsent(player.getUniqueId(), ignored -> new HashMap<>());
        Integer lastTick = frames.get(frame.getEntityId());

        if (lastTick != null && lastTick == tick) {
            return true;
        }

        if (lastTick == null && frames.size() >= 64) {
            frames.clear();
        }

        frames.put(frame.getEntityId(), tick);
        return false;
    }
}
