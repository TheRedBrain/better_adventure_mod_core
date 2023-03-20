package com.github.theredbrain.rpgmod.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public interface DuckItemMixin {
    public TypedActionResult<ItemStack> adventureUse(World world, PlayerEntity user);
}
