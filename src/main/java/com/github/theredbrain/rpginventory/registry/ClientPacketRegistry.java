package com.github.theredbrain.rpginventory.registry;

import com.github.theredbrain.rpginventory.network.packet.SheathedWeaponsPacket;
import com.github.theredbrain.rpginventory.network.packet.SheathedWeaponsPacketReceiver;
import com.github.theredbrain.rpginventory.network.packet.SwappedHandItemsPacket;
import com.github.theredbrain.rpginventory.network.packet.SwappedHandItemsPacketReceiver;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT)
public class ClientPacketRegistry {

	public static void init() {

		ClientPlayNetworking.registerGlobalReceiver(SwappedHandItemsPacket.PACKET_ID, new SwappedHandItemsPacketReceiver());

		ClientPlayNetworking.registerGlobalReceiver(SheathedWeaponsPacket.PACKET_ID, new SheathedWeaponsPacketReceiver());

	}
}
