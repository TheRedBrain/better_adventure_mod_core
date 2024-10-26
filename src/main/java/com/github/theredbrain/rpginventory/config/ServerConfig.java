package com.github.theredbrain.rpginventory.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(
		name = "server"
)
public class ServerConfig implements ConfigData {

	@Comment("""
			When true, all (off)hand slots can only hold items in the item tags 'rpginventory:hand_items' and 'rpginventory:offhand_items', respectively.
			
			It is recommended to not set this to false and instead add items to the item tags when necessary. All other items can still be accessed via the hotbar.
			""")
	public boolean are_hand_items_restricted_to_item_tags = true;

	@Comment("When false, toggling the two-handed stance is not possible when the main hand is sheathed.")
	public boolean always_allow_toggling_two_handed_stance = false;

	@Comment("""
			When the mod 'Stamina Attributes' is installed, the following 6 options take effect
			
			When true, stamina must be above 0 for swapping hand items.
			""")
	public boolean swapping_hand_items_requires_stamina = true;
	@Comment("Stamina cost for toggling two handed stance")
	public float swapping_hand_items_stamina_cost = 1.0f;

	@Comment("When true, stamina must be above 0 for sheathing hand items.")
	public boolean sheathing_hand_items_requires_stamina = true;
	@Comment("Stamina cost for toggling two handed stance")
	public float sheathing_hand_items_stamina_cost = 1.0f;

	@Comment("When true, stamina must be above 0 for toggling two handed stance.")
	public boolean toggling_two_handed_stance_requires_stamina = true;
	@Comment("Stamina cost for toggling two handed stance")
	public float toggling_two_handed_stance_stamina_cost = 1.0f;

	public String keep_inventory_status_effect_identifier = "variousstatuseffects:keep_inventory";

	public String civilisation_status_effect_identifier = "variousstatuseffects:civilisation";

	public String wilderness_status_effect_identifier = "variousstatuseffects:wilderness";

	@Comment("This status effect enables the building mode")
	public String building_mode_status_effect_identifier = "scriptblocks:building_mode";

	@Comment("This status effect is applied when an item in the 'non_two_handed_items' item tag is equipped")
	public String needs_two_handing_status_effect_identifier = "variousstatuseffects:needs_two_handing";

	@Comment("This status effect is applied when an item which is not in the 'attack_items' item tag is equipped and the 'allow_attacking_with_non_attack_items' option is set to false")
	public String no_attack_item_status_effect_identifier = "variousstatuseffects:no_attack_item";
	public boolean allow_attacking_with_non_attack_items = true;

	@Comment("Additional debug log is shown in the console.")
	public boolean show_debug_log = false;

	@Comment("""
            The default amount of spell slots.
            Must be between 0 and 8 (both inclusive)
            """)
	public int default_spell_slot_amount = 0;
	@Comment("""
			Set to false to enable the 2x2 crafting grid
			in the adventure inventory screen
			""")
	public boolean disable_inventory_crafting_slots = false;
	@Comment("default: 97")
	public int inventory_crafting_slots_x_offset = 97;
	@Comment("default: 42")
	public int inventory_crafting_slots_y_offset = 42;

	@Comment("default: 98")
	public int spell_slots_x_offset = 98;
	@Comment("default: 90")
	public int spell_slots_y_offset = 90;

	@Comment("default: 8")
	public int head_slot_x_offset = 33;
	@Comment("default: 72")
	public int head_slot_y_offset = 18;

	@Comment("default: 8")
	public int chest_slot_x_offset = 8;
	@Comment("default: 72")
	public int chest_slot_y_offset = 54;

	@Comment("default: 8")
	public int legs_slot_x_offset = 8;
	@Comment("default: 72")
	public int legs_slot_y_offset = 90;

	@Comment("default: 8")
	public int feet_slot_x_offset = 77;
	@Comment("default: 72")
	public int feet_slot_y_offset = 90;

	@Comment("default: 8")
	public int belts_group_x_offset = 8;
	@Comment("default: 72")
	public int belts_group_y_offset = 72;

	@Comment("default: 8")
	public int shoulders_group_x_offset = 8;
	@Comment("default: 36")
	public int shoulders_group_y_offset = 36;

	@Comment("default: 52")
	public int necklaces_group_x_offset = 52;
	@Comment("default: 18")
	public int necklaces_group_y_offset = 18;

	@Comment("default: 77")
	public int rings_1_group_x_offset = 77;
	@Comment("default: 36")
	public int rings_1_group_y_offset = 36;

	@Comment("default: 77")
	public int rings_2_group_x_offset = 77;
	@Comment("default: 54")
	public int rings_2_group_y_offset = 54;

	@Comment("default: 77")
	public int gloves_group_x_offset = 77;
	@Comment("default: 72")
	public int gloves_group_y_offset = 72;

	@Comment("default: 8")
	public int hand_group_x_offset = 8;
	@Comment("default: 108")
	public int hand_group_y_offset = 108;

	@Comment("default: 26")
	public int offhand_slot_x_offset = 26;
	@Comment("default: 108")
	public int offhand_slot_y_offset = 108;

	@Comment("default: 59")
	public int alternative_hand_group_x_offset = 59;
	@Comment("default: 108")
	public int alternative_hand_group_y_offset = 108;

	@Comment("default: 77")
	public int alternative_offhand_group_x_offset = 77;
	@Comment("default: 108")
	public int alternative_offhand_group_y_offset = 108;

	public ServerConfig() {

	}
}
