package com.github.theredbrain.rpginventory.mixin.screen;

import com.github.theredbrain.rpginventory.RPGInventory;
import com.github.theredbrain.rpginventory.registry.GameRulesRegistry;
import com.github.theredbrain.rpginventory.registry.Tags;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(targets = {"net/minecraft/screen/slot/ArmorSlot"})
public class ArmorSlotMixin {

	@Shadow
	@Final
	private LivingEntity entity;

	@Shadow
	@Final
	private EquipmentSlot equipmentSlot;

	@Inject(method = "canInsert", at = @At("RETURN"), cancellable = true)
	public void rpginventory$canInsert(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		boolean bl = true;
		if (this.entity.getServer() != null) {
			bl = this.entity.getServer().getGameRules().getBoolean(GameRulesRegistry.CAN_CHANGE_EQUIPMENT);
		}

		Optional<RegistryEntry.Reference<StatusEffect>> civilisation_status_effect = Registries.STATUS_EFFECT.getEntry(RPGInventory.SERVER_CONFIG.statusEffects.civilisation_status_effect_identifier.get());
		boolean hasCivilisationEffect = civilisation_status_effect.isPresent() && this.entity.hasStatusEffect(civilisation_status_effect.get());

		Optional<RegistryEntry.Reference<StatusEffect>> wilderness_status_effect = Registries.STATUS_EFFECT.getEntry(RPGInventory.SERVER_CONFIG.statusEffects.wilderness_status_effect_identifier.get());
		boolean hasWildernessEffect = wilderness_status_effect.isPresent() && this.entity.hasStatusEffect(wilderness_status_effect.get());

		cir.setReturnValue((cir.getReturnValue() || rpginventory$isOfEquipmentTag(stack, this.equipmentSlot)) && (hasCivilisationEffect || (this.entity instanceof PlayerEntity player && player.isCreative()) || (bl && !hasWildernessEffect)));
	}

	@Unique
	private boolean rpginventory$isOfEquipmentTag(ItemStack itemStack, EquipmentSlot slot) {
		return switch (slot) {
			case FEET -> itemStack.isIn(Tags.BOOTS);
			case LEGS -> itemStack.isIn(Tags.LEGGINGS);
			case CHEST -> itemStack.isIn(Tags.CHEST_PLATES);
			case HEAD -> itemStack.isIn(Tags.HELMETS);
			default -> false;
		};
	}
}
