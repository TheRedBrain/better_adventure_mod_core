package com.github.theredbrain.rpginventory.network.packet;

import com.github.theredbrain.rpginventory.RPGInventory;
import com.github.theredbrain.rpginventory.entity.player.DuckPlayerEntityMixin;
import com.github.theredbrain.rpginventory.entity.player.DuckPlayerInventoryMixin;
import com.github.theredbrain.staminaattributes.entity.StaminaUsingEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class SwapHandItemsPacketReceiver implements ServerPlayNetworking.PlayPayloadHandler<SwapHandItemsPacket> {
	@Override
	public void receive(SwapHandItemsPacket payload, ServerPlayNetworking.Context context) {

		boolean mainHand = payload.mainHand();
		ServerPlayerEntity player = context.player();

		ItemStack itemStack;
		ItemStack alternativeItemStack;

		boolean handIsSheathed = ((DuckPlayerEntityMixin) player).rpginventory$isHandStackSheathed();
		boolean offHandIsSheathed = ((DuckPlayerEntityMixin) player).rpginventory$isOffhandStackSheathed();

		if (mainHand) {
			itemStack = handIsSheathed ? ((DuckPlayerInventoryMixin) player.getInventory()).rpginventory$getSheathedHand().copy() : ((DuckPlayerInventoryMixin) player.getInventory()).rpginventory$getHand().copy();
			alternativeItemStack = ((DuckPlayerInventoryMixin) player.getInventory()).rpginventory$getAlternativeHand().copy();
		} else {
			itemStack = offHandIsSheathed ? ((DuckPlayerInventoryMixin) player.getInventory()).rpginventory$getSheathedOffhand().copy() : player.getInventory().offHand.get(0).copy();
			alternativeItemStack = ((DuckPlayerInventoryMixin) player.getInventory()).rpginventory$getAlternativeOffhand().copy();
		}

		if (itemStack.isEmpty() && alternativeItemStack.isEmpty()) {
			return;
		}
		if (RPGInventory.isStaminaAttributesLoaded && RPGInventory.SERVER_CONFIG.swapping_hand_items_requires_stamina.get() && ((StaminaUsingEntity) player).staminaattributes$getStamina() <= 0 && !player.isCreative()) {
			player.sendMessageToClient(Text.translatable("hud.message.staminaTooLow"), true);
			return;
		}

		if (mainHand) {
			if (handIsSheathed) {
				((DuckPlayerInventoryMixin) player.getInventory()).rpginventory$setSheathedHand(alternativeItemStack);
			} else {
				((DuckPlayerInventoryMixin) player.getInventory()).rpginventory$setHand(alternativeItemStack);
			}
			((DuckPlayerInventoryMixin) player.getInventory()).rpginventory$setAlternativeHand(itemStack);
		} else {
			if (offHandIsSheathed) {
				((DuckPlayerInventoryMixin) player.getInventory()).rpginventory$setSheathedOffhand(alternativeItemStack);
			} else {
				player.getInventory().offHand.set(0, alternativeItemStack);
			}
			((DuckPlayerInventoryMixin) player.getInventory()).rpginventory$setAlternativeOffhand(itemStack);
		}
		if (RPGInventory.isStaminaAttributesLoaded && !player.isCreative()) {
			((StaminaUsingEntity) player).staminaattributes$addStamina(-RPGInventory.SERVER_CONFIG.swapping_hand_items_stamina_cost.get());
		}
		player.getServerWorld().playSound(null, player.getBlockPos().getX(), player.getBlockPos().getY(), player.getBlockPos().getZ(), SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, SoundCategory.PLAYERS, 1.0F, 1.0F);
	}
}
