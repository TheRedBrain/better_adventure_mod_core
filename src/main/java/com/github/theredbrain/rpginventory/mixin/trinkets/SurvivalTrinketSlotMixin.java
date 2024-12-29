package com.github.theredbrain.rpginventory.mixin.trinkets;

import com.github.theredbrain.rpginventory.RPGInventory;
import com.github.theredbrain.rpginventory.entity.player.DuckPlayerEntityMixin;
import com.github.theredbrain.rpginventory.registry.GameRulesRegistry;
import com.github.theredbrain.rpginventory.screen.DuckPlayerScreenHandlerMixin;
import com.github.theredbrain.rpginventory.screen.DuckSlotMixin;
import dev.emi.trinkets.SurvivalTrinketSlot;
import dev.emi.trinkets.api.SlotGroup;
import dev.emi.trinkets.api.SlotType;
import dev.emi.trinkets.api.TrinketInventory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Mixin(value = SurvivalTrinketSlot.class)
public abstract class SurvivalTrinketSlotMixin extends Slot {

	@Shadow(remap = false)
	@Final
	private TrinketInventory trinketInventory;

	@Shadow(remap = false)
	@Final
	private SlotGroup group;

	@Shadow(remap = false)
	@Final
	private boolean alwaysVisible;

	public SurvivalTrinketSlotMixin(Inventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}

	@Inject(method = "<init>", at = @At("TAIL"), remap = false)
	public void SurvivalTrinketSlot(TrinketInventory inventory, int index, int x, int y, SlotGroup group, SlotType type, int slotOffset, boolean alwaysVisible, CallbackInfo ci) {
		addSlotTooltip(this, group.getName(), type.getName());
	}

	@Unique
	private static void addSlotTooltip(Slot slot, String groupName, String slotName) {
		List<Text> list = new ArrayList<>();
		Text text = Text.translatable("slot.tooltip." + groupName + "." + slotName);
		if (!text.getString().isEmpty()) {
			list.add(text);
			((DuckSlotMixin) slot).rpginventory$setSlotTooltipText(list);
		}
	}

