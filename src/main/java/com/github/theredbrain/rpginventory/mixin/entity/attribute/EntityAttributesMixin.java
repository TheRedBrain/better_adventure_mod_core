package com.github.theredbrain.rpginventory.mixin.entity.attribute;

import com.github.theredbrain.rpginventory.RPGInventory;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityAttributes.class)
public class EntityAttributesMixin {
	static {
		RPGInventory.ACTIVE_SPELL_SLOT_AMOUNT = Registry.registerReference(Registries.ATTRIBUTE, RPGInventory.identifier("generic.active_spell_slot_amount"), new ClampedEntityAttribute("attribute.name.generic.active_spell_slot_amount", 0.0, -8.0, 8.0).setTracked(true));
	}
}
