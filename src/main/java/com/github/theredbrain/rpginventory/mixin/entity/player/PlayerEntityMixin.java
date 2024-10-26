package com.github.theredbrain.rpginventory.mixin.entity.player;

import com.github.theredbrain.rpginventory.RPGInventory;
import com.github.theredbrain.rpginventory.entity.DuckLivingEntityMixin;
import com.github.theredbrain.rpginventory.entity.RendersSheathedWeapons;
import com.github.theredbrain.rpginventory.entity.player.DuckPlayerEntityMixin;
import com.github.theredbrain.rpginventory.entity.player.DuckPlayerInventoryMixin;
import com.github.theredbrain.rpginventory.registry.GameRulesRegistry;
import com.github.theredbrain.rpginventory.registry.Tags;
import com.github.theredbrain.rpginventory.util.ItemUtils;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements DuckPlayerEntityMixin, DuckLivingEntityMixin, RendersSheathedWeapons {

	@Shadow
	@Final
	private PlayerInventory inventory;

	@Shadow
	@Final
	public PlayerScreenHandler playerScreenHandler;

	@Shadow
	public abstract PlayerInventory getInventory();

	@Shadow
	public abstract ItemStack getEquippedStack(EquipmentSlot slot);

	@Shadow
	public abstract boolean isCreative();

	@Unique
	private boolean isAdventureHotbarCleanedUp = false;

	@Unique
	private static final TrackedData<Boolean> IS_HAND_STACK_SHEATHED = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

	@Unique
	private static final TrackedData<Boolean> IS_OFFHAND_STACK_SHEATHED = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

	@Unique
	private static final TrackedData<Integer> OLD_ACTIVE_SPELL_SLOT_AMOUNT = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "initDataTracker", at = @At("RETURN"))
	protected void rpginventory$initDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
		builder.add(IS_HAND_STACK_SHEATHED, false);
		builder.add(IS_OFFHAND_STACK_SHEATHED, false);
		builder.add(OLD_ACTIVE_SPELL_SLOT_AMOUNT, -1);

	}

	@Inject(method = "tick", at = @At("TAIL"))
	public void rpginventory$tick(CallbackInfo ci) {
		if (!this.getWorld().isClient) {
			this.rpginventory$ejectItemsFromInactiveSpellSlots();
			this.rpginventory$ejectSecondUniqueRing();
//            this.rpginventory$ejectNonHotbarItemsFromHotbar(); TODO disabled for now, needs overhaul
		}
	}

	@Inject(method = "updateTurtleHelmet", at = @At("TAIL"))
	private void rpginventory$updateTurtleHelmet(CallbackInfo ci) {

		boolean keep_inventory_on_death_item_equipped = false;
		Predicate<ItemStack> keep_inventory_on_death_item_equipped_predicate = stack -> stack.isIn(Tags.KEEPS_INVENTORY_ON_DEATH);

		Optional<TrinketComponent> trinkets = TrinketsApi.getTrinketComponent(this);
		if (trinkets.isPresent()) {
			keep_inventory_on_death_item_equipped = trinkets.get().isEquipped(keep_inventory_on_death_item_equipped_predicate);
		}

		keep_inventory_on_death_item_equipped = keep_inventory_on_death_item_equipped || rpginventory$hasEquipped(keep_inventory_on_death_item_equipped_predicate);

		Optional<RegistryEntry.Reference<StatusEffect>> keep_inventory_status_effect = Registries.STATUS_EFFECT.getEntry(Identifier.tryParse(RPGInventory.serverConfig.keep_inventory_status_effect_identifier));
		if (keep_inventory_status_effect.isPresent()) {
			if (keep_inventory_on_death_item_equipped) {
				if (!this.hasStatusEffect(keep_inventory_status_effect.get())) {
					this.addStatusEffect(new StatusEffectInstance(keep_inventory_status_effect.get(), -1, 0, false, false, false));
				}
			} else {
				this.removeStatusEffect(keep_inventory_status_effect.get());
			}
		}

		ItemStack itemStackMainHand = this.getEquippedStack(EquipmentSlot.MAINHAND);
		ItemStack itemStackOffHand = this.getEquippedStack(EquipmentSlot.OFFHAND);
		Optional<RegistryEntry.Reference<StatusEffect>> adventure_building_status_effect = Registries.STATUS_EFFECT.getEntry(Identifier.tryParse(RPGInventory.serverConfig.building_mode_status_effect_identifier));
		boolean hasAdventureBuildingEffect = adventure_building_status_effect.isPresent() && this.hasStatusEffect(adventure_building_status_effect.get());

		Optional<RegistryEntry.Reference<StatusEffect>> no_attack_item_status_effect = Registries.STATUS_EFFECT.getEntry(Identifier.tryParse(RPGInventory.serverConfig.no_attack_item_status_effect_identifier));
		if (no_attack_item_status_effect.isPresent()) {
			if (!itemStackMainHand.isIn(Tags.ATTACK_ITEMS) && !this.isCreative() && !hasAdventureBuildingEffect && !RPGInventory.serverConfig.allow_attacking_with_non_attack_items) {
				if (!this.hasStatusEffect(no_attack_item_status_effect.get())) {
					this.addStatusEffect(new StatusEffectInstance(no_attack_item_status_effect.get(), -1, 0, false, false, false));
				}
			} else {
				this.removeStatusEffect(no_attack_item_status_effect.get());
			}
		}

		Optional<RegistryEntry.Reference<StatusEffect>> needs_two_handing_status_effect = Registries.STATUS_EFFECT.getEntry(Identifier.tryParse(RPGInventory.serverConfig.needs_two_handing_status_effect_identifier));
		if (needs_two_handing_status_effect.isPresent()) {
			if (itemStackMainHand.isIn(Tags.TWO_HANDED_ITEMS) && (this.rpginventory$isHandStackSheathed() || !this.rpginventory$isOffhandStackSheathed()) && !this.isCreative() && !hasAdventureBuildingEffect) {
				if (!this.hasStatusEffect(needs_two_handing_status_effect.get())) {
					this.addStatusEffect(new StatusEffectInstance(needs_two_handing_status_effect.get(), -1, 0, false, false, false));
				}
			} else {
				this.removeStatusEffect(needs_two_handing_status_effect.get());
			}
		}
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
	public void rpginventory$readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {

		if (nbt.contains("is_hand_stack_sheathed", NbtElement.BYTE_TYPE)) {
			this.rpginventory$setIsHandStackSheathed(nbt.getBoolean("is_hand_stack_sheathed"));
		}

		if (nbt.contains("is_offhand_stack_sheathed", NbtElement.BYTE_TYPE)) {
			this.rpginventory$setIsOffhandStackSheathed(nbt.getBoolean("is_offhand_stack_sheathed"));
		}

		if (nbt.contains("old_active_spell_slot_amount", NbtElement.INT_TYPE)) {
			this.rpginventory$setOldActiveSpellSlotAmount(nbt.getInt("old_active_spell_slot_amount"));
		}
	}

	@Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
	public void rpginventory$writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {

		boolean is_hand_stack_sheathed = this.rpginventory$isHandStackSheathed();
		if (is_hand_stack_sheathed) {
			nbt.putBoolean("is_hand_stack_sheathed", this.rpginventory$isHandStackSheathed());
		}

		boolean is_offhand_stack_sheathed = this.rpginventory$isOffhandStackSheathed();
		if (is_offhand_stack_sheathed) {
			nbt.putBoolean("is_offhand_stack_sheathed", this.rpginventory$isOffhandStackSheathed());
		}

		int old_active_spell_slot_amount = this.rpginventory$oldActiveSpellSlotAmount();
		if (old_active_spell_slot_amount != -1) {
			nbt.putInt("old_active_spell_slot_amount", old_active_spell_slot_amount);
		}
	}

	/**
	 * @author TheRedBrain
	 * @reason WIP
	 */
	@Overwrite
	public void equipStack(EquipmentSlot slot, ItemStack stack) {
		this.processEquippedStack(stack);
		if (slot == EquipmentSlot.MAINHAND) {
			this.onEquipStack(slot, ((DuckPlayerEntityMixin) this).rpginventory$isHandStackSheathed() ? this.inventory.main.set(this.inventory.selectedSlot, stack) : ((DuckPlayerInventoryMixin) this.inventory).rpginventory$setHand(stack), stack);
		} else if (slot == EquipmentSlot.OFFHAND) {
			this.onEquipStack(slot, ((DuckPlayerEntityMixin) this).rpginventory$isHandStackSheathed() ? ((DuckPlayerInventoryMixin) this.inventory).rpginventory$setSheathedOffhand(stack) : this.inventory.offHand.set(0, stack), stack);
		} else if (slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
			this.onEquipStack(slot, this.inventory.armor.set(slot.getEntitySlotId(), stack), stack);
		}
	}

	@Inject(method = "dropInventory", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;vanishCursedItems()V", ordinal = 0), cancellable = true)
	private void rpginventory$pre_vanishCursedItems(CallbackInfo ci) {
		Optional<RegistryEntry.Reference<StatusEffect>> keep_inventory_status_effect = Registries.STATUS_EFFECT.getEntry(Identifier.tryParse(RPGInventory.serverConfig.keep_inventory_status_effect_identifier));
		if (keep_inventory_status_effect.isPresent() && this.hasStatusEffect(keep_inventory_status_effect.get())) {
			this.rpginventory$breakKeepInventoryItems();
			ci.cancel();
		}
	}

	@Inject(method = "dropInventory", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;dropAll()V", ordinal = 0))
	private void rpginventory$pre_inventoryDropAll(CallbackInfo ci) {
		if (this.getWorld().getGameRules().getBoolean(GameRulesRegistry.DESTROY_DROPPED_ITEMS_ON_DEATH)) {
			this.inventory.clear();
		}
	}

	@Inject(method = "getEquippedStack", at = @At("HEAD"), cancellable = true)
	public void rpginventory$getEquippedStack(EquipmentSlot slot, CallbackInfoReturnable<ItemStack> cir) {
		if (slot == EquipmentSlot.OFFHAND) {
			cir.setReturnValue(((DuckPlayerInventoryMixin) this.inventory).rpginventory$getOffHandStack());
			cir.cancel();
		}
	}

	/**
	 * @author TheRedBrain
	 * @reason
	 */
	@Overwrite
	public Iterable<ItemStack> getArmorItems() {
		return ((DuckPlayerInventoryMixin) this.inventory).rpginventory$getArmor();
	}

	@Override
	public float rpginventory$getActiveSpellSlotAmount() {
		return (float) Math.min(8, Math.max(0, Math.min(8, Math.max(0, RPGInventory.serverConfig.default_spell_slot_amount)) + this.getAttributeValue(RPGInventory.ACTIVE_SPELL_SLOT_AMOUNT)));
	}

	@Override
	public ItemStack rpginventory$getSheathedHandItemStack() {
		ItemStack itemStack = ((DuckPlayerInventoryMixin) this.getInventory()).rpginventory$getSheathedHand();
		return rpginventory$isHandStackSheathed() && !itemStack.isIn(Tags.EMPTY_HAND_WEAPONS) && ItemUtils.isUsable(itemStack) ? itemStack : ItemStack.EMPTY;
	}

	@Override
	public ItemStack rpginventory$getSheathedOffHandItemStack() {
		ItemStack itemStack = ((DuckPlayerInventoryMixin) this.getInventory()).rpginventory$getSheathedOffhand();
		return rpginventory$isOffhandStackSheathed() && !itemStack.isIn(Tags.EMPTY_HAND_WEAPONS) && ItemUtils.isUsable(itemStack) ? itemStack : ItemStack.EMPTY;
	}

	@Override
	public boolean rpginventory$isHandStackSheathed() {
		return this.dataTracker.get(IS_HAND_STACK_SHEATHED);
	}

	@Override
	public void rpginventory$setIsHandStackSheathed(boolean isHandStackSheathed) {
		this.dataTracker.set(IS_HAND_STACK_SHEATHED, isHandStackSheathed);
	}

	@Override
	public boolean rpginventory$isOffhandStackSheathed() {
		return this.dataTracker.get(IS_OFFHAND_STACK_SHEATHED);
	}

	@Override
	public void rpginventory$setIsOffhandStackSheathed(boolean isOffhandStackSheathed) {
		this.dataTracker.set(IS_OFFHAND_STACK_SHEATHED, isOffhandStackSheathed);
	}

	@Override
	public int rpginventory$oldActiveSpellSlotAmount() {
		return this.dataTracker.get(OLD_ACTIVE_SPELL_SLOT_AMOUNT);
	}

	@Override
	public void rpginventory$setOldActiveSpellSlotAmount(int oldActiveSpellSlotAmount) {
		this.dataTracker.set(OLD_ACTIVE_SPELL_SLOT_AMOUNT, oldActiveSpellSlotAmount);
	}

	@Unique
	private void rpginventory$ejectItemsFromInactiveSpellSlots() {
		int activeSpellSlotAmount = (int) this.rpginventory$getActiveSpellSlotAmount();

		if (this.rpginventory$oldActiveSpellSlotAmount() != activeSpellSlotAmount) {
			PlayerInventory playerInventory = this.getInventory();
			for (int j = activeSpellSlotAmount + 1; j < 9; j++) {

				if (!((DuckPlayerInventoryMixin) playerInventory).rpginventory$getSpellSlotStack(j).isEmpty()) {
					playerInventory.offerOrDrop(((DuckPlayerInventoryMixin) playerInventory).rpginventory$setSpellSlotStack(ItemStack.EMPTY, j));
					if (((PlayerEntity) (Object) this) instanceof ServerPlayerEntity serverPlayerEntity) {
						serverPlayerEntity.sendMessage(Text.translatable("hud.message.spellsRemovedFromInactiveSpellSlots"), false);
					}
				}
			}

			this.rpginventory$setOldActiveSpellSlotAmount(activeSpellSlotAmount);
		}
	}

	@Unique
	private void rpginventory$ejectSecondUniqueRing() {
		PlayerInventory playerInventory = this.getInventory();
		ItemStack firstRingStack = ((DuckPlayerInventoryMixin) playerInventory).rpginventory$getRing1Stack();
		ItemStack secondRingStack = ((DuckPlayerInventoryMixin) playerInventory).rpginventory$getRing2Stack();
		if (firstRingStack.isIn(Tags.UNIQUE_RINGS) && firstRingStack.getItem() == secondRingStack.getItem()) {
			playerInventory.offerOrDrop(((DuckPlayerInventoryMixin) playerInventory).rpginventory$setRing2Stack(ItemStack.EMPTY));

		}
	}

	@Unique
	private void rpginventory$ejectNonHotbarItemsFromHotbar() { // FIXME is only called once?
		Optional<RegistryEntry.Reference<StatusEffect>> adventure_building_status_effect = Registries.STATUS_EFFECT.getEntry(Identifier.tryParse(RPGInventory.serverConfig.building_mode_status_effect_identifier));
		boolean hasAdventureBuildingEffect = adventure_building_status_effect.isPresent() && this.hasStatusEffect(adventure_building_status_effect.get());

		Optional<RegistryEntry.Reference<StatusEffect>> civilisation_status_effect = Registries.STATUS_EFFECT.getEntry(Identifier.tryParse(RPGInventory.serverConfig.civilisation_status_effect_identifier));
		boolean hasCivilisationEffect = civilisation_status_effect.isPresent() && this.hasStatusEffect(civilisation_status_effect.get());

		Optional<RegistryEntry.Reference<StatusEffect>> wilderness_status_effect = Registries.STATUS_EFFECT.getEntry(Identifier.tryParse(RPGInventory.serverConfig.wilderness_status_effect_identifier));
		boolean hasWildernessEffect = wilderness_status_effect.isPresent() && this.hasStatusEffect(wilderness_status_effect.get());

		boolean canChangeEquipment = this.getServer() != null && this.getServer().getGameRules().getBoolean(GameRulesRegistry.CAN_CHANGE_EQUIPMENT);
		if (!this.isCreative() && !hasAdventureBuildingEffect && !((canChangeEquipment && !hasWildernessEffect) || hasCivilisationEffect)) {
			if (!this.isAdventureHotbarCleanedUp) {
				for (int i = 0; i < 9; i++) {
					PlayerInventory playerInventory = this.getInventory();
					Slot slot = this.playerScreenHandler.slots.get(i + 36);

					if (!slot.inventory.getStack(slot.getIndex()).isIn(Tags.ADVENTURE_HOTBAR_ITEMS)) {
						playerInventory.offerOrDrop(slot.inventory.removeStack(slot.getIndex()));
					}
				}
				this.isAdventureHotbarCleanedUp = true;
			}
		} else {
			if (this.isAdventureHotbarCleanedUp) {
				this.isAdventureHotbarCleanedUp = false;
			}
		}
	}

	@Unique
	private void rpginventory$breakKeepInventoryItems() {
		Optional<TrinketComponent> trinkets = TrinketsApi.getTrinketComponent(this);
		if (trinkets.isPresent()) {
			List<Pair<SlotReference, ItemStack>> trinketList = trinkets.get().getAllEquipped();
			for (net.minecraft.util.Pair<SlotReference, ItemStack> trinket : trinketList) {
				if (trinket.getRight().isIn(Tags.KEEPS_INVENTORY_ON_DEATH)) {
					trinket.getLeft().inventory().clear();
				}
			}
		}
		for (int i = 0; i < this.inventory.armor.size(); i++) {
			if (this.inventory.armor.get(i).isIn(Tags.KEEPS_INVENTORY_ON_DEATH)) {
				this.inventory.armor.set(i, ItemStack.EMPTY);
			}
		}
		if (this.inventory.offHand.get(0).isIn(Tags.KEEPS_INVENTORY_ON_DEATH)) {
			this.inventory.offHand.set(0, ItemStack.EMPTY);
		}
	}
}
