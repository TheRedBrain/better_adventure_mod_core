package com.github.theredbrain.rpginventory.mixin.screen;

import com.github.theredbrain.rpginventory.RPGInventory;
import com.github.theredbrain.rpginventory.entity.player.DuckPlayerEntityMixin;
import com.github.theredbrain.rpginventory.registry.Tags;
import com.github.theredbrain.rpginventory.screen.DuckPlayerScreenHandlerMixin;
import com.github.theredbrain.rpginventory.screen.DuckSlotMixin;
import com.github.theredbrain.slotcustomizationapi.api.SlotCustomization;
import com.google.common.collect.ImmutableList;
import dev.emi.trinkets.Point;
import dev.emi.trinkets.SurvivalTrinketSlot;
import dev.emi.trinkets.TrinketPlayerScreenHandler;
import dev.emi.trinkets.TrinketsClient;
import dev.emi.trinkets.api.SlotGroup;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.SlotType;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.mixin.accessor.ScreenHandlerAccessor;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Mixin(PlayerScreenHandler.class)
public abstract class PlayerScreenHandlerMixin extends ScreenHandler implements TrinketPlayerScreenHandler, DuckPlayerScreenHandlerMixin {
	@Shadow
	@Final
	private PlayerEntity owner;

	@Unique
	private final Map<SlotGroup, Integer> groupNums = new HashMap<>();
	@Unique
	private final Map<SlotGroup, Point> groupPos = new HashMap<>();
	@Unique
	private final Map<SlotGroup, List<Point>> slotHeights = new HashMap<>();
	@Unique
	private final Map<SlotGroup, List<SlotType>> slotTypes = new HashMap<>();
	@Unique
	private final Map<SlotGroup, Integer> slotWidths = new HashMap<>();
	@Unique
	private int trinketSlotStart = 0;
	@Unique
	private int trinketSlotEnd = 0;
	@Unique
	private int groupCount = 0;
	@Unique
	private PlayerInventory inventory;

	@Unique
	private boolean isAttributeScreenVisible = false;

	@Unique
	private int handSlotIndex = -1;

	@Unique
	private int sheathedHandSlotIndex = -1;

	@Unique
	private int sheathedOffhandSlotIndex = -1;

	@Unique
	private int alternativeHandSlotIndex = -1;

	@Unique
	private int alternativeOffHandSlotIndex = -1;

	public PlayerScreenHandlerMixin() {
		super(null, 0);
	}

