package com.github.theredbrain.rpginventory.config;

import com.github.theredbrain.rpginventory.RPGInventory;
import me.fzzyhmstrs.fzzy_config.annotations.ConvertFrom;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.validation.minecraft.ValidatedIdentifier;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedFloat;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@ConvertFrom(fileName = "server.json5", folder = "rpginventory")
public class ServerConfig extends Config {

	public ServerConfig() {
		super(RPGInventory.identifier("server"));
	}

	@Comment("""
			When true, all (off)hand slots can only hold items in the item tags 'rpginventory:hand_items' and 'rpginventory:offhand_items', respectively.
						
			It is recommended to not set this to false and instead add items to the item tags when necessary. All other items can still be accessed via the hotbar.
			""")
	public ValidatedBoolean are_hand_items_restricted_to_item_tags = new ValidatedBoolean(true);

	@Comment("When false, toggling the two-handed stance is not possible when the main hand is sheathed.")
	public ValidatedBoolean always_allow_toggling_two_handed_stance = new ValidatedBoolean(false);

	@Comment("""
			When the mod 'Stamina Attributes' is installed, the following 6 options take effect
						
			When true, stamina must be above 0 for swapping hand items.
			""")
	public ValidatedBoolean swapping_hand_items_requires_stamina = new ValidatedBoolean(true);
	@Comment("Stamina cost for toggling two handed stance")
	public ValidatedFloat swapping_hand_items_stamina_cost = new ValidatedFloat(1.0f);

	@Comment("When true, stamina must be above 0 for sheathing hand items.")
	public ValidatedBoolean sheathing_hand_items_requires_stamina = new ValidatedBoolean(true);
	@Comment("Stamina cost for toggling two handed stance")
	public ValidatedFloat sheathing_hand_items_stamina_cost = new ValidatedFloat(1.0f);

	@Comment("When true, stamina must be above 0 for toggling two handed stance.")
	public ValidatedBoolean toggling_two_handed_stance_requires_stamina = new ValidatedBoolean(true);
	@Comment("Stamina cost for toggling two handed stance")
	public ValidatedFloat toggling_two_handed_stance_stamina_cost = new ValidatedFloat(1.0f);

	public ValidatedIdentifier keep_inventory_status_effect_identifier = new ValidatedIdentifier("variousstatuseffects:keep_inventory");

	public ValidatedIdentifier civilisation_status_effect_identifier = new ValidatedIdentifier("variousstatuseffects:civilisation");

	public ValidatedIdentifier wilderness_status_effect_identifier = new ValidatedIdentifier("variousstatuseffects:wilderness");

	@Comment("This status effect enables the building mode")
	public ValidatedIdentifier building_mode_status_effect_identifier = new ValidatedIdentifier("scriptblocks:building_mode");

	@Comment("This status effect is applied when an item in the 'non_two_handed_items' item tag is equipped")
	public ValidatedIdentifier needs_two_handing_status_effect_identifier = new ValidatedIdentifier("variousstatuseffects:needs_two_handing");

	@Comment("This status effect is applied when an item which is not in the 'attack_items' item tag is equipped and the 'allow_attacking_with_non_attack_items' option is set to false")
	public ValidatedIdentifier no_attack_item_status_effect_identifier = new ValidatedIdentifier("variousstatuseffects:no_attack_item");
	public ValidatedBoolean allow_attacking_with_non_attack_items = new ValidatedBoolean(true);

	@Comment("Additional debug log is shown in the console.")
	public ValidatedBoolean show_debug_log = new ValidatedBoolean(false);

	@Comment("""
			The default amount of spell slots.
			Must be between 0 and 8 (both inclusive)
			""")
	public ValidatedInt default_spell_slot_amount = new ValidatedInt(0);
	@Comment("""
			Set to false to enable the 2x2 crafting grid
			in the adventure inventory screen
			""")
	public ValidatedBoolean disable_inventory_crafting_slots = new ValidatedBoolean(false);
	@Comment("default: 97")
	public ValidatedInt inventory_crafting_slots_x_offset = new ValidatedInt(97);
	@Comment("default: 42")
	public ValidatedInt inventory_crafting_slots_y_offset = new ValidatedInt(42);

	@Comment("default: 98")
	public ValidatedInt spell_slots_x_offset = new ValidatedInt(98);
	@Comment("default: 90")
	public ValidatedInt spell_slots_y_offset = new ValidatedInt(90);

	@Comment("default: 8")
	public ValidatedInt head_slot_x_offset = new ValidatedInt(33);
	@Comment("default: 72")
	public ValidatedInt head_slot_y_offset = new ValidatedInt(18);

	@Comment("default: 8")
	public ValidatedInt chest_slot_x_offset = new ValidatedInt(8);
	@Comment("default: 72")
	public ValidatedInt chest_slot_y_offset = new ValidatedInt(54);

	@Comment("default: 8")
	public ValidatedInt legs_slot_x_offset = new ValidatedInt(8);
	@Comment("default: 72")
	public ValidatedInt legs_slot_y_offset = new ValidatedInt(90);

	@Comment("default: 8")
	public ValidatedInt feet_slot_x_offset = new ValidatedInt(77);
	@Comment("default: 72")
	public ValidatedInt feet_slot_y_offset = new ValidatedInt(90);

	@Comment("default: 8")
	public ValidatedInt belts_group_x_offset = new ValidatedInt(8);
	@Comment("default: 72")
	public ValidatedInt belts_group_y_offset = new ValidatedInt(72);

	@Comment("default: 8")
	public ValidatedInt shoulders_group_x_offset = new ValidatedInt(8);
	@Comment("default: 36")
	public ValidatedInt shoulders_group_y_offset = new ValidatedInt(36);

	@Comment("default: 52")
	public ValidatedInt necklaces_group_x_offset = new ValidatedInt(52);
	@Comment("default: 18")
	public ValidatedInt necklaces_group_y_offset = new ValidatedInt(18);

	@Comment("default: 77")
	public ValidatedInt rings_1_group_x_offset = new ValidatedInt(77);
	@Comment("default: 36")
	public ValidatedInt rings_1_group_y_offset = new ValidatedInt(36);

	@Comment("default: 77")
	public ValidatedInt rings_2_group_x_offset = new ValidatedInt(77);
	@Comment("default: 54")
	public ValidatedInt rings_2_group_y_offset = new ValidatedInt(54);

	@Comment("default: 77")
	public ValidatedInt gloves_group_x_offset = new ValidatedInt(77);
	@Comment("default: 72")
	public ValidatedInt gloves_group_y_offset = new ValidatedInt(72);

	@Comment("default: 8")
	public ValidatedInt hand_group_x_offset = new ValidatedInt(8);
	@Comment("default: 108")
	public ValidatedInt hand_group_y_offset = new ValidatedInt(108);

	@Comment("default: 26")
	public ValidatedInt offhand_slot_x_offset = new ValidatedInt(26);
	@Comment("default: 108")
	public ValidatedInt offhand_slot_y_offset = new ValidatedInt(108);

	@Comment("default: 59")
	public ValidatedInt alternative_hand_group_x_offset = new ValidatedInt(59);
	@Comment("default: 108")
	public ValidatedInt alternative_hand_group_y_offset = new ValidatedInt(108);

	@Comment("default: 77")
	public ValidatedInt alternative_offhand_group_x_offset = new ValidatedInt(77);
	@Comment("default: 108")
	public ValidatedInt alternative_offhand_group_y_offset = new ValidatedInt(108);
}
