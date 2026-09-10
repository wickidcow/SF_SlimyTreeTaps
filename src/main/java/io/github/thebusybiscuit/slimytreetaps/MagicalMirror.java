package io.github.thebusybiscuit.slimytreetaps;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;

public class MagicalMirror extends SimpleSlimefunItem<ItemUseHandler> implements NotPlaceable {

    private static final ItemStack ENDER_PEARL = new ItemStack(Material.ENDER_PEARL);
    private static final Title.Times MIRROR_TITLE_TIMES = Title.Times.times(
        Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofSeconds(1));

    private final TreeTaps plugin;
    private final NamespacedKey mirrorLocation;
    private final Material mirrorMaterial;
    private final Set<UUID> pendingInput = ConcurrentHashMap.newKeySet();

    public MagicalMirror(TreeTaps plugin, ItemGroup itemGroup, SlimefunItemStack item,
                         RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
        this.plugin = plugin;
        this.mirrorLocation = new NamespacedKey(plugin, "mirror_location");
        this.mirrorMaterial = item.getType();
    }

    @Override
    public ItemUseHandler getItemHandler() {
        return event -> {
            event.cancel();
            Player player = event.getPlayer();
            UUID playerId = player.getUniqueId();
            if (!pendingInput.add(playerId)) {
                player.sendMessage(Component.text(
                    "You are already naming a Magical Mirror. Type the name in chat.", NamedTextColor.YELLOW));
                return;
            }

            EquipmentSlot hand = event.getHand();
            Location destination = player.getLocation().clone();
            player.sendMessage(Component.text(
                "Type a name in chat to bind this Magical Mirror to your current location.", NamedTextColor.GREEN));

            ChatUtils.awaitInput(player, name -> player.getScheduler().execute(
                plugin,
                () -> onNameInput(player, name, hand, destination),
                () -> pendingInput.remove(playerId),
                1L
            ));
        };
    }

    private void onNameInput(Player player, String name, EquipmentSlot hand, Location destination) {
        pendingInput.remove(player.getUniqueId());
        String cleanName = name == null ? "" : ChatUtils.removeColorCodes(name).trim();
        if (cleanName.isBlank() || cleanName.startsWith("/")) {
            player.sendMessage(Component.text(
                "Mirror binding cancelled. The name cannot be blank or start with '/'.", NamedTextColor.RED));
            return;
        }
        if (cleanName.length() > 64) {
            cleanName = cleanName.substring(0, 64);
        }

        ItemStack item = getMirrorInHand(player, hand);
        if (item == null) {
            player.sendMessage(Component.text(
                "Mirror binding cancelled because the Magical Mirror is no longer in the hand you used.", NamedTextColor.RED));
            return;
        }

        setLocation(player, item, cleanName, destination);
    }

    private ItemStack getMirrorInHand(Player player, EquipmentSlot hand) {
        ItemStack item = hand == EquipmentSlot.OFF_HAND
            ? player.getInventory().getItemInOffHand()
            : player.getInventory().getItemInMainHand();
        return isMirrorItem(item) ? item : null;
    }

    void clearPendingInput(UUID playerId) {
        pendingInput.remove(playerId);
    }

    public boolean isMirrorItem(ItemStack item) {
        return item != null && item.getType() == mirrorMaterial && isItem(item);
    }

    public void teleport(Player player, ItemStack item) {
        if (!player.getInventory().containsAtLeast(ENDER_PEARL, 1)) {
            player.sendMessage(Component.text(
                "You need at least one Ender Pearl to use a Magical Mirror.", NamedTextColor.RED));
            return;
        }

        Optional<Location> destination = getLocation(item);
        if (destination.isEmpty()) {
            player.sendMessage(Component.text(
                "This Magical Mirror does not have a valid destination.", NamedTextColor.RED));
            return;
        }

        if (!player.getInventory().removeItem(new ItemStack(Material.ENDER_PEARL, 1)).isEmpty()) {
            player.sendMessage(Component.text(
                "You need at least one Ender Pearl to teleport.", NamedTextColor.RED));
            return;
        }

        player.teleportAsync(destination.get()).whenComplete((teleported, error) -> {
            boolean success = error == null && Boolean.TRUE.equals(teleported);
            if (error != null) {
                plugin.getLogger().log(Level.WARNING,
                    "Magical Mirror teleport failed for " + player.getName() + ": " + error.getMessage());
            }

            player.getScheduler().execute(
                plugin,
                () -> finishTeleport(player, item, success),
                () -> plugin.getLogger().fine("Could not finish a Magical Mirror teleport because the player entity retired."),
                1L
            );
        });
    }

    private void finishTeleport(Player player, ItemStack item, boolean success) {
        if (!player.isValid()) {
            return;
        }

        if (success) {
            player.showTitle(Title.title(
                getMirrorTitle(item),
                Component.text("- Magical Mirror -", NamedTextColor.GRAY),
                MIRROR_TITLE_TIMES
            ));
            return;
        }

        refundPearl(player);
        player.sendMessage(Component.text("Teleportation was cancelled.", NamedTextColor.RED));
    }

    private void refundPearl(Player player) {
        Map<Integer, ItemStack> leftovers = player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));
        for (ItemStack leftover : leftovers.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), leftover);
        }
    }

    private void setLocation(Player player, ItemStack item, String name, Location location) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null || location.getWorld() == null) {
            player.sendMessage(Component.text("Could not bind this Magical Mirror.", NamedTextColor.RED));
            return;
        }

        JsonObject json = new JsonObject();
        json.addProperty("world", location.getWorld().getUID().toString());
        json.addProperty("x", location.getX());
        json.addProperty("y", location.getY());
        json.addProperty("z", location.getZ());
        json.addProperty("pitch", location.getPitch());
        json.addProperty("yaw", location.getYaw());

        meta.getPersistentDataContainer().set(mirrorLocation, PersistentDataType.STRING, json.toString());
        meta.displayName(Component.text(name, NamedTextColor.AQUA));
        item.setItemMeta(meta);
        player.sendMessage(Component.text("Magical Mirror destination saved.", NamedTextColor.GREEN));
    }

    private Optional<Location> getLocation(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return Optional.empty();
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return Optional.empty();
        }

        String data = meta.getPersistentDataContainer().get(mirrorLocation, PersistentDataType.STRING);
        if (data == null) {
            return Optional.empty();
        }

        try {
            JsonObject json = JsonParser.parseString(data).getAsJsonObject();
            UUID uuid = UUID.fromString(json.get("world").getAsString());
            World world = Bukkit.getWorld(uuid);
            if (world == null) {
                return Optional.empty();
            }

            double x = json.get("x").getAsDouble();
            double y = json.get("y").getAsDouble();
            double z = json.get("z").getAsDouble();
            float yaw = json.get("yaw").getAsFloat();
            float pitch = json.get("pitch").getAsFloat();
            if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)
                    || !Float.isFinite(yaw) || !Float.isFinite(pitch)) {
                throw new IllegalArgumentException("destination contains non-finite coordinates");
            }

            return Optional.of(new Location(world, x, y, z, yaw, pitch));
        } catch (RuntimeException ex) {
            plugin.getLogger().log(Level.WARNING,
                "Ignoring invalid Magical Mirror destination data: " + ex.getMessage());
            return Optional.empty();
        }
    }

    private Component getMirrorTitle(ItemStack item) {
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.displayName() != null) {
                return meta.displayName();
            }
        }
        return Component.text("Magical Mirror", NamedTextColor.AQUA);
    }
}