	/**
	 * @author TheRedBrain
	 */
	@Inject(method = "<init>", at = @At("TAIL"))
	public void PlayerScreenHandler(PlayerInventory inventory, boolean onServer, PlayerEntity owner, CallbackInfo ci) {
		this.inventory = inventory;

		var serverConfig = RPGInventory.SERVER_CONFIG;

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				((SlotCustomization) this.slots.get(j + (i + 1) * 9)).slotcustomizationapi$setY(138 + i * 18);
			}
		}
		for (int i = 0; i < 9; ++i) {
			((SlotCustomization) this.slots.get(i + 36)).slotcustomizationapi$setY(196);
		}

		if (serverConfig.disable_inventory_crafting_slots.get()) {
			((SlotCustomization) this.slots.get(0)).slotcustomizationapi$setDisabledOverride(true);
			((SlotCustomization) this.slots.get(1)).slotcustomizationapi$setDisabledOverride(true);
			((SlotCustomization) this.slots.get(2)).slotcustomizationapi$setDisabledOverride(true);
			((SlotCustomization) this.slots.get(3)).slotcustomizationapi$setDisabledOverride(true);
			((SlotCustomization) this.slots.get(4)).slotcustomizationapi$setDisabledOverride(true);
		} else {
			((SlotCustomization) this.slots.get(0)).slotcustomizationapi$setX(serverConfig.inventory_crafting_slots_x_offset.get() + 56);
			((SlotCustomization) this.slots.get(0)).slotcustomizationapi$setY(serverConfig.inventory_crafting_slots_y_offset.get() + 10);
			((SlotCustomization) this.slots.get(1)).slotcustomizationapi$setX(serverConfig.inventory_crafting_slots_x_offset.get());
			((SlotCustomization) this.slots.get(1)).slotcustomizationapi$setY(serverConfig.inventory_crafting_slots_y_offset.get());
			((SlotCustomization) this.slots.get(2)).slotcustomizationapi$setX(serverConfig.inventory_crafting_slots_x_offset.get() + 18);
			((SlotCustomization) this.slots.get(2)).slotcustomizationapi$setY(serverConfig.inventory_crafting_slots_y_offset.get());
			((SlotCustomization) this.slots.get(3)).slotcustomizationapi$setX(serverConfig.inventory_crafting_slots_x_offset.get());
			((SlotCustomization) this.slots.get(3)).slotcustomizationapi$setY(serverConfig.inventory_crafting_slots_y_offset.get() + 18);
			((SlotCustomization) this.slots.get(4)).slotcustomizationapi$setX(serverConfig.inventory_crafting_slots_x_offset.get() + 18);
			((SlotCustomization) this.slots.get(4)).slotcustomizationapi$setY(serverConfig.inventory_crafting_slots_y_offset.get() + 18);
		}

		// reposition vanilla armor slots
		// head
		((SlotCustomization) this.slots.get(5)).slotcustomizationapi$setX(serverConfig.head_slot_x_offset.get());
		((SlotCustomization) this.slots.get(5)).slotcustomizationapi$setY(serverConfig.head_slot_y_offset.get());
		// chest
		((SlotCustomization) this.slots.get(6)).slotcustomizationapi$setX(serverConfig.chest_slot_x_offset.get());
		((SlotCustomization) this.slots.get(6)).slotcustomizationapi$setY(serverConfig.chest_slot_y_offset.get());
		// legs
		((SlotCustomization) this.slots.get(7)).slotcustomizationapi$setX(serverConfig.legs_slot_x_offset.get());
		((SlotCustomization) this.slots.get(7)).slotcustomizationapi$setY(serverConfig.legs_slot_y_offset.get());
		// feet
		((SlotCustomization) this.slots.get(8)).slotcustomizationapi$setX(serverConfig.feet_slot_x_offset.get());
		((SlotCustomization) this.slots.get(8)).slotcustomizationapi$setY(serverConfig.feet_slot_y_offset.get());

		// reposition vanilla offhand slot
		((SlotCustomization) this.slots.get(45)).slotcustomizationapi$setX(serverConfig.offhand_slot_x_offset.get());
		((SlotCustomization) this.slots.get(45)).slotcustomizationapi$setY(serverConfig.offhand_slot_y_offset.get());

		// adding slot tooltips
		List<Text> list5 = new ArrayList<>();
		list5.add(Text.translatable("slot.tooltip.head"));
		((DuckSlotMixin) this.slots.get(5)).rpginventory$setSlotTooltipText(list5);

		List<Text> list6 = new ArrayList<>();
		list6.add(Text.translatable("slot.tooltip.chest"));
		((DuckSlotMixin) this.slots.get(6)).rpginventory$setSlotTooltipText(list6);

		List<Text> list7 = new ArrayList<>();
		list7.add(Text.translatable("slot.tooltip.legs"));
		((DuckSlotMixin) this.slots.get(7)).rpginventory$setSlotTooltipText(list7);

		List<Text> list8 = new ArrayList<>();
		list8.add(Text.translatable("slot.tooltip.feet"));
		((DuckSlotMixin) this.slots.get(8)).rpginventory$setSlotTooltipText(list8);

		List<Text> list45 = new ArrayList<>();
		list45.add(Text.translatable("slot.tooltip.offhand"));
		((DuckSlotMixin) this.slots.get(45)).rpginventory$setSlotTooltipText(list45);

		trinkets$updateTrinketSlots(true);
	}

	/**
	 * Modified and expanded code by @Emi
	 */
	@Override
	public void trinkets$updateTrinketSlots(boolean slotsChanged) {

		var serverConfig = RPGInventory.SERVER_CONFIG;

		TrinketsApi.getTrinketComponent(owner).ifPresent(trinkets -> {
			if (slotsChanged) trinkets.update();
			Map<String, SlotGroup> groups = trinkets.getGroups();
			groupPos.clear();
			while (trinketSlotStart < trinketSlotEnd) {
				slots.remove(trinketSlotStart);
				((ScreenHandlerAccessor) (this)).getTrackedStacks().remove(trinketSlotStart);
				((ScreenHandlerAccessor) (this)).getPreviousTrackedStacks().remove(trinketSlotStart);
				trinketSlotEnd--;
			}

			int groupNum = 0;//1; // Start at 1 because offhand exists
			int extraGroupCount = 0;

			for (SlotGroup group : groups.values().stream().sorted(Comparator.comparing(SlotGroup::getOrder)).toList()) {
				if (!rpginventory$hasSlots(trinkets, group)) {
					continue;
				}
				String groupName = group.getName();
				int id = group.getSlotId();
				if (id != -1) {
					if (this.slots.size() > id) {
						Slot slot = this.slots.get(id);
						if (!(slot instanceof SurvivalTrinketSlot)) {
							groupPos.put(group, new Point(slot.x, slot.y));
							groupNums.put(group, -id);
						}
					}
				} else {
					int x;
					int y;
					if (Objects.equals(groupName, "belts")) {
						x = serverConfig.belts_group_x_offset.get();
						y = serverConfig.belts_group_y_offset.get();
					} else if (Objects.equals(groupName, "shoulders")) {
						x = serverConfig.shoulders_group_x_offset.get();
						y = serverConfig.shoulders_group_y_offset.get();
					} else if (Objects.equals(groupName, "necklaces")) {
						x = serverConfig.necklaces_group_x_offset.get();
						y = serverConfig.necklaces_group_y_offset.get();
					} else if (Objects.equals(groupName, "rings_1")) {
						x = serverConfig.rings_1_group_x_offset.get();
						y = serverConfig.rings_1_group_y_offset.get();
					} else if (Objects.equals(groupName, "rings_2")) {
						x = serverConfig.rings_2_group_x_offset.get();
						y = serverConfig.rings_2_group_y_offset.get();
					} else if (Objects.equals(groupName, "gloves")) {
						x = serverConfig.gloves_group_x_offset.get();
						y = serverConfig.gloves_group_y_offset.get();
					} else if (Objects.equals(groupName, "hand")) {
						x = serverConfig.hand_group_x_offset.get();
						y = serverConfig.hand_group_y_offset.get();
					} else if (Objects.equals(groupName, "alternative_hand")) {
						x = serverConfig.alternative_hand_group_x_offset.get();
						y = serverConfig.alternative_hand_group_y_offset.get();
					} else if (Objects.equals(groupName, "alternative_offhand")) {
						x = serverConfig.alternative_offhand_group_x_offset.get();
						y = serverConfig.alternative_offhand_group_y_offset.get();
					} else if (Objects.equals(groupName, "spell_slot_1")) {
						x = serverConfig.spell_slots_x_offset.get();
						y = serverConfig.spell_slots_y_offset.get();
					} else if (Objects.equals(groupName, "spell_slot_2")) {
						x = serverConfig.spell_slots_x_offset.get() + 18;
						y = serverConfig.spell_slots_y_offset.get();
					} else if (Objects.equals(groupName, "spell_slot_3")) {
						x = serverConfig.spell_slots_x_offset.get() + 36;
						y = serverConfig.spell_slots_y_offset.get();
					} else if (Objects.equals(groupName, "spell_slot_4")) {
						x = serverConfig.spell_slots_x_offset.get() + 54;
						y = serverConfig.spell_slots_y_offset.get();
					} else if (Objects.equals(groupName, "spell_slot_5")) {
						x = serverConfig.spell_slots_x_offset.get();
						y = serverConfig.spell_slots_y_offset.get() + 18;
					} else if (Objects.equals(groupName, "spell_slot_6")) {
						x = serverConfig.spell_slots_x_offset.get() + 18;
						y = serverConfig.spell_slots_y_offset.get() + 18;
					} else if (Objects.equals(groupName, "spell_slot_7")) {
						x = serverConfig.spell_slots_x_offset.get() + 36;
						y = serverConfig.spell_slots_y_offset.get() + 18;
					} else if (Objects.equals(groupName, "spell_slot_8")) {
						x = serverConfig.spell_slots_x_offset.get() + 54;
						y = serverConfig.spell_slots_y_offset.get() + 18;
					} else if (Objects.equals(groupName, "sheathed_hand")) {
						x = serverConfig.hand_group_x_offset.get();
						y = serverConfig.hand_group_y_offset.get();
					} else if (Objects.equals(groupName, "sheathed_offhand")) {
						x = serverConfig.offhand_slot_x_offset.get();
						y = serverConfig.offhand_slot_y_offset.get();
					} else if (Objects.equals(groupName, "empty_hand") || Objects.equals(groupName, "empty_offhand")) {
						continue;
					} else {
						x = -14 - (extraGroupCount / 4) * 18;
						y = 8 + (extraGroupCount % 4) * 18;
						extraGroupCount++;
					}
					groupPos.put(group, new Point(x, y));
					groupNums.put(group, groupNum);
					groupNum++;
				}
			}
			groupCount = extraGroupCount;
			trinketSlotStart = slots.size();
			slotWidths.clear();
			slotHeights.clear();
			slotTypes.clear();

			for (Map.Entry<String, Map<String, TrinketInventory>> entry : trinkets.getInventory().entrySet()) {
				String groupId = entry.getKey();
				SlotGroup group = groups.get(groupId);
				int groupOffset = 1;

				if (group.getSlotId() != -1) {
					groupOffset++;
				}
				int width = 0;
				Point pos = trinkets$getGroupPos(group);
				if (pos == null) {
					continue;
				}
				for (Map.Entry<String, TrinketInventory> slot : entry.getValue().entrySet().stream().sorted((a, b) ->
						Integer.compare(a.getValue().getSlotType().getOrder(), b.getValue().getSlotType().getOrder())).toList()) {
					TrinketInventory stacks = slot.getValue();
					if (stacks.size() == 0) {
						continue;
					}
					int slotOffset = 1;
					int x = (int) ((groupOffset / 2) * 18 * Math.pow(-1, groupOffset));
					slotHeights.computeIfAbsent(group, (k) -> new ArrayList<>()).add(new Point(x, stacks.size()));
					slotTypes.computeIfAbsent(group, (k) -> new ArrayList<>()).add(stacks.getSlotType());
					for (int i = 0; i < stacks.size(); i++) {
						int y = (int) (pos.y() + (slotOffset / 2) * 18 * Math.pow(-1, slotOffset));
						this.addSlot(new SurvivalTrinketSlot(stacks, i, x + pos.x(), y, group, stacks.getSlotType(), i, groupOffset == 1 && i == 0));
						slotOffset++;
					}
					groupOffset++;

					width++;
				}
				slotWidths.put(group, width);
			}

			trinketSlotEnd = slots.size();
		});
	}

	/**
	 * @author Emi
	 */
	@Unique
	private boolean rpginventory$hasSlots(TrinketComponent comp, SlotGroup group) {
		for (TrinketInventory inv : comp.getInventory().get(group.getName()).values()) {
			if (inv.size() > 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @author Emi
	 */
	@Override
	public int trinkets$getGroupNum(SlotGroup group) {
		return groupNums.getOrDefault(group, 0);
	}

	/**
	 * @author Emi
	 */
	@Nullable
	@Override
	public Point trinkets$getGroupPos(SlotGroup group) {
		return groupPos.get(group);
	}

	/**
	 * @author Emi
	 */
	@NotNull
	@Override
	public List<Point> trinkets$getSlotHeights(SlotGroup group) {
		return slotHeights.getOrDefault(group, ImmutableList.of());
	}

	/**
	 * @author Emi
	 */
	@Nullable
	@Override
	public Point trinkets$getSlotHeight(SlotGroup group, int i) {
		List<Point> points = this.trinkets$getSlotHeights(group);
		return i < points.size() ? points.get(i) : null;
	}

	/**
	 * @author Emi
	 */
	@NotNull
	@Override
	public List<SlotType> trinkets$getSlotTypes(SlotGroup group) {
		return slotTypes.getOrDefault(group, ImmutableList.of());
	}

	/**
	 * @author Emi
	 */
	@Override
	public int trinkets$getSlotWidth(SlotGroup group) {
		return slotWidths.getOrDefault(group, 0);
	}

	/**
	 * @author Emi
	 */
	@Override
	public int trinkets$getGroupCount() {
		return groupCount;
	}

	/**
	 * @author Emi
	 */
	@Override
	public int trinkets$getTrinketSlotStart() {
		return trinketSlotStart;
	}

	/**
	 * @author Emi
	 */
	@Override
	public int trinkets$getTrinketSlotEnd() {
		return trinketSlotEnd;
	}

	/**
	 * @author Emi
	 */
	@Inject(at = @At("HEAD"), method = "onClosed")
	private void rpginventory$onClosed(PlayerEntity player, CallbackInfo info) {
		if (player.getWorld().isClient) {
			TrinketsClient.activeGroup = null;
			TrinketsClient.activeType = null;
			TrinketsClient.quickMoveGroup = null;
		}
	}

	/**
	 * Modified and expanded code by @Emi
	 */
	@Inject(at = @At("HEAD"), method = "quickMove", cancellable = true)
	private void rpginventory$quickMove(PlayerEntity player, int slot, CallbackInfoReturnable<ItemStack> cir) {
		Slot slot1 = slots.get(slot);

//		// TODO adventure hotbar items
//		StatusEffect civilisation_status_effect = Registries.STATUS_EFFECT.get(Identifier.tryParse(RPGInventory.serverConfig.civilisation_status_effect_identifier));
//		boolean hasCivilisationEffect = civilisation_status_effect != null && player.hasStatusEffect(civilisation_status_effect);
//
//		StatusEffect wilderness_status_effect = Registries.STATUS_EFFECT.get(Identifier.tryParse(RPGInventory.serverConfig.wilderness_status_effect_identifier));
//		boolean hasWildernessEffect = wilderness_status_effect != null && player.hasStatusEffect(wilderness_status_effect);
//
//		boolean canChangeEquipment = true;
//
//		if (player.getServer() != null) {
//			canChangeEquipment = player.getServer().getGameRules().getBoolean(GameRulesRegistry.CAN_CHANGE_EQUIPMENT);
//		}
//		hasCivilisationEffect = hasCivilisationEffect || (canChangeEquipment && !hasWildernessEffect);

		if (slot1.hasStack()) {
			ItemStack stack = slot1.getStack();
			if (slot >= trinketSlotStart && slot < trinketSlotEnd) {
				if (!this.insertItem(stack, 9, 45, false)) {   // TODO adventure hotbar items
					cir.setReturnValue(ItemStack.EMPTY);
					cir.cancel();
				} else {
					cir.setReturnValue(stack);
					cir.cancel();
				}
			} else if (slot >= 9 && slot < 45) {
				TrinketsApi.getTrinketComponent(player).ifPresent(trinkets -> {
							for (int i = trinketSlotStart; i < trinketSlotEnd; i++) {
								Slot s = slots.get(i);
								if (!(s instanceof SurvivalTrinketSlot) || !s.canInsert(stack)) {
									continue;
								}

								SurvivalTrinketSlot ts = (SurvivalTrinketSlot) s;
								SlotType type = ts.getType();
								SlotReference ref = new SlotReference((TrinketInventory) ts.inventory, ts.getIndex());

								if ((Objects.equals(type.getGroup(), "spell_slot_1") && ((DuckPlayerEntityMixin) player).rpginventory$getActiveSpellSlotAmount() < 1)
										|| (Objects.equals(type.getGroup(), "spell_slot_2") && ((DuckPlayerEntityMixin) player).rpginventory$getActiveSpellSlotAmount() < 2)
										|| (Objects.equals(type.getGroup(), "spell_slot_3") && ((DuckPlayerEntityMixin) player).rpginventory$getActiveSpellSlotAmount() < 3)
										|| (Objects.equals(type.getGroup(), "spell_slot_4") && ((DuckPlayerEntityMixin) player).rpginventory$getActiveSpellSlotAmount() < 4)
										|| (Objects.equals(type.getGroup(), "spell_slot_5") && ((DuckPlayerEntityMixin) player).rpginventory$getActiveSpellSlotAmount() < 5)
										|| (Objects.equals(type.getGroup(), "spell_slot_6") && ((DuckPlayerEntityMixin) player).rpginventory$getActiveSpellSlotAmount() < 6)
										|| (Objects.equals(type.getGroup(), "spell_slot_7") && ((DuckPlayerEntityMixin) player).rpginventory$getActiveSpellSlotAmount() < 7)
										|| (Objects.equals(type.getGroup(), "spell_slot_8") && ((DuckPlayerEntityMixin) player).rpginventory$getActiveSpellSlotAmount() < 8)
								) {
									continue;
								}

								if (Objects.equals(type.getGroup(), "hand") && Objects.equals(type.getName(), "hand")) {
									handSlotIndex = i;
									continue;
								}
								if (Objects.equals(type.getGroup(), "sheathed_hand") && Objects.equals(type.getName(), "sheathed_hand")) {
									sheathedHandSlotIndex = i;
									continue;
								}
								if (Objects.equals(type.getGroup(), "sheathed_offhand") && Objects.equals(type.getName(), "sheathed_offhand")) {
									sheathedOffhandSlotIndex = i;
									continue;
								}
								if (Objects.equals(type.getGroup(), "alternative_hand") && Objects.equals(type.getName(), "alternative_hand")) {
									alternativeHandSlotIndex = i;
									continue;
								}
								if (Objects.equals(type.getGroup(), "alternative_offhand") && Objects.equals(type.getName(), "alternative_offhand")) {
									alternativeOffHandSlotIndex = i;
									continue;
								}

								boolean res = TrinketsApi.evaluatePredicateSet(type.getQuickMovePredicates(), stack, ref, player);

								if (res) {
									if (this.insertItem(stack, i, i + 1, false)) {
										handSlotIndex = -1;
										sheathedHandSlotIndex = -1;
										sheathedOffhandSlotIndex = -1;
										alternativeHandSlotIndex = -1;
										alternativeOffHandSlotIndex = -1;
										if (player.getWorld().isClient) {
											TrinketsClient.quickMoveTimer = 20;
											TrinketsClient.quickMoveGroup = TrinketsApi.getPlayerSlots(this.owner).get(type.getGroup());
											if (ref.index() > 0) {
												TrinketsClient.quickMoveType = type;
											} else {
												TrinketsClient.quickMoveType = null;
											}
										}
									}
								}
							}
						}
				);

				EquipmentSlot equipmentSlot = this.owner.getPreferredEquipmentSlot(stack);

				if (((DuckPlayerEntityMixin) this.owner).rpginventory$isHandStackSheathed()) {
					if (!stack.isEmpty() && sheathedHandSlotIndex > -1 && stack.isIn(Tags.HAND_ITEMS)) {
						Slot s = slots.get(sheathedHandSlotIndex);
						if (s instanceof SurvivalTrinketSlot ts && s.canInsert(stack)) {

							SlotType type = ts.getType();
							SlotReference ref = new SlotReference((TrinketInventory) ts.inventory, ts.getIndex());
							boolean res = TrinketsApi.evaluatePredicateSet(type.getQuickMovePredicates(), stack, ref, player);

							if (res) {
								if (this.insertItem(stack, sheathedHandSlotIndex, sheathedHandSlotIndex + 1, false)) {
									if (player.getWorld().isClient) {
										TrinketsClient.quickMoveTimer = 20;
										TrinketsClient.quickMoveGroup = TrinketsApi.getPlayerSlots(this.owner).get(type.getGroup());
										if (ref.index() > 0) {
											TrinketsClient.quickMoveType = type;
										} else {
											TrinketsClient.quickMoveType = null;
										}
									}
								}
							}
						}
					}
				} else {
					if (!stack.isEmpty() && handSlotIndex > -1 && stack.isIn(Tags.HAND_ITEMS)) {
						Slot s = slots.get(handSlotIndex);
						if (s instanceof SurvivalTrinketSlot ts && s.canInsert(stack)) {

							SlotType type = ts.getType();
							SlotReference ref = new SlotReference((TrinketInventory) ts.inventory, ts.getIndex());
							boolean res = TrinketsApi.evaluatePredicateSet(type.getQuickMovePredicates(), stack, ref, player);

							if (res) {
								if (this.insertItem(stack, handSlotIndex, handSlotIndex + 1, false)) {
									if (player.getWorld().isClient) {
										TrinketsClient.quickMoveTimer = 20;
										TrinketsClient.quickMoveGroup = TrinketsApi.getPlayerSlots(this.owner).get(type.getGroup());
										if (ref.index() > 0) {
											TrinketsClient.quickMoveType = type;
										} else {
											TrinketsClient.quickMoveType = null;
										}
									}
								}
							}
						}
					}
				}

				if (((DuckPlayerEntityMixin) this.owner).rpginventory$isOffhandStackSheathed()) {
					if (!stack.isEmpty() && sheathedOffhandSlotIndex > -1 && (equipmentSlot == EquipmentSlot.OFFHAND || stack.isIn(Tags.OFFHAND_ITEMS))) {
						Slot s = slots.get(sheathedOffhandSlotIndex);
						if (s instanceof SurvivalTrinketSlot ts && s.canInsert(stack)) {

							SlotType type = ts.getType();
							SlotReference ref = new SlotReference((TrinketInventory) ts.inventory, ts.getIndex());
							boolean res = TrinketsApi.evaluatePredicateSet(type.getQuickMovePredicates(), stack, ref, player);

							if (res) {
								if (this.insertItem(stack, sheathedOffhandSlotIndex, sheathedOffhandSlotIndex + 1, false)) {
									if (player.getWorld().isClient) {
										TrinketsClient.quickMoveTimer = 20;
										TrinketsClient.quickMoveGroup = TrinketsApi.getPlayerSlots(this.owner).get(type.getGroup());
										if (ref.index() > 0) {
											TrinketsClient.quickMoveType = type;
										} else {
											TrinketsClient.quickMoveType = null;
										}
									}
								}
							}
						}
					}
				} else {
					if (!stack.isEmpty() && (equipmentSlot == EquipmentSlot.OFFHAND || stack.isIn(Tags.OFFHAND_ITEMS)) && !this.slots.get(45).hasStack()) {
						if (!this.insertItem(stack, 45, 46, false)) {
							cir.setReturnValue(ItemStack.EMPTY);
							cir.cancel();
						}
					}
				}
				if (!stack.isEmpty() && alternativeHandSlotIndex > -1 && stack.isIn(Tags.HAND_ITEMS)) {
					Slot s = slots.get(alternativeHandSlotIndex);
					if (s instanceof SurvivalTrinketSlot ts && s.canInsert(stack)) {

						SlotType type = ts.getType();
						SlotReference ref = new SlotReference((TrinketInventory) ts.inventory, ts.getIndex());
						boolean res = TrinketsApi.evaluatePredicateSet(type.getQuickMovePredicates(), stack, ref, player);

						if (res) {
							if (this.insertItem(stack, alternativeHandSlotIndex, alternativeHandSlotIndex + 1, false)) {
								if (player.getWorld().isClient) {
									TrinketsClient.quickMoveTimer = 20;
									TrinketsClient.quickMoveGroup = TrinketsApi.getPlayerSlots(this.owner).get(type.getGroup());
									if (ref.index() > 0) {
										TrinketsClient.quickMoveType = type;
									} else {
										TrinketsClient.quickMoveType = null;
									}
								}
							}
						}
					}
				}
				if (!stack.isEmpty() && alternativeOffHandSlotIndex > -1 && (equipmentSlot == EquipmentSlot.OFFHAND || stack.isIn(Tags.OFFHAND_ITEMS))) {
					Slot s = slots.get(alternativeOffHandSlotIndex);
					if (s instanceof SurvivalTrinketSlot ts && s.canInsert(stack)) {

						SlotType type = ts.getType();
						SlotReference ref = new SlotReference((TrinketInventory) ts.inventory, ts.getIndex());
						boolean res = TrinketsApi.evaluatePredicateSet(type.getQuickMovePredicates(), stack, ref, player);

						if (res) {
							if (this.insertItem(stack, alternativeOffHandSlotIndex, alternativeOffHandSlotIndex + 1, false)) {
								if (player.getWorld().isClient) {
									TrinketsClient.quickMoveTimer = 20;
									TrinketsClient.quickMoveGroup = TrinketsApi.getPlayerSlots(this.owner).get(type.getGroup());
									if (ref.index() > 0) {
										TrinketsClient.quickMoveType = type;
									} else {
										TrinketsClient.quickMoveType = null;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public boolean rpginventory$isAttributeScreenVisible() {
		return this.isAttributeScreenVisible;
	}

	@Override
	public void rpginventory$setIsAttributeScreenVisible(boolean isAttributeScreenVisible) {
		this.isAttributeScreenVisible = isAttributeScreenVisible;
	}
}