	@Inject(method = "canInsert", at = @At("RETURN"), cancellable = true)
	public void rpginventory$canInsert(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {

		LivingEntity livingEntity = trinketInventory.getComponent().getEntity();
		boolean bl = false;
		if (livingEntity instanceof PlayerEntity playerEntity && playerEntity.isCreative()) {
			bl = true;
		}
		boolean bl2 = true;
		if (livingEntity.getServer() != null) {
			bl2 = livingEntity.getServer().getGameRules().getBoolean(GameRulesRegistry.CAN_CHANGE_EQUIPMENT);
		}
		Optional<RegistryEntry.Reference<StatusEffect>> civilisation_status_effect = Registries.STATUS_EFFECT.getEntry(RPGInventory.SERVER_CONFIG.statusEffects.civilisation_status_effect_identifier.get());
		boolean hasCivilisationEffect = civilisation_status_effect.isPresent() && livingEntity.hasStatusEffect(civilisation_status_effect.get());

		Optional<RegistryEntry.Reference<StatusEffect>> wilderness_status_effect = Registries.STATUS_EFFECT.getEntry(RPGInventory.SERVER_CONFIG.statusEffects.wilderness_status_effect_identifier.get());
		boolean hasWildernessEffect = wilderness_status_effect.isPresent() && livingEntity.hasStatusEffect(wilderness_status_effect.get());

		cir.setReturnValue(cir.getReturnValue() && (hasCivilisationEffect || bl || (bl2 && !hasWildernessEffect)));
	}

	/**
	 * @author TheRedBrain
	 */
	@Inject(method = "canTakeItems", at = @At("RETURN"), cancellable = true)
	public void rpginventory$canTakeItems(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
		boolean bl = true;
		if (player.getServer() != null) {
			bl = player.getServer().getGameRules().getBoolean(GameRulesRegistry.CAN_CHANGE_EQUIPMENT);
		}

		Optional<RegistryEntry.Reference<StatusEffect>> civilisation_status_effect = Registries.STATUS_EFFECT.getEntry(RPGInventory.SERVER_CONFIG.statusEffects.civilisation_status_effect_identifier.get());
		boolean hasCivilisationEffect = civilisation_status_effect.isPresent() && player.hasStatusEffect(civilisation_status_effect.get());

		Optional<RegistryEntry.Reference<StatusEffect>> wilderness_status_effect = Registries.STATUS_EFFECT.getEntry(RPGInventory.SERVER_CONFIG.statusEffects.wilderness_status_effect_identifier.get());
		boolean hasWildernessEffect = wilderness_status_effect.isPresent() && player.hasStatusEffect(wilderness_status_effect.get());

		cir.setReturnValue(cir.getReturnValue() && (hasCivilisationEffect || (bl && !hasWildernessEffect) || player.isCreative()));
	}

	@Inject(method = "isEnabled", at = @At(value = "HEAD"), cancellable = true)
	public void rpginventory$isEnabled_checkForRecipeBook(CallbackInfoReturnable<Boolean> cir) {
		if (alwaysVisible && x < 0 && trinketInventory.getComponent().getEntity() instanceof PlayerEntity player) {
			if (player.currentScreenHandler instanceof DuckPlayerScreenHandlerMixin playerScreenHandler && playerScreenHandler.rpginventory$isAttributeScreenVisible()) {
				cir.setReturnValue(false);
				cir.cancel();
			}
		}
	}

	@Inject(method = "isEnabled", at = @At(value = "RETURN"), cancellable = true)
	public void rpginventory$isEnabled_checkForSlotGroup(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(cir.getReturnValue()
				&& super.isEnabled()
				&& !((Objects.equals(this.group.getName(), "spell_slot_1") && trinketInventory.getComponent().getEntity().getAttributeValue(RPGInventory.ACTIVE_SPELL_SLOT_AMOUNT) < 1)
				|| (Objects.equals(this.group.getName(), "spell_slot_2") && trinketInventory.getComponent().getEntity().getAttributeValue(RPGInventory.ACTIVE_SPELL_SLOT_AMOUNT) < 2)
				|| (Objects.equals(this.group.getName(), "spell_slot_3") && trinketInventory.getComponent().getEntity().getAttributeValue(RPGInventory.ACTIVE_SPELL_SLOT_AMOUNT) < 3)
				|| (Objects.equals(this.group.getName(), "spell_slot_4") && trinketInventory.getComponent().getEntity().getAttributeValue(RPGInventory.ACTIVE_SPELL_SLOT_AMOUNT) < 4)
				|| (Objects.equals(this.group.getName(), "spell_slot_5") && trinketInventory.getComponent().getEntity().getAttributeValue(RPGInventory.ACTIVE_SPELL_SLOT_AMOUNT) < 5)
				|| (Objects.equals(this.group.getName(), "spell_slot_6") && trinketInventory.getComponent().getEntity().getAttributeValue(RPGInventory.ACTIVE_SPELL_SLOT_AMOUNT) < 6)
				|| (Objects.equals(this.group.getName(), "spell_slot_7") && trinketInventory.getComponent().getEntity().getAttributeValue(RPGInventory.ACTIVE_SPELL_SLOT_AMOUNT) < 7)
				|| (Objects.equals(this.group.getName(), "spell_slot_8") && trinketInventory.getComponent().getEntity().getAttributeValue(RPGInventory.ACTIVE_SPELL_SLOT_AMOUNT) < 8)
				|| (Objects.equals(this.group.getName(), "hand") && trinketInventory.getComponent().getEntity() instanceof PlayerEntity && ((DuckPlayerEntityMixin) trinketInventory.getComponent().getEntity()).rpginventory$isHandStackSheathed())
				|| (Objects.equals(this.group.getName(), "sheathed_hand") && trinketInventory.getComponent().getEntity() instanceof PlayerEntity && !((DuckPlayerEntityMixin) trinketInventory.getComponent().getEntity()).rpginventory$isHandStackSheathed())
				|| (Objects.equals(this.group.getName(), "sheathed_offhand") && trinketInventory.getComponent().getEntity() instanceof PlayerEntity && !((DuckPlayerEntityMixin) trinketInventory.getComponent().getEntity()).rpginventory$isOffhandStackSheathed())
		));
	}

	/**
	 * @author TheRedBrain
	 */
	@Inject(method = "isTrinketFocused", at = @At("HEAD"), cancellable = true, remap = false)
	public void rpginventory$isTrinketFocused(CallbackInfoReturnable<Boolean> cir) {
		if ((Objects.equals(this.group.getName(), "spell_slot_1") && trinketInventory.getComponent().getEntity().getAttributeValue(RPGInventory.ACTIVE_SPELL_SLOT_AMOUNT) < 1)
				|| (Objects.equals(this.group.getName(), "spell_slot_2") && trinketInventory.getComponent().getEntity().getAttributeValue(RPGInventory.ACTIVE_SPELL_SLOT_AMOUNT) < 2)
				|| (Objects.equals(this.group.getName(), "spell_slot_3") && trinketInventory.getComponent().getEntity().getAttributeValue(RPGInventory.ACTIVE_SPELL_SLOT_AMOUNT) < 3)
				|| (Objects.equals(this.group.getName(), "spell_slot_4") && trinketInventory.getComponent().getEntity().getAttributeValue(RPGInventory.ACTIVE_SPELL_SLOT_AMOUNT) < 4)
				|| (Objects.equals(this.group.getName(), "spell_slot_5") && trinketInventory.getComponent().getEntity().getAttributeValue(RPGInventory.ACTIVE_SPELL_SLOT_AMOUNT) < 5)
				|| (Objects.equals(this.group.getName(), "spell_slot_6") && trinketInventory.getComponent().getEntity().getAttributeValue(RPGInventory.ACTIVE_SPELL_SLOT_AMOUNT) < 6)
				|| (Objects.equals(this.group.getName(), "spell_slot_7") && trinketInventory.getComponent().getEntity().getAttributeValue(RPGInventory.ACTIVE_SPELL_SLOT_AMOUNT) < 7)
				|| (Objects.equals(this.group.getName(), "spell_slot_8") && trinketInventory.getComponent().getEntity().getAttributeValue(RPGInventory.ACTIVE_SPELL_SLOT_AMOUNT) < 8)
				|| (Objects.equals(this.group.getName(), "hand") && trinketInventory.getComponent().getEntity() instanceof PlayerEntity && ((DuckPlayerEntityMixin) trinketInventory.getComponent().getEntity()).rpginventory$isHandStackSheathed())
				|| (Objects.equals(this.group.getName(), "sheathed_hand") && trinketInventory.getComponent().getEntity() instanceof PlayerEntity && !((DuckPlayerEntityMixin) trinketInventory.getComponent().getEntity()).rpginventory$isHandStackSheathed())
				|| (Objects.equals(this.group.getName(), "sheathed_offhand") && trinketInventory.getComponent().getEntity() instanceof PlayerEntity && !((DuckPlayerEntityMixin) trinketInventory.getComponent().getEntity()).rpginventory$isOffhandStackSheathed())
		) {
			cir.setReturnValue(false);
			cir.cancel();
		}
	}
}
