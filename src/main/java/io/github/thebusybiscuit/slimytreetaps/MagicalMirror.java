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
            e.getPlayer().sendMessage(Component.text("请为这个位置命名！直接在聊天栏输入名称。", NamedTextColor.GREEN));
            ChatUtils.awaitInput(e.getPlayer(), name -> setLocation(e.getPlayer(), e.getItem(), name, e.getPlayer().getLocation()));
        };
    }

    public void teleport(Player p, ItemStack item) {
        if (!p.getInventory().containsAtLeast(ENDER_PEARL.clone(), 1)) {
            p.sendMessage(Component.text("你至少需要一颗末影珍珠才能使用魔法镜！", NamedTextColor.RED));
            return;
        }

        Optional<Location> location = getLocation(item);

        if (location.isPresent()) {
            if (p.getInventory().removeItem(ENDER_PEARL.clone()).isEmpty()) {
                p.teleportAsync(location.get()).thenAccept(hasTeleported -> p.getScheduler().execute(plugin, () -> {
                    if (!p.isValid()) {
                        return;
                    }

                    if (hasTeleported.booleanValue()) {
                        p.showTitle(Title.title(getMirrorTitle(item), Component.text("- 魔法镜 -", NamedTextColor.GRAY), MIRROR_TITLE_TIMES));
                    } else {
                        p.getInventory().addItem(ENDER_PEARL.clone());
                        p.sendMessage(Component.text("传送已取消！", NamedTextColor.RED));
                    }
                }, () -> { }, 1L));
            } else {
                p.sendMessage(Component.text("你至少需要一颗末影珍珠才能传送！", NamedTextColor.RED));
            }
        } else {
            p.sendMessage(Component.text("这个魔法镜似乎没有有效目的地！", NamedTextColor.RED));
        }
    }

    private void setLocation(Player p, ItemStack item, String name, Location l) {
        ItemMeta meta = item.getItemMeta();

        JsonObject json = new JsonObject();
        json.addProperty("world", l.getWorld().getUID().toString());
        json.addProperty("x", l.getX());
        json.addProperty("y", l.getY());
        json.addProperty("z", l.getZ());
        json.addProperty("pitch", l.getPitch());
        json.addProperty("yaw", l.getYaw());

        meta.getPersistentDataContainer().set(mirrorLocation, PersistentDataType.STRING, json.toString());
        meta.displayName(Component.text(ChatUtils.removeColorCodes(name), NamedTextColor.AQUA));
        item.setItemMeta(meta);
        p.sendMessage(Component.text("已成功设置魔法镜位置！", NamedTextColor.GREEN));
    }

    private Optional<Location> getLocation(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        String data = meta.getPersistentDataContainer().get(mirrorLocation, PersistentDataType.STRING);

        if (data != null) {
            JsonObject json = JsonParser.parseString(data).getAsJsonObject();
            UUID uuid = UUID.fromString(json.get("world").getAsString());
            World world = Bukkit.getWorld(uuid);

            if (world != null) {
                double x = json.get("x").getAsDouble();
                double y = json.get("y").getAsDouble();
                double z = json.get("z").getAsDouble();
                float pitch = json.get("pitch").getAsFloat();
                float yaw = json.get("yaw").getAsFloat();
                Location loc = new Location(world, x, y, z, yaw, pitch);

                return Optional.of(loc);
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    private Component getMirrorTitle(ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        if (meta != null && meta.hasDisplayName()) {
            return meta.displayName();
        }

        return Component.text("魔法镜", NamedTextColor.AQUA);
    }

}
