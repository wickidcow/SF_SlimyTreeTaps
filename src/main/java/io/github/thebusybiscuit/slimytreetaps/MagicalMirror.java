package io.github.thebusybiscuit.slimytreetaps;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
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
    private static final Title.Times MIRROR_TITLE_TIMES = Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofSeconds(1));

    private final TreeTaps plugin;
    private final NamespacedKey mirrorLocation;

    public MagicalMirror(TreeTaps plugin, ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
        this.plugin = plugin;
        mirrorLocation = new NamespacedKey(plugin, "mirror_location");
    }

    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            e.cancel();
            e.getPlayer().sendMessage(Component.text("Name this location by typing a name in chat.", NamedTextColor.GREEN));
            ChatUtils.awaitInput(e.getPlayer(), name -> setLocation(e.getPlayer(), e.getItem(), name, e.getPlayer().getLocation()));
        };
    }

    public void teleport(Player player, ItemStack item) {
        if (!player.getInventory().containsAtLeast(ENDER_PEARL, 1)) {
            player.sendMessage(Component.text("You need at least one Ender Pearl to use the Magical Mirror.", NamedTextColor.RED));
            return;
        }

        Optional<Location> location = getLocation(item);
        if (location.isEmpty()) {
            player.sendMessage(Component.text("This Magical Mirror does not have a valid destination.", NamedTextColor.RED));
            return;
        }

        if (!player.getInventory().removeItem(ENDER_PEARL.clone()).isEmpty()) {
            player.sendMessage(Component.text("You need at least one Ender Pearl to teleport.", NamedTextColor.RED));
            return;
        }

        player.teleportAsync(location.get()).thenAccept(hasTeleported -> player.getScheduler().execute(plugin, () -> {
            if (!player.isValid()) {
                return;
            }

            if (hasTeleported) {
                player.showTitle(Title.title(getMirrorTitle(item), Component.text("- Magical Mirror -", NamedTextColor.GRAY), MIRROR_TITLE_TIMES));
            } else {
                player.getInventory().addItem(ENDER_PEARL.clone());
                player.sendMessage(Component.text("Teleportation was cancelled.", NamedTextColor.RED));
            }
        }, () -> { }, 1L));
    }

    private void setLocation(Player player, ItemStack item, String name, Location location) {
        ItemMeta meta = item.getItemMeta();

        JsonObject json = new JsonObject();
        json.addProperty("world", location.getWorld().getUID().toString());
        json.addProperty("x", location.getX());
        json.addProperty("y", location.getY());
        json.addProperty("z", location.getZ());
        json.addProperty("pitch", location.getPitch());
        json.addProperty("yaw", location.getYaw());

        meta.getPersistentDataContainer().set(mirrorLocation, PersistentDataType.STRING, json.toString());
        meta.displayName(Component.text(ChatUtils.removeColorCodes(name), NamedTextColor.AQUA));
        item.setItemMeta(meta);
        player.sendMessage(Component.text("Magical Mirror destination saved.", NamedTextColor.GREEN));
    }

    private Optional<Location> getLocation(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        String data = meta.getPersistentDataContainer().get(mirrorLocation, PersistentDataType.STRING);
        if (data == null) {
            return Optional.empty();
        }

        JsonObject json = JsonParser.parseString(data).getAsJsonObject();
        UUID uuid = UUID.fromString(json.get("world").getAsString());
        World world = Bukkit.getWorld(uuid);
        if (world == null) {
            return Optional.empty();
        }

        return Optional.of(new Location(
                world,
                json.get("x").getAsDouble(),
                json.get("y").getAsDouble(),
                json.get("z").getAsDouble(),
                json.get("yaw").getAsFloat(),
                json.get("pitch").getAsFloat()));
    }

    private Component getMirrorTitle(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            return meta.displayName();
        }
        return Component.text("Magical Mirror", NamedTextColor.AQUA);
    }
}
