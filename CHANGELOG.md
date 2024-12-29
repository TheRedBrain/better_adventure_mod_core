# 2.3.0

- added compatibility with "RPG Crafting"

# 2.2.0

- removed feature: Attribute Screen, this feature was exported into a standalone mod called "Player Attribute Screen". The "attribute_screen_configuration" config value should work in the new mods config file.
- added compatibility with "Player Attribute Screen"
- added server config option to set default spell slot amount
- added vanilla axes and items from the "More RPG Classes" mods by @Fichte to several item tags

# 2.1.0

- update to 1.21.1
- client config is now much more organized and better documented
- added client option to hide empty hand slots in the HUD
- added client option to hide the status effect screen
- added compatibility with "Backpack Attribute"
- added compatibility with "Inventory Size Attributes"
- fixed several issues with the status effect screen
- prepared several features that are coming in the future

# 2.0.1

- fixed an issue where the client would crash when shift-clicking in the RPG inventory, on some items, while hand items where sheathed

# 2.0.0

Update to 1.21

# 1.9.0

- improved quick moving (shift-clicking) items in the RPG inventory screen
- refactored "main_hand" slots to "hand" slots. This should improve compatibility with other mods.
- fixed an issue where trinket slots from other mods where not registered correctly

# 1.8.0

- the "unusable items" feature is now properly implemented
- added various tooltips to items/inventory slots. This is customizable via the client config
- fixed an issue where connecting to a dedicated server would fail (for real this time!)

# 1.7.1

- fixed an issue where connecting to a dedicated server would fail

# 1.7.0

- added unique rings functionality (any item in the rpginventory:unique_rings item tag can only be equipped in one ring slot at a time)
- position/orientation of sheathed items is now configurable
- fixed an issue where swapping/sheathing hands would sometimes duplicate/delete items

# 1.6.0

- sheathing or swapping a hand item now plays a sound
- added HUD indicator for hand slots that are not sheathed
- The indicator for the selected hotbar slot is now only visible when the main hand is sheathed. This can be disabled in the client config.
- added server config option to allow hand slots to hold every item
- changed toggling two-handed stance to no longer fail when both hands are sheathed. The old functionality can be re-enabled in the server config.

# 1.5.3

- fixed an issue where items in the offhand were deleted when sheathing or swapping the offhand item

# 1.5.2

- fixed crash on start up

# 1.5.1

- fixed an issue with the weapon_attribute file for the default_empty_hand_weapon

# 1.5.0

RPG Inventory now supports all features of the Trinkets mod, no more restrictions!

- switched positions of the effects screen and the attributes screen
- refactored slots and slot groups to 'offhand' instead of 'off_hand'
- refactored some server config values to be more descriptive

# 1.4.0

- interactions with the offhand slot are now restricted like for all other slots
- positions of the equipment slots are now controlled by the server config
- added support for additional trinket slots

# 1.3.0

- AzureLib is no longer a required dependency
- API for rendering equipped trinkets on the player model was removed. This feature will be implemented in an addon for RPG Inventory instead.

# 1.2.2

- Stamina Attributes is no longer a required dependency
- disabled the functionality of vanillas 'Swap Item With Offhand' hotkey. Using it could lead to item duplication.

# 1.2.1

- custom belt/gloves/necklace/rings slots by default now also accept all items configured for the corresponding default Trinket slots
- spell slots now accept spell books from all mods
- added missing translation keys for keybindings
- changed default keybindings to no longer conflict with vanilla keybindings

# 1.2.0

- added a toggleable attribute screen which shows a configurable list of attributes
- added compatibility with Food Overhaul, food effects are displayed separately to other effects
- status effect description are now only displayed, when the translation key has a value assigned to it
- added client config option to hide the armor bar
- changed active spell slots amount to 1 by default

# 1.1.0

- added default config values for status effects provided by 'Various Status Effects'
- added mod compatibilities via item tags for 'Better Combat' and 'Better Combat Extension'
- added new gamerule 'destroyDroppedItemsOnDeath', false by default and has no effect when gamerule 'keepInventory' is true
- added config option for a new status effect identifier, when valid and applied to a player, acts like the 'keepInventory' gamerule

# 1.0.0

First release!

#