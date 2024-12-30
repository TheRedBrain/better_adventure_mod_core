package com.github.theredbrain.rpginventory.mixin.client.gui.hud;

import com.github.theredbrain.rpginventory.RPGInventory;
import com.github.theredbrain.rpginventory.RPGInventoryClient;
import com.github.theredbrain.rpginventory.config.ClientConfig;
import com.github.theredbrain.rpginventory.entity.player.DuckPlayerEntityMixin;
import com.github.theredbrain.rpginventory.entity.player.DuckPlayerInventoryMixin;
import com.github.theredbrain.rpginventory.registry.Tags;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

	@Shadow
	protected abstract PlayerEntity getCameraPlayer();

	@Shadow
	protected abstract void renderHotbarItem(DrawContext context, int x, int y, RenderTickCounter tickCounter, PlayerEntity player, ItemStack stack, int seed);

	@Shadow
	@Final
	private static Identifier HOTBAR_TEXTURE;
	@Shadow
	@Final
	private static Identifier HOTBAR_SELECTION_TEXTURE;
	@Shadow
	@Final
	private MinecraftClient client;
	@Shadow
	@Final
	private static Identifier HOTBAR_ATTACK_INDICATOR_BACKGROUND_TEXTURE;
	@Shadow
	@Final
	private static Identifier HOTBAR_ATTACK_INDICATOR_PROGRESS_TEXTURE;
	@Unique
	private static final Identifier HOTBAR_SELECTION_FIXED_TEXTURE = RPGInventory.identifier("hud/hotbar_selection_fixed");
	@Unique
	private static final Identifier UNSHEATHED_RIGHT_HAND_SLOT_SELECTOR_TEXTURE = RPGInventory.identifier("hud/unsheathed_right_hand_slot_selector");
	@Unique
	private static final Identifier HOTBAR_HAND_SLOTS_TEXTURE = RPGInventory.identifier("hud/hotbar_hand_slots");
	@Unique
	private static final Identifier HOTBAR_ALTERNATE_HAND_SLOTS_TEXTURE = RPGInventory.identifier("hud/hotbar_alternate_hand_slots");

	@WrapOperation(
			method = "renderMainHud",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V")
	)
	private void rpginventory$wrap_renderMainHud(InGameHud instance, DrawContext context, RenderTickCounter tickCounter, Operation<Void> original) {
		if (RPGInventoryClient.CLIENT_CONFIG.enable_hotbar_overhaul.get()) {
			rpginventory$renderOverhauledHotbar(context, tickCounter);
		} else {
			original.call(instance, context, tickCounter);
		}
	}

	@Unique
	private void rpginventory$renderOverhauledHotbar(DrawContext context, RenderTickCounter tickCounter) {

		PlayerEntity playerEntity = this.getCameraPlayer();
		if (playerEntity != null) {
			ClientConfig clientConfig = RPGInventoryClient.CLIENT_CONFIG;
			ItemStack itemStack = playerEntity.getOffHandStack();
			Arm arm = playerEntity.getMainArm().getOpposite();
			int i = context.getScaledWindowWidth() / 2;
			int j = 182;
			int k = 91;
			RenderSystem.enableBlend();
			context.getMatrices().push();
			context.getMatrices().translate(0.0F, 0.0F, -90.0F);

//			if (clientConfig.hotBarOverhaul.always_show_all_hotbar_slots.get()) { // TODO show only active hotbar slots
				context.drawGuiTexture(HOTBAR_TEXTURE, i - 91, context.getScaledWindowHeight() - 22, 182, 22);

				if (((DuckPlayerEntityMixin) playerEntity).rpginventory$isHandStackSheathed() || clientConfig.hotBarOverhaul.always_show_selected_hotbar_slot.get()) {
					context.drawGuiTexture(
							HOTBAR_SELECTION_TEXTURE, i - 91 - 1 + playerEntity.getInventory().selectedSlot * 20, context.getScaledWindowHeight() - 22 - 1, 24, 23
					);
				}
//			} else { // TODO show only active hotbar slots
//
//			}

			ItemStack itemStackHand = ((DuckPlayerInventoryMixin) playerEntity.getInventory()).rpginventory$getHand();
			ItemStack itemStackOffHand = playerEntity.getInventory().offHand.get(0);
			ItemStack itemStackAlternativeHand = ((DuckPlayerInventoryMixin) playerEntity.getInventory()).rpginventory$getAlternativeHand();
			ItemStack itemStackAlternativeOffHand = ((DuckPlayerInventoryMixin) playerEntity.getInventory()).rpginventory$getAlternativeOffhand();
			boolean isHandSheathed = ((DuckPlayerEntityMixin) playerEntity).rpginventory$isHandStackSheathed();
			boolean isOffhandSheathed = ((DuckPlayerEntityMixin) playerEntity).rpginventory$isOffhandStackSheathed();
			if (isHandSheathed) {
				itemStackHand = ((DuckPlayerInventoryMixin) playerEntity.getInventory()).rpginventory$getSheathedHand();
			}
			if (isOffhandSheathed) {
				itemStackOffHand = ((DuckPlayerInventoryMixin) playerEntity.getInventory()).rpginventory$getSheathedOffhand();
			}

			int l = 10;
			int x;
			int y;

			if (clientConfig.hotBarOverhaul.show_empty_hand_slots.get() || !(itemStackHand.isEmpty() || itemStackHand.isIn(Tags.EMPTY_HAND_WEAPONS)) || !(itemStackOffHand.isEmpty() || itemStackOffHand.isIn(Tags.EMPTY_HAND_WEAPONS))) {
				x = context.getScaledWindowWidth() / 2 + clientConfig.hotBarOverhaul.hand_slots_offset_x.get();
				y = context.getScaledWindowHeight() + clientConfig.hotBarOverhaul.hand_slots_offset_y.get();

				context.drawGuiTexture(HOTBAR_HAND_SLOTS_TEXTURE, x, y, 49, 24);

				boolean offhand_slot_is_right = clientConfig.hotBarOverhaul.offhand_item_is_right.get();
				this.renderHotbarItem(context, x + 23, y + 4, tickCounter, playerEntity, offhand_slot_is_right ? itemStackOffHand : itemStackHand, l++);
				this.renderHotbarItem(context, x + 3, y + 4, tickCounter, playerEntity, offhand_slot_is_right ? itemStackHand : itemStackOffHand, l++);

				// sheathed hand indicator
				if ((!isHandSheathed && offhand_slot_is_right) || (!isOffhandSheathed && !offhand_slot_is_right)) {
					context.drawGuiTexture(HOTBAR_SELECTION_FIXED_TEXTURE, x - 1, y, 24, 24);
				}
				if ((!isOffhandSheathed && offhand_slot_is_right) || (!isHandSheathed && !offhand_slot_is_right)) {
					context.drawGuiTexture(UNSHEATHED_RIGHT_HAND_SLOT_SELECTOR_TEXTURE, x + 19, y, 24, 24);
				}
			}

			if (clientConfig.hotBarOverhaul.show_empty_alternative_hand_slots.get() || !(itemStackAlternativeHand.isEmpty() || itemStackAlternativeHand.isIn(Tags.EMPTY_HAND_WEAPONS)) || !(itemStackAlternativeOffHand.isEmpty() || itemStackAlternativeOffHand.isIn(Tags.EMPTY_HAND_WEAPONS))) {
				x = context.getScaledWindowWidth() / 2 + clientConfig.hotBarOverhaul.alternative_hand_slots_offset_x.get();
				y = context.getScaledWindowHeight() + clientConfig.hotBarOverhaul.alternative_hand_slots_offset_y.get();

				context.drawGuiTexture(HOTBAR_ALTERNATE_HAND_SLOTS_TEXTURE, x, y, 49, 24);

				boolean alternative_offhand_slot_is_right = clientConfig.hotBarOverhaul.alternative_offhand_item_is_right.get();
				this.renderHotbarItem(context, x + 10, y + 4, tickCounter, playerEntity, alternative_offhand_slot_is_right ? itemStackAlternativeHand : itemStackAlternativeOffHand, l++);
				this.renderHotbarItem(context, x + 30, y + 4, tickCounter, playerEntity, alternative_offhand_slot_is_right ? itemStackAlternativeOffHand : itemStackAlternativeHand, l);
			}
//			if (!itemStack.isEmpty()) {
//				if (arm == Arm.LEFT) {
//					context.drawGuiTexture(HOTBAR_OFFHAND_LEFT_TEXTURE, i - 91 - 29, context.getScaledWindowHeight() - 23, 29, 24);
//				} else {
//					context.drawGuiTexture(HOTBAR_OFFHAND_RIGHT_TEXTURE, i + 91, context.getScaledWindowHeight() - 23, 29, 24);
//				}
//			}

			context.getMatrices().pop();
			RenderSystem.disableBlend();
			l = 1;

//			if (clientConfig.hotBarOverhaul.always_show_all_hotbar_slots.get()) { // TODO show only active hotbar slots
				for (int m = 0; m < 9; m++) {
					int n = i - 90 + m * 20 + 2;
					int o = context.getScaledWindowHeight() - 16 - 3;
					this.renderHotbarItem(context, n, o, tickCounter, playerEntity, playerEntity.getInventory().main.get(m), l++);
				}
//			} else { // TODO show only active hotbar slots
//
//			}

//			if (!itemStack.isEmpty()) {
//				int m = context.getScaledWindowHeight() - 16 - 3;
//				if (arm == Arm.LEFT) {
//					this.renderHotbarItem(context, i - 91 - 26, m, tickCounter, playerEntity, itemStack, l++);
//				} else {
//					this.renderHotbarItem(context, i + 91 + 10, m, tickCounter, playerEntity, itemStack, l++);
//				}
//			}

			if (this.client.options.getAttackIndicator().getValue() == AttackIndicator.HOTBAR) {
				RenderSystem.enableBlend();
				ClientPlayerEntity clientPlayerEntity = this.client.player;
				float f = clientPlayerEntity.getAttackCooldownProgress(0.0F);
				if (f < 1.0F) {
					int n = context.getScaledWindowHeight() - 20;
					int o = i + 91 + 6;
					if (arm == Arm.RIGHT) {
						o = i - 91 - 22;
					}

					int p = (int) (f * 19.0F);
					context.drawGuiTexture(HOTBAR_ATTACK_INDICATOR_BACKGROUND_TEXTURE, o, n, 18, 18);
					context.drawGuiTexture(HOTBAR_ATTACK_INDICATOR_PROGRESS_TEXTURE, 18, 18, 0, 18 - p, o, n + 18 - p, 18, p);
				}

				RenderSystem.disableBlend();
			}
		}
		// TODO if alternative hotbar is enabled
//				do all the stuff
//				else render the vanilla hotbar
//				use existing config options to customize the alternative hotbar
//				add support for Raised
	}

	// disables rendering of the armor bar when disabled in the client config
	@WrapOperation(
			method = "renderArmor",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getArmor()I")
	)
	private static int rpginventory$wrap_getArmor(PlayerEntity instance, Operation<Integer> original) {
		return RPGInventoryClient.CLIENT_CONFIG.show_armor_bar.get() ? original.call(instance) : 0;
	}
}
