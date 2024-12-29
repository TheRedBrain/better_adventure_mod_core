package com.github.theredbrain.rpginventory.network.packet;

import com.github.theredbrain.rpginventory.RPGInventory;
import com.github.theredbrain.rpginventory.entity.player.DuckPlayerEntityMixin;
import com.github.theredbrain.rpginventory.entity.player.DuckPlayerInventoryMixin;
import com.github.theredbrain.rpginventory.registry.Tags;
import com.github.theredbrain.staminaattributes.entity.StaminaUsingEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class ToggleTwoHandedStancePacketReceiver implements ServerPlayNetworking.PlayPayloadHandler<ToggleTwoHandedStancePacket> {
	@Override
	public void receive(ToggleTwoHandedStancePacket payload, ServerPlayNetworking.Context context) {

		ServerPlayerEntity player = context.player();

		ItemStack handItemStack = ((DuckPlayerInventoryMixin) player.getInventory()).rpginventory$getHand().copy();
		if (handItemStack.isEmpty()) {
			handItemStack = ((DuckPlayerInventoryMixin) player.getInventory()).rpginventory$getSheathedHand().copy();
		}
		ItemStack offHandItemStack = player.getInventory().offHand.get(0).copy();
		if (offHandItemStack.isEmpty()) {
			offHandItemStack = ((DuckPlayerInventoryMixin) player.getInventory()).rpginventory$getSheathedOffhand().copy();
		}

		if (RPGInventory.isStaminaAttributesLoaded && RPGInventory.SERVER_CONFIG.staminaAttributesCompat.toggling_two_handed_stance_requires_stamina.get() && ((StaminaUsingEntity) player).staminaattributes$getStamina() <= 0 && !player.isCreative()) {
			player.sendMessageToClient(Text.translatable("hud.message.staminaTooLow"), true);
			return;
		} else if (player.getMainHandStack().isIn(Tags.NON_TWO_HANDED_ITEMS)) {
			player.sendMessageToClient(Text.translatable("hud.message.nonTwoHandedWeaponEquipped"), true);
			return;
		} else if (((DuckPlayerEntityMixin) player).rpginventory$isHandStackSheathed() && ((DuckPlayerEntityMixin) player).rpginventory$isOffhandStackSheathed()) {
			if (!RPGInventory.SERVER_CONFIG.always_allow_toggling_two_handed_stance.get()) {
				player.sendMessageToClient(Text.translatable("hud.message.weaponsAreSheathed"), true);
				return;
			} else {
				((DuckPlayerEntityMixin) player).rpginventory$setIsHandStackSheathed(false);
				((DuckPlayerInventoryMixin) player.getInventory()).rpginventory$setHand(handItemStack);
				((DuckPlayerInventoryMixin) player.getInventory()).rpginventory$setSheathedHand(ItemStack.EMPTY);
			}
		} else if (((DuckPlayerEntityMixin) player).rpginventory$isOffhandStackSheathed()) {
			((DuckPlayerEntityMixin) player).rpginventory$setIsOffhandStackSheathed(false);
			player.getInventory().offHand.set(0, offHandItemStack);
			((DuckPlayerInventoryMixin) player.getInventory()).rpginventory$setSheathedOffhand(ItemStack.EMPTY);
		} else {
			((DuckPlayerEntityMixin) player).rpginventory$setIsOffhandStackSheathed(true);
			player.getInventory().offHand.set(0, ItemStack.EMPTY);
			((DuckPlayerInventoryMixin) player.getInventory()).rpginventory$setSheathedOffhand(offHandItemStack);
		}
		if (RPGInventory.isStaminaAttributesLoaded && !player.isCreative()) {
			((StaminaUsingEntity) player).staminaattributes$addStamina(-RPGInventory.SERVER_CONFIG.staminaAttributesCompat.toggling_two_handed_stance_stamina_cost.get());
		}
		player.getServerWorld().playSound(null, player.getBlockPos().getX(), player.getBlockPos().getY(), player.getBlockPos().getZ(), SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, SoundCategory.PLAYERS, 1.0F, 1.0F);
	}
}
