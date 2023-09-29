package com.github.theredbrain.bamcore.item;
//
//import net.minecraft.item.ArmorItem;
//import net.minecraft.item.ArmorMaterial;
//import net.minecraft.item.Items;
//import net.minecraft.recipe.Ingredient;
//import net.minecraft.sound.SoundEvent;
//import net.minecraft.sound.SoundEvents;
//import net.minecraft.util.Lazy;
//import net.minecraft.util.StringIdentifiable;
//import net.minecraft.util.Util;
//
//import java.util.EnumMap;
//import java.util.function.Supplier;
//
//public enum CustomArmorMaterials implements StringIdentifiable, ArmorMaterial {
//    LEATHER("leather", 5, Util.make(new EnumMap<ArmorItem.Type, Integer>(ArmorItem.Type.class), map -> {
//        map.put(ArmorItem.Type.BOOTS, 1);
//        map.put(ArmorItem.Type.LEGGINGS, 2);
//        map.put(ArmorItem.Type.CHESTPLATE, 3);
//        map.put(ArmorItem.Type.HELMET, 1);
//        map.put(ExtendedArmorItemType.GLOVES, 1);
//        map.put(ExtendedArmorItemType.SHOULDERS, 1);
//    }), 15, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0f, 0.0f, () -> Ingredient.ofItems(Items.LEATHER)),
//    CHAINMAIL("chainmail", 15, Util.make(new EnumMap<ArmorItem.Type, Integer>(ArmorItem.Type.class), map -> {
//        map.put(ArmorItem.Type.BOOTS, 1);
//        map.put(ArmorItem.Type.LEGGINGS, 4);
//        map.put(ArmorItem.Type.CHESTPLATE, 5);
//        map.put(ArmorItem.Type.HELMET, 2);
//        map.put(ExtendedArmorItemType.GLOVES, 3);
//        map.put(ExtendedArmorItemType.SHOULDERS, 3);
//    }), 12, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 0.0f, 0.0f, () -> Ingredient.ofItems(Items.IRON_INGOT)),
//    IRON("iron", 15, Util.make(new EnumMap<ArmorItem.Type, Integer>(ArmorItem.Type.class), map -> {
//        map.put(ArmorItem.Type.BOOTS, 2);
//        map.put(ArmorItem.Type.LEGGINGS, 5);
//        map.put(ArmorItem.Type.CHESTPLATE, 6);
//        map.put(ArmorItem.Type.HELMET, 2);
//        map.put(ExtendedArmorItemType.GLOVES, 2);
//        map.put(ExtendedArmorItemType.SHOULDERS, 1);
//    }), 9, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0f, 0.0f, () -> Ingredient.ofItems(Items.IRON_INGOT)),
//    GOLD("gold", 7, Util.make(new EnumMap<ArmorItem.Type, Integer>(ArmorItem.Type.class), map -> {
//        map.put(ArmorItem.Type.BOOTS, 1);
//        map.put(ArmorItem.Type.LEGGINGS, 3);
//        map.put(ArmorItem.Type.CHESTPLATE, 5);
//        map.put(ArmorItem.Type.HELMET, 2);
//        map.put(ExtendedArmorItemType.GLOVES, 2);
//        map.put(ExtendedArmorItemType.SHOULDERS, 2);
//    }), 25, SoundEvents.ITEM_ARMOR_EQUIP_GOLD, 0.0f, 0.0f, () -> Ingredient.ofItems(Items.GOLD_INGOT)),
//    DIAMOND("diamond", 33, Util.make(new EnumMap<ArmorItem.Type, Integer>(ArmorItem.Type.class), map -> {
//        map.put(ArmorItem.Type.BOOTS, 3);
//        map.put(ArmorItem.Type.LEGGINGS, 6);
//        map.put(ArmorItem.Type.CHESTPLATE, 8);
//        map.put(ArmorItem.Type.HELMET, 3);
//        map.put(ExtendedArmorItemType.GLOVES, 3);
//        map.put(ExtendedArmorItemType.SHOULDERS, 3);
//    }), 10, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 2.0f, 0.0f, () -> Ingredient.ofItems(Items.DIAMOND)),
//    TURTLE("turtle", 25, Util.make(new EnumMap<ArmorItem.Type, Integer>(ArmorItem.Type.class), map -> {
//        map.put(ArmorItem.Type.BOOTS, 2);
//        map.put(ArmorItem.Type.LEGGINGS, 5);
//        map.put(ArmorItem.Type.CHESTPLATE, 6);
//        map.put(ArmorItem.Type.HELMET, 2);
//        map.put(ExtendedArmorItemType.GLOVES, 2);
//        map.put(ExtendedArmorItemType.SHOULDERS, 2);
//    }), 9, SoundEvents.ITEM_ARMOR_EQUIP_TURTLE, 0.0f, 0.0f, () -> Ingredient.ofItems(Items.SCUTE)),
//    NETHERITE("netherite", 37, Util.make(new EnumMap<ArmorItem.Type, Integer>(ArmorItem.Type.class), map -> {
//        map.put(ArmorItem.Type.BOOTS, 3);
//        map.put(ArmorItem.Type.LEGGINGS, 6);
//        map.put(ArmorItem.Type.CHESTPLATE, 8);
//        map.put(ArmorItem.Type.HELMET, 3);
//        map.put(ExtendedArmorItemType.GLOVES, 3);
//        map.put(ExtendedArmorItemType.SHOULDERS, 3);
//    }), 15, SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, 3.0f, 0.1f, () -> Ingredient.ofItems(Items.NETHERITE_INGOT));
//
//    public static final StringIdentifiable.Codec<CustomArmorMaterials> CODEC;
//    private static final EnumMap<ArmorItem.Type, Integer> BASE_DURABILITY;
////    private static final int[] BASE_DURABILITY;
//    private final String name;
//    private final int durabilityMultiplier;
////    private final int[] protectionAmounts;
//    private final EnumMap<ArmorItem.Type, Integer> protectionAmounts;
//    private final int enchantability;
//    private final SoundEvent equipSound;
//    private final float toughness;
//    private final float knockbackResistance;
//    private final Lazy<Ingredient> repairIngredientSupplier;
//
//    private CustomArmorMaterials(String name, int durabilityMultiplier, EnumMap<ArmorItem.Type, Integer> protectionAmounts, int enchantability, SoundEvent equipSound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredientSupplier) {
//        this.name = name;
//        this.durabilityMultiplier = durabilityMultiplier;
//        this.protectionAmounts = protectionAmounts;
//        this.enchantability = enchantability;
//        this.equipSound = equipSound;
//        this.toughness = toughness;
//        this.knockbackResistance = knockbackResistance;
//        this.repairIngredientSupplier = new Lazy<Ingredient>(repairIngredientSupplier);
//    }
//
////    @Override
////    public int getDurability(ArmorItem.Type type) {
////        return BASE_DURABILITY[slot.getEntitySlotId()] * this.durabilityMultiplier;
////    }
////
////    @Override
////    public int getProtectionAmount(EquipmentSlot slot) {
////        return this.protectionAmounts[slot.getEntitySlotId()];
////    }
//
//    @Override
//    public int getDurability(ArmorItem.Type type) {
//        return BASE_DURABILITY.get((Object)type) * this.durabilityMultiplier;
//    }
//
//    @Override
//    public int getProtection(ArmorItem.Type type) {
//        return this.protectionAmounts.get((Object)type);
//    }
//
//    @Override
//    public int getEnchantability() {
//        return this.enchantability;
//    }
//
//    @Override
//    public SoundEvent getEquipSound() {
//        return this.equipSound;
//    }
//
//    @Override
//    public Ingredient getRepairIngredient() {
//        return this.repairIngredientSupplier.get();
//    }
//
//    @Override
//    public String getName() {
//        return this.name;
//    }
//
//    @Override
//    public float getToughness() {
//        return this.toughness;
//    }
//
//    @Override
//    public float getKnockbackResistance() {
//        return this.knockbackResistance;
//    }
//
//    @Override
//    public String asString() {
//        return this.name;
//    }
//
//    static {
//        CODEC = StringIdentifiable.createCodec(CustomArmorMaterials::values);
//        BASE_DURABILITY = Util.make(new EnumMap(ArmorItem.Type.class), map -> { // TODO balancing
//            map.put(ArmorItem.Type.BOOTS, 13);
//            map.put(ArmorItem.Type.LEGGINGS, 15);
//            map.put(ArmorItem.Type.CHESTPLATE, 16);
//            map.put(ArmorItem.Type.HELMET, 11);
//            map.put(ExtendedArmorItemType.GLOVES, 11);
//            map.put(ExtendedArmorItemType.SHOULDERS, 11);
//        });
//    }
//}
