# RPG Inventory

Adds a new inventory screen with more equipment slots and other equipment related mechanics.

## Installation

Requires
- [Fabric API](https://modrinth.com/mod/fabric-api)
- [Cloth Config](https://modrinth.com/mod/cloth-config)
- [Trinkets](https://modrinth.com/mod/trinkets)
- [Slot Customization API](https://modrinth.com/mod/slot-customization-api)

Highly recommended
- [Mod Menu](https://modrinth.com/mod/modmenu)
- [Food Overhaul](https://modrinth.com/mod/food-overhaul)
- [Stamina Attributes](https://modrinth.com/mod/stamina-attributes)
- [Various Status Effects](https://modrinth.com/mod/various-status-effects)

## New equipment slots

The new slots accessible in the inventory are:
- 1 belt slot
- 1 necklace slot
- 2 ring slots
- 1 gloves slot
- 1 shoulders slot
- 8 spell slots
  - The value "default_spell_slot_amount" in the server config file controls the amount of spell slots each player has active by default.\
    The new entity attribute "generic.active_spell_slot_amount" controls how many spell slots are added to/removed from the default active amount. This is 0 by default, but with entity attribute modifiers (EAMs) it can be changed.
These spell slots are intended to be used with spell books powered by Spell Engine, but they should work with other items as well.
- 1 hand slot
- 1 alternative hand slot
- 1 alternative offhand slot

The mod also adds additional slots which are not directly accessible. They are used in mechanics which are explained later.
- 1 sheathed hand slot
- 1 sheathed offhand slot
- 1 empty hand slot
- 1 empty offhand slot

## New Keybindings and mechanics

### Swap hand

Swaps the item in the hand slot with the item in the alternative hand slot

### Swap offhand

Swaps the item in the offhand slot with the item in the alternative offhand slot

### Sheathe Weapons

Puts the items in the hand and the offhand slot into their corresponding sheathed hand slots. When pressed again, swaps the items back

### Toggle Two-handing Stance

Puts the items in the offhand slot into the sheathed offhand slot. When pressed again, swaps the item back. This is not possible, when the hand item is in the "non_two_handed_items" item tag.
When the hand item is sheathed, the hand slot contains the item in the selected hotbar slot, like in vanilla.
When the offhand item is sheathed, the offhand slot contains an empty item stack. On its own this is not very useful, it's designed to be used in combination with other mods like [Better Combat Extension](https://modrinth.com/mod/bettercombat-extension).

When a hand item is not sheathed, but the corresponding slot contains no item, the players hand is not empty. The item in the corresponding empty hand slot is held instead.
The empty hand slots always contain a item called "Empty Hand Weapon". This is technically a weapon. When Better Combat is installed, this allows for unarmed combat.

### Stamina Attributes Compatibility

Installing [Stamina Attributes](https://modrinth.com/mod/stamina-attributes) allows for swapping. sheathing and toggling the 2-handed stance to have configurable stamina costs/requirements.

### Note: The vanilla 'Swap Item With Offhand' hotkey no longer has a function.

Using it when items where sheathed could duplicate items. Swapping items into the hotbar using the number keys still works.

## Status Effect screen

Active and visible status effects are listed on the right side of the inventory screen. They are sorted by their category (harmful, beneficial and neutral). Effects can have a description (added by assigning a value to <effect_translation_key>.description in the lang files), which is also displayed.

### Food Overhaul Compatibility

[Food Overhauls](https://modrinth.com/mod/food-overhaul) food effects are displayed in a separate list.

## Unusable Items

Items in the "unusable_when_low_durability" item tag have the same behaviour as elytra. Instead of getting destroyed when losing all durability, they become unusable until they are repaired. Unusable items have a different translation key (default one + "_broken").
Inventory slots that contain unusable items have an overlay of a configurable colour. This can be disabled in the client config.

## Additional Item Tooltips

These are separated into categories. Each category can be disabled in the client config.
- the slots an item can be equipped in, this is controlled by item tags.
- if an item is in the "two_handed_items" item tag.

## Slot Tooltips

Equipment and trinket slots now have a tooltip. It is only shown when the slot and the cursor stack are empty. This feature can be disabled in the client config.

All trinket slots have a tooltip. It can be set in the lang file with this schema:

```json
{
	"slot.tooltip.<group_name>.<slot_name>": "Test Slot"
}
```

When the string is empty, no tooltip will be shown.

## Additional settings and features

The 2x2 crafting grid in the player inventory can be disabled.

Items in the "two_handed_items" item tag can only be used when the offhand is sheathed.

When "needs_two_handing_status_effect_identifier" is a valid status effect identifier, that status effect is applied when the item in the hand is in the "two_handed_items" item tag and the offhand is not sheathed.

When "no_attack_item_status_effect_identifier" is a valid status effect identifier, that status effect is applied when the item in the hand is not in the 'attack_items' item tag and the 'allow_attacking_with_non_attack_items' option is set to false.

When "building_mode_status_effect_identifier" is a valid status effect identifier and the player has that status effect, several mechanics are ignored.
- every item can be used to attack and to break blocks
- both hands behave like they are sheathed, so the hand slot contains the item in the selected hotbar slot, like in vanilla.

The gamerule "canChangeEquipment" controls, whether items can be put into or removed from equipment slots.

When "civilisation_status_effect_identifier" is a valid status effect identifier and the player has that status effect items can be put into or removed from equipment slots, regardless of the gamerule "canChangeEquipment".

When "wilderness_status_effect_identifier" is a valid status effect identifier and the player has that status effect items can not be put into or removed from equipment slots, regardless of the gamerule "canChangeEquipment".

When the gamerule "destroyDroppedItemsOnDeath" is true and the vanilla gamerule "keepInventory" is false, the items in the players inventory are not dropped when they die. They are destroyed instead.

When "keep_inventory_status_effect_identifier" is a valid status effect identifier, that status effect is applied when an item in the "keeps_inventory_on_death" item tag is equipped (in an equipment, trinket or the offhand slot).
When the player dies while having that status effect, all equipped items in the "keeps_inventory_on_death" item tag are destroyed. The rest of the inventory is kept, regardless of game rules and stuff like "Curse of Vanishing".

### Various Status Effects Compatibility

All status effect identifier options default to status effects implemented by [Various Status Effects](https://modrinth.com/mod/various-status-effects).

### Player Attribute Screen Compatibility

When the "Player Attribute Screen" mod is installed, a button to toggle the attribute screen is active in the RPG Inventory screen.

### Additional Trinket slots

Trinket slots added by other mods or data packs are displayed and function like normal.

