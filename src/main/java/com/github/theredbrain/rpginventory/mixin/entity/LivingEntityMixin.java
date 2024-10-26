package com.github.theredbrain.rpginventory.mixin.entity;

import com.github.theredbrain.rpginventory.RPGInventory;
import com.github.theredbrain.rpginventory.entity.DuckLivingEntityMixin;
import com.github.theredbrain.rpginventory.registry.GameRulesRegistry;
import com.github.theredbrain.rpginventory.registry.Tags;
import com.github.theredbrain.rpginventory.util.ItemUtils;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import dev.emi.trinkets.TrinketModifiers;
import dev.emi.trinkets.TrinketPlayerScreenHandler;
import dev.emi.trinkets.api.SlotAttributes;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.SlotType;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketEnums;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.api.event.TrinketDropCallback;
import dev.emi.trinkets.api.event.TrinketEquipCallback;
import dev.emi.trinkets.api.event.TrinketUnequipCallback;
import dev.emi.trinkets.payload.SyncInventoryPayload;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

@Mixin(LivingEntity.class)
@SuppressWarnings("UnreachableCode")
public abstract class LivingEntityMixin extends Entity implements DuckLivingEntityMixin {

	@Unique
	private final Map<String, ItemStack> lastEquippedTrinkets = new HashMap<>();

	@Shadow
	public abstract ItemStack getEquippedStack(EquipmentSlot slot);

	@Shadow
	public abstract AttributeContainer getAttributes();

	public LivingEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject(method = "createLivingAttributes", at = @At("RETURN"))
	private static void rpginventory$createLivingAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
		cir.getReturnValue()
				.add(RPGInventory.ACTIVE_SPELL_SLOT_AMOUNT, 0.0F)
		;
	}

	/**
	 * @author Emi
	 */
	@Inject(at = @At("HEAD"), method = "canFreeze", cancellable = true)
	private void rpginventory$canFreeze(CallbackInfoReturnable<Boolean> cir) {
		Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent((LivingEntity) (Object) this);
		if (component.isPresent()) {
			for (Pair<SlotReference, ItemStack> equipped : component.get().getAllEquipped()) {
				if (equipped.getRight().isIn(ItemTags.FREEZE_IMMUNE_WEARABLES)) {
					cir.setReturnValue(false);
					break;
				}
			}
		}
	}

	@Override
	public boolean rpginventory$hasEquipped(Predicate<ItemStack> predicate) {
		if (predicate.test(this.getEquippedStack(EquipmentSlot.MAINHAND))) {
			return true;
		}
		if (predicate.test(this.getEquippedStack(EquipmentSlot.OFFHAND))) {
			return true;
		}
		if (predicate.test(this.getEquippedStack(EquipmentSlot.FEET))) {
			return true;
		}
		if (predicate.test(this.getEquippedStack(EquipmentSlot.LEGS))) {
			return true;
		}
		if (predicate.test(this.getEquippedStack(EquipmentSlot.CHEST))) {
			return true;
		}
		if (predicate.test(this.getEquippedStack(EquipmentSlot.HEAD))) {
			return true;
		}
		return false;
	}

	@Override
	public int rpginventory$getAmountEquipped(Predicate<ItemStack> predicate) {
		int i = 0;
		if (predicate.test(this.getEquippedStack(EquipmentSlot.MAINHAND))) {
			i += 1;
		}
		if (predicate.test(this.getEquippedStack(EquipmentSlot.OFFHAND))) {
			i += 1;
		}
		if (predicate.test(this.getEquippedStack(EquipmentSlot.FEET))) {
			i += 1;
		}
		if (predicate.test(this.getEquippedStack(EquipmentSlot.LEGS))) {
			i += 1;
		}
		if (predicate.test(this.getEquippedStack(EquipmentSlot.CHEST))) {
			i += 1;
		}
		if (predicate.test(this.getEquippedStack(EquipmentSlot.HEAD))) {
			i += 1;
		}
		return i;
	}

	//	@Redirect(at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(FF)F"), method = "damageEquipment")
