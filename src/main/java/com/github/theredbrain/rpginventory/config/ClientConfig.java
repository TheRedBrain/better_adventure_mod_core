package com.github.theredbrain.rpginventory.config;

import com.github.theredbrain.rpginventory.RPGInventory;
import me.fzzyhmstrs.fzzy_config.annotations.ConvertFrom;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedColor;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;

import java.util.LinkedHashMap;

@ConvertFrom(fileName = "client.json5", folder = "rpginventory")
public class ClientConfig extends Config {

	public ClientConfig() {
		super(RPGInventory.identifier("client"));
	}

	public ValidatedBoolean always_show_selected_hotbar_slot = new ValidatedBoolean(false);

	public ValidatedBoolean hud_shows_disabled_hotbar_slots = new ValidatedBoolean(true);

	public ValidatedBoolean show_empty_hand_slots = new ValidatedBoolean(true);
	public ValidatedInt hand_slots_offset_x = new ValidatedInt(-140);
	public ValidatedInt hand_slots_offset_y = new ValidatedInt(-23);
	public ValidatedBoolean offhand_item_is_right = new ValidatedBoolean(true);

	public ValidatedBoolean show_empty_alternative_hand_slots = new ValidatedBoolean(true);
	public ValidatedInt alternative_hand_slots_offset_x = new ValidatedInt(91);
	public ValidatedInt alternative_hand_slots_offset_y = new ValidatedInt(-23);
	public ValidatedBoolean alternative_offhand_item_is_right = new ValidatedBoolean(true);

	public ValidatedBoolean show_armor_bar = new ValidatedBoolean(false);

	//		@ConfigEntry.Gui.PrefixText
	public ValidatedBoolean show_attribute_screen_when_opening_inventory_screen = new ValidatedBoolean(false);
	public ValidatedBoolean can_hide_status_effect_screen = new ValidatedBoolean(false);
	public ValidatedBoolean show_effect_screen_when_opening_inventory_screen = new ValidatedBoolean(true);

	//		@ConfigEntry.Gui.PrefixText
	public ValidatedBoolean show_inactive_inventory_slots = new ValidatedBoolean(true);

	//		@ConfigEntry.Gui.PrefixText
	public ValidatedBoolean enable_open_backpack_button = new ValidatedBoolean(false);
	public ValidatedInt open_backpack_button_offset_x = new ValidatedInt(99);
	public ValidatedInt open_backpack_button_offset_y = new ValidatedInt(35);

	//		@ConfigEntry.Gui.PrefixText
	public ValidatedBoolean enable_open_hand_crafting_button = new ValidatedBoolean(false);
	public ValidatedInt open_hand_crafting_button_offset_x = new ValidatedInt(99);
	public ValidatedInt open_hand_crafting_button_offset_y = new ValidatedInt(57);

	//		@ConfigEntry.Gui.PrefixText
	public ValidatedBoolean slots_with_unusable_items_have_overlay = new ValidatedBoolean(true);
	public ValidatedColor first_overlay_colour_for_slots_with_unusable_items = new ValidatedColor(200, 5, 5, 50);
	public ValidatedColor second_overlay_colour_for_slots_with_unusable_items = new ValidatedColor(200, 5, 5, 50);
	public ValidatedBoolean show_slot_tooltips = new ValidatedBoolean(true);
	public ValidatedBoolean show_item_tooltip_two_handed_items = new ValidatedBoolean(true);
	public ValidatedBoolean show_item_tooltip_equipment_slots = new ValidatedBoolean(true);


	//		@Comment("""
//				These values describe how the matrixStack is manipulated.
//
//				They are defined like so:
//
//				initial_translation_x
//				initial_translation_y
//				initial_translation_z
//				equipped_chest_offset_x
//				equipped_chest_offset_y
//				equipped_chest_offset_z
//				rotation_positive_z
//				rotation_positive_y
//				rotation_positive_x
//
//				Manipulations:
//				matrixStack.translate(initial_translation_x, initial_translation_y, initial_translation_z);
//				if (hasStackEquippedInChestSlot) {
//					matrixStack.translate(equipped_chest_offset_x, equipped_chest_offset_y, equipped_chest_offset_z);
//				}
//				matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation_positive_z));
//				matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation_positive_y));
//				matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotation_positive_x));
//				""")
	public LinkedHashMap<String, Float[]> sheathed_hand_item_positions = new LinkedHashMap<>() {{
		put("minecraft:crossbow", new Float[]{-0.3F, 0.1F, 0.16F, 0.0F, 0.0F, 0.06F, 0.0F, 90.0F, -10.0F});
	}};
	public LinkedHashMap<String, Float[]> sheathed_offhand_item_positions = new LinkedHashMap<>() {{
		put("minecraft:shield", new Float[]{0.2F, 0.4F, 0.0F, 0.0F, 0.0F, 0.06F, 0.0F, -90.0F, 15.0F});
	}};

}
