package xiroc.dungeoncrawl.dungeon.treasure;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.config.SpecialItemTags;
import xiroc.dungeoncrawl.dungeon.monster.RandomEquipment;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class RandomSpecialItem {

    public static final int COLOR = 3847130;

    public static final ItemStack[] ITEMS;

    public static final ItemStack[] RARE_ITEMS;

    public static final ItemStack REINFORCED_BOW, BOOTS_OF_BATTLE, PANTS_OF_DEFLECTION, LUMBERJACKET, YOKEL_AXE, DOOM,
            THE_SLAYER, DEMON_HUNTER_CROSSBOW, THIEF_DAGGER, THE_GREAT_CLEAVER, ARCHANGEL_SWORD, REPULSER, ELB_BOW;

    public static final TreasureItem CAP, PANTALOONS, LEATHER_JACKET, LEATHER_BOOTS, IRON_SWORD;
//	CHAINMAIL_BOOTS, CHAINMAIL_LEGGINGS, CHAINMAIL_CHESTPLATE, CHAINMAIL_HELMET, STONE_SWORD;

    public static WeightedRandomTreasureItem STAGE_1, STAGE_2, STAGE_3, STAGE_4, STAGE_5;
    //public static final TreasureItem[] RARE_SPECIAL_ITEMS;

    static {
        REINFORCED_BOW = new ItemStack(Items.BOW);
        REINFORCED_BOW.addEnchantment(Enchantments.UNBREAKING, 1);
        REINFORCED_BOW.addEnchantment(Enchantments.POWER, 1);
        REINFORCED_BOW.setDisplayName(new StringTextComponent("Reinforced Bow"));

        BOOTS_OF_BATTLE = new ItemStack(Items.LEATHER_BOOTS);
        RandomEquipment.setArmorColor(BOOTS_OF_BATTLE, COLOR);
        BOOTS_OF_BATTLE.addEnchantment(Enchantments.UNBREAKING, 1);
        BOOTS_OF_BATTLE.addEnchantment(Enchantments.PROTECTION, 1);
        BOOTS_OF_BATTLE.setDisplayName(new StringTextComponent("Boots of Battle"));

        PANTS_OF_DEFLECTION = new ItemStack(Items.LEATHER_LEGGINGS);
        RandomEquipment.setArmorColor(PANTS_OF_DEFLECTION, COLOR);
        PANTS_OF_DEFLECTION.addEnchantment(Enchantments.PROTECTION, 2);
        PANTS_OF_DEFLECTION.addEnchantment(Enchantments.THORNS, 1);
        PANTS_OF_DEFLECTION.setDisplayName(new StringTextComponent("Pants of Deflection"));

        LUMBERJACKET = new ItemStack(Items.LEATHER_CHESTPLATE);
        RandomEquipment.setArmorColor(LUMBERJACKET, 11546150);
        LUMBERJACKET.addEnchantment(Enchantments.UNBREAKING, 3);
        LUMBERJACKET.addEnchantment(Enchantments.FIRE_PROTECTION, 1);
        LUMBERJACKET.setDisplayName(new StringTextComponent("Lumberjacket"));

        YOKEL_AXE = new ItemStack(Items.IRON_AXE);
        YOKEL_AXE.addEnchantment(Enchantments.EFFICIENCY, 2);
        YOKEL_AXE.addEnchantment(Enchantments.SHARPNESS, 1);
        YOKEL_AXE.addEnchantment(Enchantments.UNBREAKING, 1);
        YOKEL_AXE.setDisplayName(new StringTextComponent("Yokel's Axe"));

        DOOM = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:golden_sword")));
        DOOM.addEnchantment(Enchantments.SHARPNESS, 1);
        DOOM.addEnchantment(Enchantments.FIRE_ASPECT, 2);
        DOOM.addEnchantment(Enchantments.UNBREAKING, 1);
        DOOM.setDisplayName(new StringTextComponent("Doom"));

        THE_SLAYER = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:diamond_sword")));
        THE_SLAYER.addEnchantment(Enchantments.SHARPNESS, 4);
        THE_SLAYER.setDisplayName(new StringTextComponent("The Slayer"));

        DEMON_HUNTER_CROSSBOW = new ItemStack(Items.CROSSBOW);
        DEMON_HUNTER_CROSSBOW.addEnchantment(Enchantments.PIERCING, 2);
        DEMON_HUNTER_CROSSBOW.addEnchantment(Enchantments.MULTISHOT, 1);
        DEMON_HUNTER_CROSSBOW.addEnchantment(Enchantments.QUICK_CHARGE, 1);
        DEMON_HUNTER_CROSSBOW.addEnchantment(Enchantments.POWER, 4);
        DEMON_HUNTER_CROSSBOW.setDisplayName(new StringTextComponent("Demon Hunter's Crossbow"));

        THIEF_DAGGER = new ItemStack(Items.IRON_SWORD);
        THIEF_DAGGER.addEnchantment(Enchantments.SHARPNESS, 1);
        THIEF_DAGGER.addEnchantment(Enchantments.LOOTING, 3);
        THIEF_DAGGER.setDisplayName(new StringTextComponent("Thief's Dagger"));

        THE_GREAT_CLEAVER = new ItemStack(Items.DIAMOND_SWORD);
        THE_GREAT_CLEAVER.addEnchantment(Enchantments.SWEEPING, 3);
        THE_GREAT_CLEAVER.addEnchantment(Enchantments.SMITE, 4);
        THE_GREAT_CLEAVER.addEnchantment(Enchantments.UNBREAKING, 3);
        THE_GREAT_CLEAVER.setDisplayName(new StringTextComponent("The Great Cleaver"));

        ARCHANGEL_SWORD = new ItemStack(Items.GOLDEN_SWORD);
        ARCHANGEL_SWORD.addEnchantment(Enchantments.SHARPNESS, 4);
        ARCHANGEL_SWORD.addEnchantment(Enchantments.UNBREAKING, 2);
        ARCHANGEL_SWORD.addEnchantment(Enchantments.VANISHING_CURSE, 1);
        ARCHANGEL_SWORD.setDisplayName(new StringTextComponent("Archangel's Sword"));

        REPULSER = new ItemStack(Items.IRON_SWORD);
        REPULSER.addEnchantment(Enchantments.KNOCKBACK, 2);
        REPULSER.addEnchantment(Enchantments.SWEEPING, 1);
        REPULSER.setDisplayName(new StringTextComponent("Repulser"));

        ELB_BOW = new ItemStack(Items.BOW);
        ELB_BOW.addEnchantment(Enchantments.POWER, 4);
        ELB_BOW.addEnchantment(Enchantments.PIERCING, 3);
        ELB_BOW.addEnchantment(Enchantments.MENDING, 1);
        ELB_BOW.setDisplayName(new StringTextComponent("Bow of the Elbs"));

        ITEMS = new ItemStack[]{REINFORCED_BOW, BOOTS_OF_BATTLE, LUMBERJACKET, YOKEL_AXE, DOOM, ARCHANGEL_SWORD,
                REPULSER};

        RARE_ITEMS = new ItemStack[]{THE_SLAYER, DEMON_HUNTER_CROSSBOW, THIEF_DAGGER, THE_GREAT_CLEAVER};

        CAP = new TreasureItem("minecraft:air").withProcessor((world, rand, theme,
                                                               lootLevel) -> RandomEquipment.setArmorColor(SpecialItemTags.rollForTagsAndApply(
                ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:leather_helmet")), lootLevel,
                rand, "Cap"), RandomEquipment.getRandomColor(rand)));
        LEATHER_JACKET = new TreasureItem(
                "minecraft:air")
                .withProcessor(
                        (world, rand, theme, lootLevel) -> RandomEquipment
                                .setArmorColor(
                                        SpecialItemTags.rollForTagsAndApply(
                                                ForgeRegistries.ITEMS.getValue(
                                                        new ResourceLocation("minecraft:leather_chestplate")),
                                                lootLevel, rand, "Jacket"),
                                        RandomEquipment.getRandomColor(rand)));

        PANTALOONS = new TreasureItem(
                "minecraft:air")
                .withProcessor(
                        (world, rand, theme, lootLevel) -> RandomEquipment
                                .setArmorColor(
                                        SpecialItemTags.rollForTagsAndApply(
                                                ForgeRegistries.ITEMS.getValue(
                                                        new ResourceLocation("minecraft:leather_leggings")),
                                                lootLevel, rand, "Pantaloons"),
                                        RandomEquipment.getRandomColor(rand)));

        LEATHER_BOOTS = new TreasureItem(
                "minecraft:air")
                .withProcessor(
                        (world, rand, theme, lootLevel) -> RandomEquipment
                                .setArmorColor(
                                        SpecialItemTags.rollForTagsAndApply(
                                                ForgeRegistries.ITEMS.getValue(
                                                        new ResourceLocation("minecraft:leather_boots")),
                                                lootLevel, rand, "Boots"),
                                        RandomEquipment.getRandomColor(rand)));

        IRON_SWORD = new TreasureItem("minecraft:air").withProcessor((world, rand, theme, lootLevel) -> SpecialItemTags
                .rollForTagsAndApply(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:iron_sword")),
                        lootLevel, rand, "Blade"));

//        SPECIAL_ITEMS = new TreasureItem[]{CAP, PANTALOONS, LEATHER_JACKET, LEATHER_BOOTS,
//                createDefaultSpecialItem("minecraft:chainmail_boots"),
//                createDefaultSpecialItem("minecraft:chainmail_leggings"),
//                createDefaultSpecialItem("minecraft:chainmail_chestplate"),
//                createDefaultSpecialItem("minecraft:chainmail_helmet"), createDefaultSpecialItem("minecraft:stone_sword"),
//                createDefaultSpecialItem("minecraft:golden_sword"), createDefaultSpecialItem("minecraft:golden_axe"),
//                createDefaultSpecialItem("minecraft:golden_boots"), createDefaultSpecialItem("minecraft:golden_leggings"),
//                createDefaultSpecialItem("minecraft:golden_chestplate"), createDefaultSpecialItem("minecraft:golden_helmet"),
//                createDefaultSpecialItem("minecraft:iron_axe"), createDefaultSpecialItem("minecraft:iron_boots"),
//                createDefaultSpecialItem("minecraft:iron_leggings"), createDefaultSpecialItem("minecraft:iron_chestplate"),
//                createDefaultSpecialItem("minecraft:iron_helmet"), IRON_SWORD};

//        RARE_SPECIAL_ITEMS = new TreasureItem[]{createDefaultSpecialItem("minecraft:diamond_sword"),
//                createDefaultSpecialItem("minecraft:diamond_boots"), createDefaultSpecialItem("minecraft:diamond_leggings"),
//                createDefaultSpecialItem("minecraft:diamond_chestplate"),
//                createDefaultSpecialItem("minecraft:diamond_helmet"), createDefaultSpecialItem("minecraft:diamond_axe"),
//                createDefaultSpecialItem("minecraft:diamond_shovel"), createDefaultSpecialItem("minecraft:diamond_pickaxe")};
    }

    public static void loadJson(IResourceManager resourceManager) {
        try {
            JsonParser parser = new JsonParser();
            ResourceLocation stage1 = DungeonCrawl.locate("treasure/stage_1.json");
            if (resourceManager.hasResource(stage1)) {
                DungeonCrawl.LOGGER.debug("Loading {}", stage1.toString());
                JsonArray array = parser.parse(new JsonReader(new InputStreamReader(resourceManager.getResource(stage1).getInputStream()))).getAsJsonArray();
                STAGE_1 = WeightedRandomTreasureItem.loadFromJSON(array);
            } else {
                throw new RuntimeException("Missing file " + stage1.toString());
            }

            ResourceLocation stage2 = DungeonCrawl.locate("treasure/stage_2.json");
            if (resourceManager.hasResource(stage2)) {
                DungeonCrawl.LOGGER.debug("Loading {}", stage2.toString());
                JsonArray array = parser.parse(new JsonReader(new InputStreamReader(resourceManager.getResource(stage2).getInputStream()))).getAsJsonArray();
                STAGE_2 = WeightedRandomTreasureItem.loadFromJSON(array);
            } else {
                throw new RuntimeException("Missing file " + stage2.toString());
            }

            ResourceLocation stage3 = DungeonCrawl.locate("treasure/stage_3.json");
            if (resourceManager.hasResource(stage3)) {
                DungeonCrawl.LOGGER.debug("Loading {}", stage3.toString());
                JsonArray array = parser.parse(new JsonReader(new InputStreamReader(resourceManager.getResource(stage3).getInputStream()))).getAsJsonArray();
                STAGE_3 = WeightedRandomTreasureItem.loadFromJSON(array);
            } else {
                throw new RuntimeException("Missing file " + stage3.toString());
            }

            ResourceLocation stage4 = DungeonCrawl.locate("treasure/stage_4.json");
            if (resourceManager.hasResource(stage4)) {
                DungeonCrawl.LOGGER.debug("Loading {}", stage4.toString());
                JsonArray array = parser.parse(new JsonReader(new InputStreamReader(resourceManager.getResource(stage4).getInputStream()))).getAsJsonArray();
                STAGE_4 = WeightedRandomTreasureItem.loadFromJSON(array);
            } else {
                throw new RuntimeException("Missing file " + stage4.toString());
            }

            ResourceLocation stage5 = DungeonCrawl.locate("treasure/stage_5.json");
            if (resourceManager.hasResource(stage5)) {
                DungeonCrawl.LOGGER.debug("Loading {}", stage5.toString());
                JsonArray array = parser.parse(new JsonReader(new InputStreamReader(resourceManager.getResource(stage5).getInputStream()))).getAsJsonArray();
                STAGE_5 = WeightedRandomTreasureItem.loadFromJSON(array);
            } else {
                throw new RuntimeException("Missing file " + stage5.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ItemStack generate(ServerWorld world, Random rand, int theme, Integer lootLevel) {
//        if (lootLevel > 4 || rand.nextDouble() < 0.025 * lootLevel)
//            return rand.nextBoolean() ? RARE_SPECIAL_ITEMS[rand.nextInt(RARE_SPECIAL_ITEMS.length)].generate(world,
//                    rand, theme, lootLevel) : RARE_ITEMS[rand.nextInt(RARE_ITEMS.length)].copy();
//        return rand.nextDouble() < 0.8
//                ? SPECIAL_ITEMS[rand.nextInt(SPECIAL_ITEMS.length)].generate(world, rand, theme, lootLevel)
//                : ITEMS[rand.nextInt(ITEMS.length)].copy();
        DungeonCrawl.LOGGER.debug("LootLevel: {}", lootLevel);
        switch (lootLevel) {
            case 0:
                return STAGE_1.roll(rand).generate(world, rand, theme, lootLevel);
            case 1:
                return STAGE_2.roll(rand).generate(world, rand, theme, lootLevel);
            case 3:
                return STAGE_3.roll(rand).generate(world, rand, theme, lootLevel);
            case 4:
                return STAGE_4.roll(rand).generate(world, rand, theme, lootLevel);
            default:
                return STAGE_5.roll(rand).generate(world, rand, theme, lootLevel);
        }
    }

    public static TreasureItem createSpecialItem(String item) {
        return new TreasureItem("minecraft:air").withProcessor((world, rand, theme, lootLevel) -> SpecialItemTags
                .rollForTagsAndApply(ForgeRegistries.ITEMS.getValue(new ResourceLocation(item)), lootLevel, rand));
    }

    public static TreasureItem createEnchantedSpecialItem(String itemName) {
        return new TreasureItem("minecraft:air").withProcessor((world, rand, theme, lootLevel) -> {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));
            if (item != null) {
                return EnchantmentHelper.addRandomEnchantment(rand, new ItemStack(item), 10 + 5 * lootLevel, lootLevel > 2);
            } else {
                DungeonCrawl.LOGGER.error("The item {} does not exist.", itemName);
                return ItemStack.EMPTY;
            }
        });
    }

}
