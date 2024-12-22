package com.github.theredbrain.rpginventory.client.gui.screen.ingame;

import com.github.theredbrain.foodoverhaul.effect.FoodStatusEffect;
import com.github.theredbrain.rpginventory.RPGInventory;
import com.github.theredbrain.rpginventory.RPGInventoryClient;
import com.github.theredbrain.rpginventory.client.gui.widget.ToggleInventoryScreenWidget;
import com.github.theredbrain.rpginventory.config.ClientConfig;
import com.github.theredbrain.rpginventory.config.ServerConfig;
import com.github.theredbrain.rpginventory.entity.player.DuckPlayerEntityMixin;
import com.github.theredbrain.rpginventory.screen.DuckPlayerScreenHandlerMixin;
import com.github.theredbrain.rpginventory.screen.DuckSlotMixin;
import com.github.theredbrain.slotcustomizationapi.api.SlotCustomization;
import com.google.common.collect.Ordering;
import dev.emi.trinkets.Point;
import dev.emi.trinkets.TrinketPlayerScreenHandler;
import dev.emi.trinkets.TrinketScreen;
import dev.emi.trinkets.TrinketScreenManager;
import dev.emi.trinkets.api.SlotGroup;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class RPGInventoryScreen extends HandledScreen<PlayerScreenHandler> implements TrinketScreen {
	public static final Identifier ADVENTURE_INVENTORY_MAIN_BACKGROUND_TEXTURE = RPGInventory.identifier("textures/gui/container/adventure_inventory/adventure_inventory_main_background.png");
	public static final Identifier ADVENTURE_INVENTORY_SIDES_BACKGROUND_TEXTURE = RPGInventory.identifier("textures/gui/container/adventure_inventory/adventure_inventory_sides_background.png");
	public static final Identifier SLOT_TEXTURE = Identifier.ofVanilla("textures/gui/sprites/container/slot.png");
	private static final Identifier EFFECT_BACKGROUND_SMALL_TEXTURE = Identifier.ofVanilla("container/inventory/effect_background_small");
	private static final Identifier CRAFTING_SLOTS_BACKGROUND_TEXTURE = RPGInventory.identifier("textures/gui/container/adventure_inventory/adventure_inventory_crafting_background.png");
	private static final Identifier SCROLL_BAR_BACKGROUND_8_206_TEXTURE = RPGInventory.identifier("textures/gui/sprites/scroll_bar/scroll_bar_background_8_206.png");
	private static final Identifier SCROLL_BAR_BACKGROUND_8_32_TEXTURE = RPGInventory.identifier("textures/gui/sprites/scroll_bar/scroll_bar_background_8_32.png");
	private static final Identifier SCROLLER_VERTICAL_6_7_TEXTURE = RPGInventory.identifier("textures/gui/sprites/scroll_bar/scroller_vertical_6_7.png");
	private static final Identifier SCROLLER_VERTICAL_6_15_TEXTURE = RPGInventory.identifier("textures/gui/sprites/scroll_bar/scroller_vertical_6_15.png");
	private static final Text CRAFTING_LABEL_TEXT = Text.translatable("gui.adventure_inventory_screen.crafting_label");
	private static final Text SPELLS_LABEL_TEXT = Text.translatable("gui.adventure_inventory_screen.spells_label");
	private static final Text TOGGLE_SHOW_ATTRIBUTES_BUTTON_TOOLTIP_TEXT_OFF = Text.translatable("gui.adventureInventory.toggleShowAttributeScreenButton.off.tooltip");
	private static final Text TOGGLE_SHOW_ATTRIBUTES_BUTTON_TOOLTIP_TEXT_ON = Text.translatable("gui.adventureInventory.toggleShowAttributeScreenButton.on.tooltip");
	private static final Text TOGGLE_SHOW_EFFECTS_BUTTON_TOOLTIP_TEXT_OFF = Text.translatable("gui.adventureInventory.toggleShowEffectScreenButton.off.tooltip");
	private static final Text TOGGLE_SHOW_EFFECTS_BUTTON_TOOLTIP_TEXT_ON = Text.translatable("gui.adventureInventory.toggleShowEffectScreenButton.on.tooltip");
	private static final Text OPEN_BACKPACK_BUTTON_LABEL_TEXT = Text.translatable("gui.adventureInventory.openBackpackButton");
	private static final Text OPEN_HAND_CRAFTING_BUTTON_LABEL_TEXT = Text.translatable("gui.adventureInventory.openHandCraftingButton");
	private static final int MAX_ATTRIBUTE_SCREEN_LINES = 15;
	private float mouseX;
	private float mouseY;
	private ButtonWidget openBackpackButton;
	private ButtonWidget openHandCraftingButton;
	private final int sidesBackgroundWidth = 130;
	private ButtonWidget toggleShowAttributeScreenButton;
	private boolean showAttributeScreen;
	private ButtonWidget toggleShowEffectScreenButton;
	private boolean showEffectScreen;
	private int attributeListSize = 0;
	private int oldEffectsListSize = 0;
	private List<StatusEffectInstance> foodEffectsList = new ArrayList<>(Collections.emptyList());
	private List<StatusEffectInstance> negativeEffectsList = new ArrayList<>(Collections.emptyList());
	private List<StatusEffectInstance> positiveEffectsList = new ArrayList<>(Collections.emptyList());
	private List<StatusEffectInstance> neutralEffectsList = new ArrayList<>(Collections.emptyList());
	private int attributeScrollPosition = 0;
	private int foodScrollPosition = 0;
	private int negativeScrollPosition = 0;
	private int positiveScrollPosition = 0;
	private int neutralScrollPosition = 0;
	private int foodEffectsRowAmount = 1;
	private int negativeEffectsRowAmount = 1;
	private int positiveEffectsRowAmount = 1;
	private int neutralEffectsRowAmount = 1;
	private float attributeScrollAmount = 0.0f;
	private float foodScrollAmount = 0.0f;
	private float negativeScrollAmount = 0.0f;
	private float positiveScrollAmount = 0.0f;
	private float neutralScrollAmount = 0.0f;
	private boolean foodMouseClicked = false;
	private boolean attributeMouseClicked = false;
	private boolean negativeMouseClicked = false;
	private boolean positiveMouseClicked = false;
	private boolean neutralMouseClicked = false;

	public RPGInventoryScreen(PlayerEntity player) {
		super(player.playerScreenHandler, player.getInventory(), Text.translatable("gui.adventure_inventory_screen.equipment_label"));
		this.backgroundWidth = 176;
		this.backgroundHeight = 220;
		this.titleX = 8;
		this.titleY = 6;
		this.playerInventoryTitleX = 8;
		this.playerInventoryTitleY = this.backgroundHeight - 93;
	}

	public void handledScreenTick() {
		TrinketScreenManager.tick();
	}

	private void updateEffectsLists(PlayerEntity player) {
		List<StatusEffectInstance> effectsList = Ordering.natural().sortedCopy(player.getStatusEffects());
		List<StatusEffectInstance> visibleEffectsList = new ArrayList<>(Collections.emptyList());
		for (StatusEffectInstance statusEffectInstance : effectsList) {
			if (statusEffectInstance.shouldShowIcon()) {
				visibleEffectsList.add(statusEffectInstance);
			}
		}
		int visibleEffectsListSize = visibleEffectsList.size();
		if (visibleEffectsListSize == 0) {
			this.oldEffectsListSize = 0;
			this.foodEffectsList.clear();
			this.negativeEffectsList.clear();
			this.positiveEffectsList.clear();
			this.neutralEffectsList.clear();
			return;
		}
		boolean bl = false;

		// determine if the UI should be updated
		if (visibleEffectsListSize != this.oldEffectsListSize) {
			bl = true;
			this.oldEffectsListSize = visibleEffectsListSize;
		}

		if (bl) {
			this.foodEffectsList.clear();
			this.negativeEffectsList.clear();
			this.positiveEffectsList.clear();
			this.neutralEffectsList.clear();

			this.foodScrollPosition = 0;
			this.negativeScrollPosition = 0;
			this.positiveScrollPosition = 0;
			this.neutralScrollPosition = 0;

			this.foodScrollAmount = 0.0f;
			this.negativeScrollAmount = 0.0f;
			this.positiveScrollAmount = 0.0f;
			this.neutralScrollAmount = 0.0f;

			for (StatusEffectInstance statusEffectInstance : visibleEffectsList) {
				if (RPGInventory.isFoodOverhaulLoaded && statusEffectInstance.getEffectType().value() instanceof FoodStatusEffect) {
					this.foodEffectsList.add(statusEffectInstance);
				} else if (statusEffectInstance.getEffectType().value().getCategory() == StatusEffectCategory.HARMFUL) {
					this.negativeEffectsList.add(statusEffectInstance);
				} else if (statusEffectInstance.getEffectType().value().getCategory() == StatusEffectCategory.BENEFICIAL) {
					this.positiveEffectsList.add(statusEffectInstance);
				} else if (statusEffectInstance.getEffectType().value().getCategory() == StatusEffectCategory.NEUTRAL) {
					this.neutralEffectsList.add(statusEffectInstance);
				}
			}
			this.foodEffectsRowAmount = Math.max(1, this.calculateStatusEffectRowAmount(this.foodEffectsList.size()));
			this.negativeEffectsRowAmount = Math.max(1, this.calculateStatusEffectRowAmount(this.negativeEffectsList.size()));
			this.positiveEffectsRowAmount = Math.max(1, this.calculateStatusEffectRowAmount(this.positiveEffectsList.size()));
			this.neutralEffectsRowAmount = Math.max(1, this.calculateStatusEffectRowAmount(this.neutralEffectsList.size()));
		}
		visibleEffectsList.clear();
	}

	private int calculateStatusEffectRowAmount(int statusEffectListSize) {
		return statusEffectListSize / 3 + (statusEffectListSize % 3 > 0 ? 1 : 0);
	}

	private void openBackpack() {
		if (this.client != null) {
			RPGInventoryClient.openBackPackScreen(this.client);
		}
	}

	private void openHandCraftingScreen() {
		if (this.client != null) {
			RPGInventoryClient.openHandCraftingScreen(this.client);
		}
	}

	private void toggleShowAttributeScreen() {
		this.showAttributeScreen = !this.showAttributeScreen;
		((ToggleInventoryScreenWidget) this.toggleShowAttributeScreenButton).setIsPressed(this.showAttributeScreen);
		this.toggleShowAttributeScreenButton.setTooltip(this.showAttributeScreen ? Tooltip.of(TOGGLE_SHOW_ATTRIBUTES_BUTTON_TOOLTIP_TEXT_ON) : Tooltip.of(TOGGLE_SHOW_ATTRIBUTES_BUTTON_TOOLTIP_TEXT_OFF));
		((DuckPlayerScreenHandlerMixin) this.handler).rpginventory$setIsAttributeScreenVisible(this.showAttributeScreen);
	}

	private void toggleShowEffectScreen() {
		this.showEffectScreen = !this.showEffectScreen;
		((ToggleInventoryScreenWidget) this.toggleShowEffectScreenButton).setIsPressed(this.showEffectScreen);
		this.toggleShowEffectScreenButton.setTooltip(this.showEffectScreen ? Tooltip.of(TOGGLE_SHOW_EFFECTS_BUTTON_TOOLTIP_TEXT_ON) : Tooltip.of(TOGGLE_SHOW_EFFECTS_BUTTON_TOOLTIP_TEXT_OFF));
	}

	@Override
	protected void init() {
		TrinketScreenManager.init(this);
		if (this.client != null && this.client.player != null) {
			if (this.client.interactionManager != null && this.client.interactionManager.hasCreativeInventory()) {
				this.client.setScreen(new CreativeInventoryScreen(this.client.player, this.client.player.networkHandler.getEnabledFeatures(), this.client.options.getOperatorItemsTab().getValue()));
				return;
			}
			((SlotCustomization) this.handler.slots.get(45)).slotcustomizationapi$setDisabledOverride(((DuckPlayerEntityMixin) this.client.player).rpginventory$isOffhandStackSheathed());
		}
		super.init();
		ClientConfig clientConfig = RPGInventoryClient.CLIENT_CONFIG;
		this.showAttributeScreen = clientConfig.show_attribute_screen_when_opening_inventory_screen.get() && RPGInventory.isPlayerAttributeScreenLoaded;
		((DuckPlayerScreenHandlerMixin) this.handler).rpginventory$setIsAttributeScreenVisible(this.showAttributeScreen);
		this.toggleShowAttributeScreenButton = this.addDrawableChild(new ToggleInventoryScreenWidget(this.x + 6, this.y + 19, this.showAttributeScreen, false, button -> this.toggleShowAttributeScreen()));
		this.toggleShowAttributeScreenButton.setTooltip(Tooltip.of(this.showAttributeScreen ? TOGGLE_SHOW_ATTRIBUTES_BUTTON_TOOLTIP_TEXT_ON : TOGGLE_SHOW_ATTRIBUTES_BUTTON_TOOLTIP_TEXT_OFF));
		this.showEffectScreen = !clientConfig.can_hide_status_effect_screen.get() || clientConfig.show_effect_screen_when_opening_inventory_screen.get();
		this.toggleShowEffectScreenButton = this.addDrawableChild(new ToggleInventoryScreenWidget(this.x + this.backgroundWidth - 29, this.y + 19, this.showEffectScreen, true, button -> this.toggleShowEffectScreen()));
		this.toggleShowEffectScreenButton.setTooltip(Tooltip.of(this.showEffectScreen ? TOGGLE_SHOW_EFFECTS_BUTTON_TOOLTIP_TEXT_ON : TOGGLE_SHOW_EFFECTS_BUTTON_TOOLTIP_TEXT_OFF));
		this.openBackpackButton = this.addDrawableChild(ButtonWidget.builder(OPEN_BACKPACK_BUTTON_LABEL_TEXT, button -> this.openBackpack()).dimensions(this.x + clientConfig.open_backpack_button_offset_x.get(), this.y + clientConfig.open_backpack_button_offset_y.get(), 70, 20).build());
		this.openBackpackButton.visible = RPGInventory.SERVER_CONFIG.disable_inventory_crafting_slots.get() && clientConfig.enable_open_backpack_button.get() && RPGInventory.isBackpackAttributeLoaded;
		this.openHandCraftingButton = this.addDrawableChild(ButtonWidget.builder(OPEN_HAND_CRAFTING_BUTTON_LABEL_TEXT, button -> this.openHandCraftingScreen()).dimensions(this.x + clientConfig.open_hand_crafting_button_offset_x.get(), this.y + clientConfig.open_hand_crafting_button_offset_y.get(), 70, 20).build());
		this.openHandCraftingButton.visible = RPGInventory.SERVER_CONFIG.disable_inventory_crafting_slots.get() && clientConfig.enable_open_hand_crafting_button.get() && RPGInventory.isRPGCraftingLoaded;
		this.toggleShowAttributeScreenButton.visible = RPGInventory.isPlayerAttributeScreenLoaded;
	}

	@Override
	protected void drawMouseoverTooltip(DrawContext context, int x, int y) {
		super.drawMouseoverTooltip(context, x, y);
		if (RPGInventoryClient.CLIENT_CONFIG.show_slot_tooltips.get() && this.handler.getCursorStack().isEmpty() && this.focusedSlot != null && !this.focusedSlot.hasStack()) {
			if (this.focusedSlot instanceof DuckSlotMixin slotWithTooltip) {
				List<Text> list = slotWithTooltip.rpginventory$getSlotTooltipText();
				if (!list.isEmpty()) {
					context.drawTooltip(this.textRenderer, list, Optional.empty(), x, y);
				}
			}
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		TrinketScreenManager.update(mouseX, mouseY);
		super.render(context, mouseX, mouseY, delta);
		this.drawStatusEffects(context, mouseX, mouseY);
		this.drawAttributeScreen(context, mouseX, mouseY);
		this.drawMouseoverTooltip(context, mouseX, mouseY);
		this.mouseX = mouseX;
		this.mouseY = mouseY;
	}

	@Override
	protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
		super.drawForeground(context, mouseX, mouseY);
		TrinketScreenManager.drawActiveGroup(context);
	}

	@Override
	protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
		int i = this.x;
		int j = this.y;
		int k;
		int m;
		int activeSpellSlotAmount = 0;
		int inventorySize = 0;
		int hotbarSize = 0;
		ClientConfig clientConfig = RPGInventoryClient.CLIENT_CONFIG;
		ServerConfig serverConfig = RPGInventory.SERVER_CONFIG;
		if (this.client != null && this.client.player != null) {
			activeSpellSlotAmount = (int) ((DuckPlayerEntityMixin) this.client.player).rpginventory$getActiveSpellSlotAmount();

			hotbarSize = RPGInventory.getActiveHotbarSize(this.client.player);
			inventorySize = RPGInventory.getActiveInventorySize(this.client.player);
			updateEffectsLists(this.client.player);
		}
		context.drawTexture(ADVENTURE_INVENTORY_MAIN_BACKGROUND_TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight, this.backgroundWidth, this.backgroundHeight);
		if (!serverConfig.disable_inventory_crafting_slots.get()) {
			context.drawText(this.textRenderer, CRAFTING_LABEL_TEXT, i + serverConfig.inventory_crafting_slots_x_offset.get(), j + serverConfig.inventory_crafting_slots_y_offset.get() - 11, 4210752, false);
			context.drawTexture(CRAFTING_SLOTS_BACKGROUND_TEXTURE, i + serverConfig.inventory_crafting_slots_x_offset.get() - 1, j + serverConfig.inventory_crafting_slots_y_offset.get() - 1, 0, 0, 74, 36, 74, 36);
		}
		if (activeSpellSlotAmount > 0) {
			context.drawText(this.textRenderer, SPELLS_LABEL_TEXT, i + serverConfig.spell_slots_x_offset.get(), j + serverConfig.spell_slots_y_offset.get() - 11, 4210752, false);
			for (k = 0; k < activeSpellSlotAmount; k++) {
				context.drawTexture(SLOT_TEXTURE, i + serverConfig.spell_slots_x_offset.get() - 1 + ((k % 4) * 18), j + serverConfig.spell_slots_y_offset.get() - 1 + ((k / 4) * 18), 0, 0, 18, 18, 18, 18);
			}
		}
		context.drawTexture(SLOT_TEXTURE, i + serverConfig.head_slot_x_offset.get() - 1, j + serverConfig.head_slot_y_offset.get() - 1, 0, 0, 18, 18, 18, 18);
		context.drawTexture(SLOT_TEXTURE, i + serverConfig.chest_slot_x_offset.get() - 1, j + serverConfig.chest_slot_y_offset.get() - 1, 0, 0, 18, 18, 18, 18);
		context.drawTexture(SLOT_TEXTURE, i + serverConfig.legs_slot_x_offset.get() - 1, j + serverConfig.legs_slot_y_offset.get() - 1, 0, 0, 18, 18, 18, 18);
		context.drawTexture(SLOT_TEXTURE, i + serverConfig.feet_slot_x_offset.get() - 1, j + serverConfig.feet_slot_y_offset.get() - 1, 0, 0, 18, 18, 18, 18);
		context.drawTexture(SLOT_TEXTURE, i + serverConfig.belts_group_x_offset.get() - 1, j + serverConfig.belts_group_y_offset.get() - 1, 0, 0, 18, 18, 18, 18);
		context.drawTexture(SLOT_TEXTURE, i + serverConfig.shoulders_group_x_offset.get() - 1, j + serverConfig.shoulders_group_y_offset.get() - 1, 0, 0, 18, 18, 18, 18);
		context.drawTexture(SLOT_TEXTURE, i + serverConfig.necklaces_group_x_offset.get() - 1, j + serverConfig.necklaces_group_y_offset.get() - 1, 0, 0, 18, 18, 18, 18);
		context.drawTexture(SLOT_TEXTURE, i + serverConfig.rings_1_group_x_offset.get() - 1, j + serverConfig.rings_1_group_y_offset.get() - 1, 0, 0, 18, 18, 18, 18);
		context.drawTexture(SLOT_TEXTURE, i + serverConfig.rings_2_group_x_offset.get() - 1, j + serverConfig.rings_2_group_y_offset.get() - 1, 0, 0, 18, 18, 18, 18);
		context.drawTexture(SLOT_TEXTURE, i + serverConfig.gloves_group_x_offset.get() - 1, j + serverConfig.gloves_group_y_offset.get() - 1, 0, 0, 18, 18, 18, 18);
		context.drawTexture(SLOT_TEXTURE, i + serverConfig.hand_group_x_offset.get() - 1, j + serverConfig.hand_group_y_offset.get() - 1, 0, 0, 18, 18, 18, 18);
		context.drawTexture(SLOT_TEXTURE, i + serverConfig.offhand_slot_x_offset.get() - 1, j + serverConfig.offhand_slot_y_offset.get() - 1, 0, 0, 18, 18, 18, 18);
		context.drawTexture(SLOT_TEXTURE, i + serverConfig.alternative_hand_group_x_offset.get() - 1, j + serverConfig.alternative_hand_group_y_offset.get() - 1, 0, 0, 18, 18, 18, 18);
		context.drawTexture(SLOT_TEXTURE, i + serverConfig.alternative_offhand_group_x_offset.get() - 1, j + serverConfig.alternative_offhand_group_y_offset.get() - 1, 0, 0, 18, 18, 18, 18);

		boolean showInactiveSlots = clientConfig.show_inactive_inventory_slots.get();
		for (k = 0; k < (showInactiveSlots ? 27 : Math.min(inventorySize, 27)); ++k) {
			m = (k / 9);
			context.drawTexture(SLOT_TEXTURE, i + 7 + (k - (m * 9)) * 18, j + 137 + (m * 18), 0, 0, 18, 18, 18, 18);
		}
		for (k = 0; k < (showInactiveSlots ? 9 : Math.min(hotbarSize, 9)); ++k) {
			context.drawTexture(SLOT_TEXTURE, i + 7 + k * 18, j + 195, 0, 0, 18, 18, 18, 18);
		}

		if (this.showAttributeScreen) {
			context.drawTexture(ADVENTURE_INVENTORY_SIDES_BACKGROUND_TEXTURE, i - this.sidesBackgroundWidth, j, 0, 0, this.sidesBackgroundWidth, this.backgroundHeight, this.sidesBackgroundWidth, this.backgroundHeight);
		}
		if (this.oldEffectsListSize > 0 && (this.showEffectScreen || !clientConfig.can_hide_status_effect_screen.get())) {
			context.drawTexture(ADVENTURE_INVENTORY_SIDES_BACKGROUND_TEXTURE, i + this.backgroundWidth, j, 0, 0, this.sidesBackgroundWidth, this.backgroundHeight, this.sidesBackgroundWidth, this.backgroundHeight);
		}
		this.toggleShowEffectScreenButton.visible = clientConfig.can_hide_status_effect_screen.get() && this.oldEffectsListSize > 0;
		if (this.client != null && this.client.player != null) {
			InventoryScreen.drawEntity(context, i + 26, j + 36, i + 75, j + 106, 30, 0.0625f, this.mouseX, this.mouseY, this.client.player);
		}
		TrinketScreenManager.drawExtraGroups(context);
	}

	private void drawAttributeScreen(DrawContext context, int mouseX, int mouseY) {
		if (!this.showAttributeScreen) {
			return;
		}
		int x = this.x - this.sidesBackgroundWidth + 7;
		int y = this.y + 7;
		int currentY;
		List<MutablePair<Text, List<Text>>> list = RPGInventoryClient.getPlayerAttributeScreenData(this.client);
		this.attributeListSize = list.size();
		for (int i = this.attributeScrollPosition; i < Math.min(this.attributeListSize, this.attributeScrollPosition + 15); i++) {
			currentY = y + ((i - this.attributeScrollPosition) * 13);
			context.drawText(this.textRenderer, list.get(i).left, x, currentY, 0x404040, false);
			if (mouseX >= x && mouseX <= x + this.sidesBackgroundWidth - 7 && mouseY >= currentY && mouseY <= currentY + 13) {
				List<Text> tooltipList = list.get(i).right;
				if (!tooltipList.isEmpty()) {
					context.drawTooltip(this.textRenderer, tooltipList, Optional.empty(), mouseX, mouseY);
				}
			}
		}
		if (list.size() > MAX_ATTRIBUTE_SCREEN_LINES) {
			context.drawTexture(SCROLL_BAR_BACKGROUND_8_206_TEXTURE, x + 108, y, 0, 0, 8, 206, 8, 206);
			int k = (int) (189.0f * this.attributeScrollAmount);
			context.drawTexture(SCROLLER_VERTICAL_6_15_TEXTURE, x + 109, y + 1 + k, 0, 0, 6, 15, 6, 15);
		}
	}

	private void drawStatusEffects(DrawContext context, int mouseX, int mouseY) {
		int i = this.x + this.backgroundWidth + 7;
		int j = this.y + 7;
		if (this.showEffectScreen) {
			if (this.oldEffectsListSize > 0) {
				context.drawText(this.textRenderer, Text.translatable("gui.adventureInventory.status_effects"), i + 1, j, 0x404040, false);
			}
			j += 13;
			if (!this.foodEffectsList.isEmpty()) {

				context.drawText(this.textRenderer, Text.translatable("gui.adventureInventory.status_effects.food_effects"), i + 1, j, 0x404040, false);
				j += 13;
				for (int k = 3 * this.foodScrollPosition; k < Math.min(this.foodEffectsList.size(), (3 * (1 + this.foodScrollPosition))); k++) {
					drawStatusEffectTexturesAndToolTips(context, i, j, k, mouseX, mouseY, this.foodEffectsList.get(k));
				}
				if (this.foodEffectsRowAmount > 1) {
					context.drawTexture(SCROLL_BAR_BACKGROUND_8_32_TEXTURE, i + 109, j, 0, 0, 8, 32, 8, 32);
					int k = (int) (23.0f * this.foodScrollAmount);
					context.drawTexture(SCROLLER_VERTICAL_6_7_TEXTURE, i + 110, j + 1 + k, 0, 0, 6, 7, 6, 7);
				}
				j += 37;
			}
			if (!this.negativeEffectsList.isEmpty()) {

				context.drawText(this.textRenderer, Text.translatable("gui.adventureInventory.status_effects.negative_effects"), i + 1, j, 0x404040, false);
				j += 13;
				for (int k = 3 * this.negativeScrollPosition; k < Math.min(this.negativeEffectsList.size(), (3 * (1 + this.negativeScrollPosition))); k++) {
					drawStatusEffectTexturesAndToolTips(context, i, j, k, mouseX, mouseY, this.negativeEffectsList.get(k));
				}
				if (this.negativeEffectsRowAmount > 1) {
					context.drawTexture(SCROLL_BAR_BACKGROUND_8_32_TEXTURE, i + 109, j, 0, 0, 8, 32, 8, 32);
					int k = (int) (23.0f * this.negativeScrollAmount);
					context.drawTexture(SCROLLER_VERTICAL_6_7_TEXTURE, i + 110, j + 1 + k, 0, 0, 6, 7, 6, 7);
				}
				j += 37;
			}
			if (!this.positiveEffectsList.isEmpty()) {

				context.drawText(this.textRenderer, Text.translatable("gui.adventureInventory.status_effects.positive_effects"), i + 1, j, 0x404040, false);
				j += 13;
				for (int k = 3 * this.positiveScrollPosition; k < Math.min(this.positiveEffectsList.size(), (3 * (1 + this.positiveScrollPosition))); k++) {
					drawStatusEffectTexturesAndToolTips(context, i, j, k, mouseX, mouseY, this.positiveEffectsList.get(k));
				}
				if (this.positiveEffectsRowAmount > 1) {
					context.drawTexture(SCROLL_BAR_BACKGROUND_8_32_TEXTURE, i + 109, j, 0, 0, 8, 32, 8, 32);
					int k = (int) (23.0f * this.positiveScrollAmount);
					context.drawTexture(SCROLLER_VERTICAL_6_7_TEXTURE, i + 110, j + 1 + k, 0, 0, 6, 7, 6, 7);
				}
				j += 37;
			}
			if (!this.neutralEffectsList.isEmpty()) {

				context.drawText(this.textRenderer, Text.translatable("gui.adventureInventory.status_effects.neutral_effects"), i + 1, j, 0x404040, false);
				j += 13;
				for (int k = 3 * this.neutralScrollPosition; k < Math.min(this.neutralEffectsList.size(), (3 * (1 + this.neutralScrollPosition))); k++) {
					drawStatusEffectTexturesAndToolTips(context, i, j, k, mouseX, mouseY, this.neutralEffectsList.get(k));
				}
				if (this.neutralEffectsRowAmount > 1) {
					context.drawTexture(SCROLL_BAR_BACKGROUND_8_32_TEXTURE, i + 109, j, 0, 0, 8, 32, 8, 32);
					int k = (int) (23.0f * this.neutralScrollAmount);
					context.drawTexture(SCROLLER_VERTICAL_6_7_TEXTURE, i + 110, j + 1 + k, 0, 0, 6, 7, 6, 7);
				}
			}
		}
	}

	private void drawStatusEffectTexturesAndToolTips(DrawContext context, int x, int y, int z, int mouseX, int mouseY, StatusEffectInstance statusEffectInstance) {
		if (this.client != null) {
			StatusEffectSpriteManager statusEffectSpriteManager = this.client.getStatusEffectSpriteManager();
			int i = x + 3 + ((z % 3) * 35);
			context.drawGuiTexture(EFFECT_BACKGROUND_SMALL_TEXTURE, i, y, 32, 32);
			Sprite sprite = statusEffectSpriteManager.getSprite(statusEffectInstance.getEffectType());
			context.drawSprite(i + 7, y + 7, 0, 18, 18, sprite);
			if (mouseX >= i && mouseX <= i + 32 && mouseY >= y && mouseY <= y + 32) {
				List<Text> list = getStatusEffectTooltip(statusEffectInstance);
				context.drawTooltip(this.textRenderer, list, Optional.empty(), mouseX, mouseY);
			}
		}
	}

	private List<Text> getStatusEffectTooltip(StatusEffectInstance statusEffectInstance) {
		List<Text> list = new ArrayList<>(List.of(getStatusEffectName(statusEffectInstance)));
		if (!(statusEffectInstance.isInfinite()) && this.client != null && this.client.world != null) {
			list.add(StatusEffectUtil.getDurationText(statusEffectInstance, 1.0f, this.client.world.getTickManager().getTickRate()));
		}
		Text description = getStatusEffectDescription(statusEffectInstance);
		if (!description.getString().isEmpty()) {
			list.add(description);
		}
		return list;
	}

	private Text getStatusEffectName(StatusEffectInstance statusEffectInstance) {
		MutableText mutableText = statusEffectInstance.getEffectType().value().getName().copy();
		if (statusEffectInstance.getAmplifier() >= 1 && statusEffectInstance.getAmplifier() <= 9) {
			mutableText.append(ScreenTexts.SPACE).append(Text.translatable("enchantment.level." + (statusEffectInstance.getAmplifier() + 1)));
		}
		return mutableText;
	}

	private Text getStatusEffectDescription(StatusEffectInstance statusEffectInstance) {
		String translationKey = statusEffectInstance.getEffectType().value().getTranslationKey() + ".description";
		Text description = Text.translatable(translationKey);
		if (description.getString().equals(translationKey)) {
			description = Text.empty();
		}
		return description;
	}

	@Override
	protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
		return super.isClickOutsideBounds(mouseX, mouseY, left, top, button) && !(TrinketScreenManager.isClickInsideTrinketBounds(mouseX, mouseY));
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		boolean bool = this.showAttributeScreen;
		boolean bool1 = this.showEffectScreen;
		int number = this.attributeScrollPosition;
		int number1 = this.foodScrollPosition;
		int number2 = this.negativeScrollPosition;
		int number3 = this.positiveScrollPosition;
		int number4 = this.neutralScrollPosition;
		float number5 = this.attributeScrollAmount;
		float number6 = this.foodScrollAmount;
		float number7 = this.negativeScrollAmount;
		float number8 = this.positiveScrollAmount;
		float number9 = this.neutralScrollAmount;
		this.init(client, width, height);
		this.showAttributeScreen = bool;
		this.showEffectScreen = bool1;
		this.attributeScrollPosition = number;
		this.foodScrollPosition = number1;
		this.negativeScrollPosition = number2;
		this.positiveScrollPosition = number3;
		this.neutralScrollPosition = number4;
		this.attributeScrollAmount = number5;
		this.foodScrollAmount = number6;
		this.negativeScrollAmount = number7;
		this.positiveScrollAmount = number8;
		this.neutralScrollAmount = number9;
		((ToggleInventoryScreenWidget) this.toggleShowAttributeScreenButton).setIsPressed(this.showAttributeScreen);
		this.toggleShowAttributeScreenButton.setTooltip(this.showAttributeScreen ? Tooltip.of(TOGGLE_SHOW_ATTRIBUTES_BUTTON_TOOLTIP_TEXT_ON) : Tooltip.of(TOGGLE_SHOW_ATTRIBUTES_BUTTON_TOOLTIP_TEXT_OFF));
		((ToggleInventoryScreenWidget) this.toggleShowEffectScreenButton).setIsPressed(this.showEffectScreen);
		this.toggleShowEffectScreenButton.setTooltip(this.showEffectScreen ? Tooltip.of(TOGGLE_SHOW_EFFECTS_BUTTON_TOOLTIP_TEXT_ON) : Tooltip.of(TOGGLE_SHOW_EFFECTS_BUTTON_TOOLTIP_TEXT_OFF));
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		this.attributeMouseClicked = false;
		this.foodMouseClicked = false;
		this.negativeMouseClicked = false;
		this.positiveMouseClicked = false;
		this.neutralMouseClicked = false;
		int i = this.x + this.backgroundWidth + this.sidesBackgroundWidth - 15;
		int j;
		if (!RPGInventoryClient.CLIENT_CONFIG.can_hide_status_effect_screen.get() || this.showEffectScreen) {
			if (this.foodEffectsRowAmount > 1) {
				j = this.y + 34;
				if (mouseX >= (double) i && mouseX < (double) (i + 6) && mouseY >= (double) j && mouseY < (double) (j + 30)) {
					this.foodMouseClicked = true;
				}
			}
			if (this.negativeEffectsRowAmount > 1) {
				j = this.y + 84;
				if (mouseX >= (double) i && mouseX < (double) (i + 6) && mouseY >= (double) j && mouseY < (double) (j + 30)) {
					this.negativeMouseClicked = true;
				}
			}
			if (this.positiveEffectsRowAmount > 1) {
				j = this.y + 134;
				if (mouseX >= (double) i && mouseX < (double) (i + 6) && mouseY >= (double) j && mouseY < (double) (j + 30)) {
					this.positiveMouseClicked = true;
				}
			}
			if (this.neutralEffectsRowAmount > 1) {
				j = this.y + 184;
				if (mouseX >= (double) i && mouseX < (double) (i + 6) && mouseY >= (double) j && mouseY < (double) (j + 30)) {
					this.neutralMouseClicked = true;
				}
			}
		}

		if (this.attributeListSize > 15 && this.showAttributeScreen) {
			i = this.x - 13;
			j = this.y + 7;
			if (mouseX >= (double) i && mouseX < (double) (i + 6) && mouseY >= (double) j && mouseY < (double) (j + 206)) {
				this.attributeMouseClicked = true;
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (!RPGInventoryClient.CLIENT_CONFIG.can_hide_status_effect_screen.get() || this.showEffectScreen) {
			if (this.foodEffectsRowAmount > 1 && this.foodMouseClicked) {
				int i = this.foodEffectsRowAmount - 1;
				float f = (float) deltaY / (float) i;
				this.foodScrollAmount = MathHelper.clamp(this.foodScrollAmount + f, 0.0f, 1.0f);
				this.foodScrollPosition = (int) ((double) (this.foodScrollAmount * (float) i));
			}
			if (this.negativeEffectsRowAmount > 1 && this.negativeMouseClicked) {
				int i = this.negativeEffectsRowAmount - 1;
				float f = (float) deltaY / (float) i;
				this.negativeScrollAmount = MathHelper.clamp(this.negativeScrollAmount + f, 0.0f, 1.0f);
				this.negativeScrollPosition = (int) ((double) (this.negativeScrollAmount * (float) i));
			}
			if (this.positiveEffectsRowAmount > 1 && this.positiveMouseClicked) {
				int i = this.positiveEffectsRowAmount - 1;
				float f = (float) deltaY / (float) i;
				this.positiveScrollAmount = MathHelper.clamp(this.positiveScrollAmount + f, 0.0f, 1.0f);
				this.positiveScrollPosition = (int) ((double) (this.positiveScrollAmount * (float) i));
			}
			if (this.neutralEffectsRowAmount > 1 && this.neutralMouseClicked) {
				int i = this.neutralEffectsRowAmount - 1;
				float f = (float) deltaY / (float) i;
				this.neutralScrollAmount = MathHelper.clamp(this.neutralScrollAmount + f, 0.0f, 1.0f);
				this.neutralScrollPosition = (int) ((double) (this.neutralScrollAmount * (float) i));
			}
		}
		if (this.attributeListSize > 15 && this.attributeMouseClicked && this.showAttributeScreen) {
			int i = this.attributeListSize - 15;
			float f = (float) deltaY / (float) i;
			this.attributeScrollAmount = MathHelper.clamp(this.attributeScrollAmount + f, 0.0f, 1.0f);
			this.attributeScrollPosition = (int) ((double) (this.attributeScrollAmount * (float) i));
		}
		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		int scrollAreaStartX = this.x + this.backgroundWidth + 7;
		int scrollAreaWidth = 119;
		int scrollAreaStartY = this.y + 33;
		int scrollAreaHeight = 32;
		if (!RPGInventoryClient.CLIENT_CONFIG.can_hide_status_effect_screen.get() || this.showEffectScreen) {
			if (this.foodEffectsRowAmount > 1 && mouseX >= scrollAreaStartX && mouseX <= scrollAreaStartX + scrollAreaWidth && mouseY >= scrollAreaStartY && mouseY <= scrollAreaStartY + scrollAreaHeight) {
				int i = this.foodEffectsRowAmount - 1;
				float f = (float) verticalAmount / (float) i;
				this.foodScrollAmount = MathHelper.clamp(this.foodScrollAmount - f, 0.0f, 1.0f);
				this.foodScrollPosition = (int) ((double) (this.foodScrollAmount * (float) i));
			}
			scrollAreaStartY = this.y + 83;
			if (this.negativeEffectsRowAmount > 1 && mouseX >= scrollAreaStartX && mouseX <= scrollAreaStartX + scrollAreaWidth && mouseY >= scrollAreaStartY && mouseY <= scrollAreaStartY + scrollAreaHeight) {
				int i = this.negativeEffectsRowAmount - 1;
				float f = (float) verticalAmount / (float) i;
				this.negativeScrollAmount = MathHelper.clamp(this.negativeScrollAmount - f, 0.0f, 1.0f);
				this.negativeScrollPosition = (int) ((double) (this.negativeScrollAmount * (float) i));
			}
			scrollAreaStartY = this.y + 133;
			if (this.positiveEffectsRowAmount > 1 && mouseX >= scrollAreaStartX && mouseX <= scrollAreaStartX + scrollAreaWidth && mouseY >= scrollAreaStartY && mouseY <= scrollAreaStartY + scrollAreaHeight) {
				int i = this.positiveEffectsRowAmount - 1;
				float f = (float) verticalAmount / (float) i;
				this.positiveScrollAmount = MathHelper.clamp(this.positiveScrollAmount - f, 0.0f, 1.0f);
				this.positiveScrollPosition = (int) ((double) (this.positiveScrollAmount * (float) i));
			}
			scrollAreaStartY = this.y + 183;
			if (this.neutralEffectsRowAmount > 1 && mouseX >= scrollAreaStartX && mouseX <= scrollAreaStartX + scrollAreaWidth && mouseY >= scrollAreaStartY && mouseY <= scrollAreaStartY + scrollAreaHeight) {
				int i = this.neutralEffectsRowAmount - 1;
				float f = (float) verticalAmount / (float) i;
				this.neutralScrollAmount = MathHelper.clamp(this.neutralScrollAmount - f, 0.0f, 1.0f);
				this.neutralScrollPosition = (int) ((double) (this.neutralScrollAmount * (float) i));
			}
		}
		scrollAreaStartX = this.x - this.sidesBackgroundWidth + 7;
		scrollAreaWidth = 116;
		scrollAreaStartY = this.y + 7;
		scrollAreaHeight = 206;
		if (this.attributeListSize > 15 && mouseX >= scrollAreaStartX && mouseX <= scrollAreaStartX + scrollAreaWidth && mouseY >= scrollAreaStartY && mouseY <= scrollAreaStartY + scrollAreaHeight) {
			int i = this.attributeListSize - 15;
			float f = (float) verticalAmount / (float) i;
			this.attributeScrollAmount = MathHelper.clamp(this.attributeScrollAmount - f, 0.0f, 1.0f);
			this.attributeScrollPosition = (int) ((double) (this.attributeScrollAmount * (float) i));
		}
		return true;
	}

	@Override
	public TrinketPlayerScreenHandler trinkets$getHandler() {
		return ((TrinketPlayerScreenHandler) this.handler);
	}

	@Override
	public Rect2i trinkets$getGroupRect(SlotGroup group) {
		Point pos = ((TrinketPlayerScreenHandler) handler).trinkets$getGroupPos(group);
		if (pos != null) {
			return new Rect2i(pos.x() - 1, pos.y() - 1, 17, 17);
		}
		return new Rect2i(0, 0, 0, 0);
	}

	@Override
	public Slot trinkets$getFocusedSlot() {
		return this.focusedSlot;
	}

	@Override
	public int trinkets$getX() {
		return this.x;
	}

	@Override
	public int trinkets$getY() {
		return this.y;
	}

	@Override
	public boolean trinkets$isRecipeBookOpen() {
		return this.showAttributeScreen;
	}
}
