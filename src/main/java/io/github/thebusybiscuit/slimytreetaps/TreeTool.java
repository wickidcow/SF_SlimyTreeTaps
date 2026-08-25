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

import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.DamageableItem;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;

public class TreeTool extends SimpleSlimefunItem<ItemUseHandler> implements NotPlaceable, DamageableItem {

    private final int chance;
    private final ItemStack output;
    private final ItemStack paleOakOutput;

    public TreeTool(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            int chance,
            ItemStack output,
            ItemStack[] recipe) {
        this(itemGroup, item, chance, output, null, recipe);
    }

    public TreeTool(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            int chance,
            ItemStack output,
            ItemStack paleOakOutput,
            ItemStack[] recipe) {
        super(itemGroup, item, RecipeType.ENHANCED_CRAFTING_TABLE, recipe);
        this.chance = Math.max(0, Math.min(100, chance));
        this.output = output.clone();
        this.paleOakOutput = paleOakOutput == null ? null : paleOakOutput.clone();
    }

    @Override
    public ItemUseHandler getItemHandler() {
        return event -> event.getClickedBlock().ifPresent(block ->
                harvest(event.getPlayer(), block, event.getClickedFace(), event.getItem()));
    }

    private void harvest(Player player, Block block, BlockFace clickedFace, ItemStack tool) {
        Material original = block.getType();
        if (!LogCache.isTappable(original)) {
            return;
        }

        Location blockLocation = block.getLocation();

        // Keep the hot path cheap: cached material check first, then cached
        // Slimefun occupancy, and only then the protection-provider lookup.
        if (StorageCacheUtils.hasSlimefunBlock(blockLocation)
                || !Slimefun.getProtectionManager().hasPermission(player, blockLocation, Interaction.BREAK_BLOCK)) {
            return;
        }

        block.getWorld().playSound(
                blockLocation, block.getBlockData().getSoundGroup().getHitSound(), 1.0F, 1.0F);

        if (ThreadLocalRandom.current().nextInt(100) < chance) {
            Material stripped = LogCache.strippedVariant(original);
            if (stripped == null) {
                return;
            }

            // Tree tapping changes a real world block. Report the old and new
            // states through Dough so CoreProtect/LogBlock integrations can
            // audit and roll back the change like a normal block action.
            Slimefun.getProtectionManager().logAction(player, block, Interaction.BREAK_BLOCK);
            stripLog(block, stripped);
            Slimefun.getProtectionManager().logAction(player, block, Interaction.PLACE_BLOCK);

            ItemStack drop = isPaleOak(original) && paleOakOutput != null
                    ? paleOakOutput.clone()
                    : output.clone();

            Location dropLocation;
            if (clickedFace == null) {
                dropLocation = blockLocation.clone().add(0.5, 0.5, 0.5);
            } else {
                dropLocation = block.getRelative(clickedFace).getLocation().add(0.5, 0.5, 0.5);
            }

            block.getWorld().dropItem(dropLocation, drop);
        }

        damageItem(player, tool);
    }

    private void stripLog(Block block, Material strippedType) {
        BlockData originalData = block.getBlockData();
        block.setType(strippedType, false);

        if (originalData instanceof Orientable originalOrientable) {
            BlockData strippedData = block.getBlockData();
            if (strippedData instanceof Orientable strippedOrientable) {
                strippedOrientable.setAxis(originalOrientable.getAxis());
                block.setBlockData(strippedOrientable, false);
            }
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
