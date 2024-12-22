package com.github.theredbrain.rpginventory;

import com.github.theredbrain.inventorysizeattributes.entity.player.DuckPlayerEntityMixin;
import com.github.theredbrain.rpginventory.config.ServerConfig;
import com.github.theredbrain.rpginventory.registry.GameRulesRegistry;
import com.github.theredbrain.rpginventory.registry.ItemRegistry;
import com.github.theredbrain.rpginventory.registry.PredicateRegistry;
import com.github.theredbrain.rpginventory.registry.ServerPacketRegistry;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RPGInventory implements ModInitializer {
	public static final String MOD_ID = "rpginventory";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static ServerConfig SERVER_CONFIG = ConfigApiJava.registerAndLoadConfig(ServerConfig::new);

	public static RegistryEntry<EntityAttribute> ACTIVE_SPELL_SLOT_AMOUNT;

	public static final boolean isRPGCraftingLoaded = FabricLoader.getInstance().isModLoaded("rpgcrafting");
	public static final boolean isBackpackAttributeLoaded = FabricLoader.getInstance().isModLoaded("backpackattribute");
	public static final boolean isFoodOverhaulLoaded = FabricLoader.getInstance().isModLoaded("foodoverhaul");
	public static final boolean isStaminaAttributesLoaded = FabricLoader.getInstance().isModLoaded("staminaattributes");
	public static final boolean isInventorySizeAttributesLoaded = FabricLoader.getInstance().isModLoaded("inventorysizeattributes");

	public static int getActiveInventorySize(PlayerEntity player) {
		return isInventorySizeAttributesLoaded ? ((DuckPlayerEntityMixin) player).inventorysizeattributes$getActiveInventorySlotAmount() : 27;
	}

	public static int getActiveHotbarSize(PlayerEntity player) {
		return isInventorySizeAttributesLoaded ? ((DuckPlayerEntityMixin) player).inventorysizeattributes$getActiveHotbarSlotAmount() : 9;
	}

	public static final boolean isPlayerAttributeScreenLoaded = FabricLoader.getInstance().isModLoaded("playerattributescreen");

	@Override
	public void onInitialize() {
		LOGGER.info("We are going on an adventure!");

		// Packets
		ServerPacketRegistry.init();

		// Registry
		ItemRegistry.init();
		GameRulesRegistry.init();
		PredicateRegistry.init();
	}

	public static Identifier identifier(String path) {
		return Identifier.of(MOD_ID, path);
	}

	public static void info(String message) {
		LOGGER.info("[" + MOD_ID + "] [info]: " + message);
	}

	public static void warn(String message) {
		LOGGER.warn("[" + MOD_ID + "] [warn]: " + message);
	}

	public static void debug(String message) {
		LOGGER.debug("[" + MOD_ID + "] [debug]: " + message);
	}

	public static void error(String message) {
		LOGGER.error("[" + MOD_ID + "] [error]: " + message);
	}
}
