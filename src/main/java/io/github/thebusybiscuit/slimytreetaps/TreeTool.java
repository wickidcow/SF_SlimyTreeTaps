package io.github.thebusybiscuit.slimytreetaps;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Orientable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.DamageableItem;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import me.mrCookieSlime.Slimefun.api.BlockStorage;

public class TreeTool extends SimpleSlimefunItem<ItemUseHandler> implements NotPlaceable, DamageableItem {

    private final int chance;
    private final ItemStack output;
    private final ItemStack paleOakOutput;

    public TreeTool(ItemGroup itemGroup, SlimefunItemStack item, int chance, ItemStack output, ItemStack[] recipe) {
        this(itemGroup, item, chance, output, null, recipe);
    }

    public TreeTool(ItemGroup itemGroup, SlimefunItemStack item, int chance, ItemStack output,
                    ItemStack paleOakOutput, ItemStack[] recipe) {
        super(itemGroup, item, RecipeType.ENHANCED_CRAFTING_TABLE, recipe);
        this.chance = Math.max(0, Math.min(100, chance));
        this.output = output.clone();
        this.paleOakOutput = paleOakOutput == null ? null : paleOakOutput.clone();
    }

    @Override
    public ItemUseHandler getItemHandler() {
        return e -> e.getClickedBlock().ifPresent(block ->
            harvest(e.getPlayer(), block, e.getClickedFace(), e.getItem())
        );
    }

    private void harvest(Player player, Block block, BlockFace clickedFace, ItemStack tool) {
        Material original = block.getType();
        if (!LogCache.isTappable(original) || BlockStorage.hasBlockInfo(block.getLocation())) {
            return;
        }
        if (!Slimefun.getProtectionManager().hasPermission(player, block, Interaction.BREAK_BLOCK)) {
            return;
        }

        BlockData originalData = block.getBlockData();
        block.getWorld().playSound(block.getLocation(), originalData.getSoundGroup().getHitSound(), 1.0F, 1.0F);

        if (ThreadLocalRandom.current().nextInt(100) < chance) {
            Material stripped = LogCache.strippedVariant(original);
            if (stripped == null) {
                return;
            }

            stripLog(block, stripped, originalData);
            ItemStack drop = isPaleOak(original) && paleOakOutput != null ? paleOakOutput.clone() : output.clone();
            Location dropAt = clickedFace == null
                ? block.getLocation().add(0.5, 0.5, 0.5)
                : block.getRelative(clickedFace).getLocation().add(0.5, 0.5, 0.5);
            block.getWorld().dropItem(dropAt, drop);
        }

        damageItem(player, tool);
    }

    private void stripLog(Block block, Material stripped, BlockData originalData) {
        block.setType(stripped, false);
        if (originalData instanceof Orientable originalOrientable
                && block.getBlockData() instanceof Orientable strippedOrientable) {
            strippedOrientable.setAxis(originalOrientable.getAxis());
            block.setBlockData(strippedOrientable, false);
        }
    }

    static boolean isPaleOak(Material material) {
        return material == Material.PALE_OAK_LOG || material == Material.PALE_OAK_WOOD;
    }

    @Override
    public boolean isDamageable() {
        return true;
    }
}