//	private float size(float a, float b) {
//		return Math.max(1.0F, (b * 4.0F) / 6.0F);
//	}
	@ModifyVariable(method = "damageEquipment(Lnet/minecraft/entity/damage/DamageSource;F[Lnet/minecraft/entity/EquipmentSlot;)V", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/lang/Math;max(FF)F"), argsOnly = true)
	private float rpginventory$damageEquipment_divideAmount(float oldValue, DamageSource source, float amount) {
		return Math.max(1.0F, amount / 6.0F);
	}

	/**
	 * @author TheRedBrain
	 * @reason overhaul armor
	 */
	@Inject(method = "damageEquipment(Lnet/minecraft/entity/damage/DamageSource;F[Lnet/minecraft/entity/EquipmentSlot;)V", at = @At(value = "JUMP"))
	public void damageEquipment(DamageSource source, float amount, EquipmentSlot[] slots, CallbackInfo ci) {
//		if (!(amount <= 0.0F)) {
//			int i = (int)Math.max(1.0F, amount / 6.0F);
//
//			for (EquipmentSlot equipmentSlot : slots) {
//				ItemStack itemStack = this.getEquippedStack(equipmentSlot);
//				if (itemStack.getItem() instanceof ArmorItem && itemStack.takesDamageFrom(source)) {
//					itemStack.damage(i, ((LivingEntity) (Object) this), equipmentSlot);
//				}
//			}

		int newAmount = (int) Math.max(1.0F, amount / 6.0F);
		if (slots.length > 1 && this.getWorld() instanceof ServerWorld serverWorld && ((LivingEntity) (Object) this) instanceof ServerPlayerEntity serverPlayerEntity) {
			// armor trinkets
			TrinketsApi.getTrinketComponent(serverPlayerEntity).ifPresent(trinkets ->
					trinkets.forEach((slotReference, itemStack) -> {
						if (itemStack.takesDamageFrom(source) && itemStack.isIn(Tags.ARMOR_TRINKETS) && ItemUtils.isUsable(itemStack)) {
							itemStack.damage((int) newAmount, serverWorld, serverPlayerEntity, (item) -> TrinketsApi.onTrinketBroken(itemStack, slotReference, serverPlayerEntity));
						}
					}));
		}

//		if (amount <= 0.0f) {
//			return;
//		}
//		// divide by 6 because gloves and shoulders exist
//		if ((amount /= 6.0f) < 1.0f) {
//			amount = 1.0f;
//		}
//		float finalAmount = amount;
//
////		int[] var4 = slots;
//		int var5 = slots.length;
//
//		for (int var6 = 0; var6 < var5; ++var6) {
//			int i = var4[var6];
//			ItemStack itemStack = (ItemStack) this.armor.get(i);
//			if ((!damageSource.isIn(DamageTypeTags.IS_FIRE) || !itemStack.getItem().isFireproof()) && itemStack.getItem() instanceof ArmorItem && ItemUtils.isUsable(itemStack)) {
//				itemStack.damage((int) finalAmount, (LivingEntity) this.player, (Consumer) ((player) -> {
//					((LivingEntity) player).sendEquipmentBreakStatus(EquipmentSlot.fromTypeIndex(EquipmentSlot.Type.ARMOR, i));
//				}));
//			}
//		}
	}

	/**
	 * Modified code by @Emi to inject gamerule destroyDroppedItemsOnDeath into Trinkets drop logic
	 */
	@Inject(at = @At("TAIL"), method = "dropInventory")
	private void rpginventory$dropInventory(CallbackInfo info) {
		LivingEntity entity = (LivingEntity) (Object) this;

		boolean keepInv = entity.getWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY);

		boolean destroyDroppedItems;
		if (entity.getServer() != null && entity instanceof PlayerEntity) {
			destroyDroppedItems = entity.getServer().getGameRules().getBoolean(GameRulesRegistry.DESTROY_DROPPED_ITEMS_ON_DEATH);
		} else {
			destroyDroppedItems = false;
		}

		TrinketsApi.getTrinketComponent(entity).ifPresent(trinkets -> trinkets.forEach((ref, stack) -> {
			if (stack.isEmpty()) {
				return;
			}

			TrinketEnums.DropRule dropRule = TrinketsApi.getTrinket(stack.getItem()).getDropRule(stack, ref, entity);

			dropRule = TrinketDropCallback.EVENT.invoker().drop(dropRule, stack, ref, entity);

			TrinketInventory inventory = ref.inventory();

			if (dropRule == TrinketEnums.DropRule.DEFAULT) {
				dropRule = inventory.getSlotType().getDropRule();
			}

			if (dropRule == TrinketEnums.DropRule.DEFAULT) {
				if (keepInv && entity.getType() == EntityType.PLAYER) {
					dropRule = TrinketEnums.DropRule.KEEP;
				} else {
					if (EnchantmentHelper.hasAnyEnchantmentsWith(stack, EnchantmentEffectComponentTypes.PREVENT_EQUIPMENT_DROP) || destroyDroppedItems) {
						dropRule = TrinketEnums.DropRule.DESTROY;
					} else {
						dropRule = TrinketEnums.DropRule.DROP;
					}
				}
			}

			switch (dropRule) {
				case DROP:
					dropFromEntity(stack);
					// Fallthrough
				case DESTROY:
					inventory.setStack(ref.index(), ItemStack.EMPTY);
					break;
				default:
					break;
			}
		}));
	}

	/**
	 * @author Emi
	 */
	@Unique
	private void dropFromEntity(ItemStack stack) {
		// Mimic player drop behavior for only players
		if (((Entity) this) instanceof PlayerEntity player) {
			ItemEntity entity = player.dropItem(stack, true, false);
		} else {
			ItemEntity entity = dropStack(stack);
		}
	}

	/**
	 * @author Emi
	 */
	@Inject(at = @At("TAIL"), method = "tick")
	private void rpginventory$tick(CallbackInfo info) {
		LivingEntity entity = (LivingEntity) (Object) this;
		if (entity.isRemoved()) {
			return;
		}
		TrinketsApi.getTrinketComponent(entity).ifPresent(trinkets -> {
			Map<String, ItemStack> newlyEquippedTrinkets = new HashMap<>();
			Map<String, ItemStack> contentUpdates = new HashMap<>();
			trinkets.forEach((ref, stack) -> {
				TrinketInventory inventory = ref.inventory();
				SlotType slotType = inventory.getSlotType();
				int index = ref.index();
				ItemStack oldStack = getOldStack(slotType, index);
				ItemStack newStack = inventory.getStack(index);
				ItemStack newStackCopy = newStack.copy();
				String newRef = slotType.getGroup() + "/" + slotType.getName() + "/" + index;

				if (!ItemStack.areEqual(newStack, oldStack)) {

					TrinketsApi.getTrinket(oldStack.getItem()).onUnequip(oldStack, ref, entity);
					TrinketUnequipCallback.EVENT.invoker().onUnequip(oldStack, ref, entity);
					TrinketsApi.getTrinket(newStack.getItem()).onEquip(newStack, ref, entity);
					TrinketEquipCallback.EVENT.invoker().onEquip(newStack, ref, entity);

					World world = this.getWorld();
					if (!world.isClient) {
						contentUpdates.put(newRef, newStackCopy);

						if (!oldStack.isEmpty()) {
							Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> map = TrinketModifiers.get(oldStack, ref, entity);
							Multimap<String, EntityAttributeModifier> slotMap = HashMultimap.create();
							Set<RegistryEntry<EntityAttribute>> toRemove = Sets.newHashSet();
							for (RegistryEntry<EntityAttribute> attr : map.keySet()) {
								if (attr.hasKeyAndValue() && attr.value() instanceof SlotAttributes.SlotEntityAttribute slotAttr) {
									slotMap.putAll(slotAttr.slot, map.get(attr));
									toRemove.add(attr);
								}
							}
							for (RegistryEntry<EntityAttribute> attr : toRemove) {
								map.removeAll(attr);
							}
							//this.getAttributes().removeModifiers(map);
							map.asMap().forEach((attribute, modifiers) -> {
								EntityAttributeInstance entityAttributeInstance = this.getAttributes().getCustomInstance(attribute);
								if (entityAttributeInstance != null) {
									modifiers.forEach(modifier -> entityAttributeInstance.removeModifier(modifier.id()));
								}
							});

							trinkets.removeModifiers(slotMap);
						}

						if (!newStack.isEmpty()) {
							Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> map = TrinketModifiers.get(newStack, ref, entity);
							Multimap<String, EntityAttributeModifier> slotMap = HashMultimap.create();
							Set<RegistryEntry<EntityAttribute>> toRemove = Sets.newHashSet();
							for (RegistryEntry<EntityAttribute> attr : map.keySet()) {
								if (attr.hasKeyAndValue() && attr.value() instanceof SlotAttributes.SlotEntityAttribute slotAttr) {
									slotMap.putAll(slotAttr.slot, map.get(attr));
									toRemove.add(attr);
								}
							}
							for (RegistryEntry<EntityAttribute> attr : toRemove) {
								map.removeAll(attr);
							}
							//this.getAttributes().addTemporaryModifiers(map);
							map.forEach((attribute, attributeModifier) -> {
								EntityAttributeInstance entityAttributeInstance = this.getAttributes().getCustomInstance(attribute);
								if (entityAttributeInstance != null) {
									entityAttributeInstance.removeModifier(attributeModifier.id());
									entityAttributeInstance.addTemporaryModifier(attributeModifier);
								}

							});
							trinkets.addTemporaryModifiers(slotMap);
						}
					}
				}
				TrinketsApi.getTrinket(newStack.getItem()).tick(newStack, ref, entity);
				ItemStack tickedStack = inventory.getStack(index);
				// Avoid calling equip/unequip on stacks that mutate themselves
				if (tickedStack.getItem() == newStackCopy.getItem()) {
					newlyEquippedTrinkets.put(newRef, tickedStack.copy());
				} else {
					newlyEquippedTrinkets.put(newRef, newStackCopy);
				}
			});

			World world = this.getWorld();
			if (!world.isClient) {
				Set<TrinketInventory> inventoriesToSend = trinkets.getTrackingUpdates();

				if (!contentUpdates.isEmpty() || !inventoriesToSend.isEmpty()) {
					Map<String, NbtCompound> map = new HashMap<>();

					for (TrinketInventory trinketInventory : inventoriesToSend) {
						map.put(trinketInventory.getSlotType().getId(), trinketInventory.getSyncTag());
					}
					SyncInventoryPayload packet = new SyncInventoryPayload(this.getId(), contentUpdates, map);

					for (ServerPlayerEntity player : PlayerLookup.tracking(entity)) {
						ServerPlayNetworking.send(player, packet);
					}

					if (entity instanceof ServerPlayerEntity serverPlayer) {
						ServerPlayNetworking.send(serverPlayer, packet);

						if (!inventoriesToSend.isEmpty()) {
							((TrinketPlayerScreenHandler) serverPlayer.playerScreenHandler).trinkets$updateTrinketSlots(false);
						}
					}

					inventoriesToSend.clear();
				}
			}

			lastEquippedTrinkets.clear();
			lastEquippedTrinkets.putAll(newlyEquippedTrinkets);
		});
	}

	/**
	 * @author Emi
	 */
	@Unique
	private ItemStack getOldStack(SlotType type, int index) {
		return lastEquippedTrinkets.getOrDefault(type.getGroup() + "/" + type.getName() + "/" + index, ItemStack.EMPTY);
	}
}
