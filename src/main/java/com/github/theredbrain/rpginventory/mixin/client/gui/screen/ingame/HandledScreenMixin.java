package com.github.theredbrain.rpginventory.mixin.client.gui.screen.ingame;

import com.github.theredbrain.rpginventory.RPGInventoryClient;
import com.github.theredbrain.rpginventory.config.ClientConfig;
import com.github.theredbrain.rpginventory.registry.Tags;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {

	/**
	 * effectively disables the vanilla swap item mechanic
	 *
	 * @reason prevent item duplication
	 */
	@Redirect(
			method = "onMouseClick(I)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/option/KeyBinding;matchesMouse(I)Z",
					ordinal = 0
			)
	)
	public boolean rpginventory$redirect_matchesMouse(KeyBinding instance, int code) {
		return false;
	}

	/**
	 * effectively disables the vanilla swap item mechanic
	 *
	 * @reason prevent item duplication
	 */
	@Redirect(
			method = "handleHotbarKeyPressed",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/option/KeyBinding;matchesKey(II)Z",
					ordinal = 0
			)
	)
	public boolean rpginventory$redirect_matchesKey(KeyBinding instance, int keyCode, int scanCode) {
		return false;
	}

	@Inject(method = "drawSlot", at = @At("TAIL"))
	private void rpginventory$drawSlot(DrawContext context, Slot slot, CallbackInfo ci) {
		// draw slot overlay
		ItemStack stack = slot.getStack();
		if (!stack.isEmpty() && stack.isDamageable() && stack.getDamage() >= stack.getMaxDamage() - 1 && stack.isIn(Tags.UNUSABLE_WHEN_LOW_DURABILITY) && RPGInventoryClient.CLIENT_CONFIG.slots_with_unusable_items_have_overlay.get()) {
			rpginventory$drawDisabledItemSlotHighlight(context, slot.x, slot.y, 0);
		}
	}

	@Unique
	private static void rpginventory$drawDisabledItemSlotHighlight(DrawContext context, int x, int y, int z) {
		RenderSystem.disableDepthTest();
		RenderSystem.colorMask(true, true, true, false);
		ClientConfig clientConfig = RPGInventoryClient.CLIENT_CONFIG;
		context.fillGradient(RenderLayer.getGuiOverlay(), x, y, x + 16, y + 16, clientConfig.first_overlay_colour_for_slots_with_unusable_items.toInt(), clientConfig.second_overlay_colour_for_slots_with_unusable_items.toInt(), z);
		RenderSystem.colorMask(true, true, true, true);
		RenderSystem.enableDepthTest();
	}
}
