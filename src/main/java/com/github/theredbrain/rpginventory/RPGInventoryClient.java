package com.github.theredbrain.rpginventory;

import com.github.theredbrain.backpackattribute.BackpackAttributeClient;
import com.github.theredbrain.playerattributescreen.PlayerAttributeScreenClient;
import com.github.theredbrain.rpgcrafting.RPGCraftingClient;
import com.github.theredbrain.rpginventory.config.ClientConfig;
import com.github.theredbrain.rpginventory.registry.ClientEventsRegistry;
import com.github.theredbrain.rpginventory.registry.ClientPacketRegistry;
import com.github.theredbrain.rpginventory.registry.KeyBindingsRegistry;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.ArrayList;
import java.util.List;


public class RPGInventoryClient implements ClientModInitializer {
	public static ClientConfig CLIENT_CONFIG;

	public RPGInventoryClient() {
		CLIENT_CONFIG = ConfigApiJava.registerAndLoadConfig(ClientConfig::new, RegisterType.CLIENT);
	}

	public static List<MutablePair<Text, List<Text>>> getPlayerAttributeScreenData(MinecraftClient client) {
		List<MutablePair<Text, List<Text>>> newData = new ArrayList<>(List.of());
		if (RPGInventory.isPlayerAttributeScreenLoaded) {
			newData = PlayerAttributeScreenClient.getPlayerAttributeScreenData(client);
		}
		return newData;
	}

	public static void openBackPackScreen(MinecraftClient client) {
		if (RPGInventory.isBackpackAttributeLoaded) {
			BackpackAttributeClient.openBackpackScreen(client);
		} else if (client.player != null) {
			client.player.sendMessage(Text.translatable("hud.message.backpackAttributesNotInstalled"));
		}
	}

	public static void openHandCraftingScreen(MinecraftClient client) {
		if (RPGInventory.isRPGCraftingLoaded) {
			RPGCraftingClient.openHandCraftingScreen(client);
		} else if (client.player != null) {
			client.player.sendMessage(Text.translatable("hud.message.rpgCraftingNotInstalled"));
		}
	}

	@Override
	public void onInitializeClient() {
		// Packets
		ClientPacketRegistry.init();

		// Registry
		ClientEventsRegistry.initializeClientEvents();
		KeyBindingsRegistry.registerKeyBindings();
	}
}
