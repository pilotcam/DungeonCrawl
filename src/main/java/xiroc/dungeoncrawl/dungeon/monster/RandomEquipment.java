/*
        Dungeon Crawl, a procedural dungeon generator for Minecraft 1.14 and later.
        Copyright (C) 2020

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package xiroc.dungeoncrawl.dungeon.monster;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.util.WeightedRandom;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Random;

public class RandomEquipment {

    public static final int HIGHEST_STAGE = 4;

    public static final int[] ARMOR_COLORS = new int[]{11546150, 16701501, 3949738, 6192150, 16351261, 16383998,
            15961002, 1908001, 8439583, 4673362, 1481884, 8991416, 3847130};

    public static Hashtable<Integer, WeightedRandom<Item>> HELMET, CHESTPLATE, LEGGINGS, BOOTS, MELEE_WEAPON, RANGED_WEAPON;

    /**
     * Initializes all WeightedRandomItems from the datapack.
     */
    public static void loadJson(IResourceManager resourceManager) {
        HELMET = new Hashtable<>(5);
        CHESTPLATE = new Hashtable<>(5);
        LEGGINGS = new Hashtable<>(5);
        BOOTS = new Hashtable<>(5);
        MELEE_WEAPON = new Hashtable<>(5);
        RANGED_WEAPON = new Hashtable<>(5);

        JsonParser parser = new JsonParser();

        try {
            loadArmorFromJson(resourceManager, DungeonCrawl.locate("monster/equipment/armor/stage_1.json"), parser, 0);
            loadArmorFromJson(resourceManager, DungeonCrawl.locate("monster/equipment/armor/stage_2.json"), parser, 1);
            loadArmorFromJson(resourceManager, DungeonCrawl.locate("monster/equipment/armor/stage_3.json"), parser, 2);
            loadArmorFromJson(resourceManager, DungeonCrawl.locate("monster/equipment/armor/stage_4.json"), parser, 3);
            loadArmorFromJson(resourceManager, DungeonCrawl.locate("monster/equipment/armor/stage_5.json"), parser, 4);

            loadWeaponsFromJson(resourceManager, DungeonCrawl.locate("monster/equipment/weapon/stage_1.json"), parser, 0);
            loadWeaponsFromJson(resourceManager, DungeonCrawl.locate("monster/equipment/weapon/stage_2.json"), parser, 1);
            loadWeaponsFromJson(resourceManager, DungeonCrawl.locate("monster/equipment/weapon/stage_3.json"), parser, 2);
            loadWeaponsFromJson(resourceManager, DungeonCrawl.locate("monster/equipment/weapon/stage_4.json"), parser, 3);
            loadWeaponsFromJson(resourceManager, DungeonCrawl.locate("monster/equipment/weapon/stage_5.json"), parser, 4);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Convenience method to load an armor file from json.
     */
    private static void loadArmorFromJson(IResourceManager resourceManager, ResourceLocation file, JsonParser parser, int stage) throws IOException {
        if (resourceManager.hasResource(file)) {
            try {
                DungeonCrawl.LOGGER.debug("Loading {}", file.toString());
                JsonObject object = parser.parse(new JsonReader(new InputStreamReader(resourceManager.getResource(file).getInputStream()))).getAsJsonObject();
                if (object.has("helmet")) {
                    HELMET.put(stage, WeightedRandom.ITEM.fromJson(object.getAsJsonArray("helmet")));
                } else {
                    DungeonCrawl.LOGGER.warn("Missing entry 'helmet' in {}", file.toString());
                }

                if (object.has("chestplate")) {
                    CHESTPLATE.put(stage, WeightedRandom.ITEM.fromJson(object.getAsJsonArray("chestplate")));
                } else {
                    DungeonCrawl.LOGGER.warn("Missing entry 'chestplate' in {}", file.toString());
                }

                if (object.has("leggings")) {
                    LEGGINGS.put(stage, WeightedRandom.ITEM.fromJson(object.getAsJsonArray("leggings")));
                } else {
                    DungeonCrawl.LOGGER.warn("Missing entry 'leggings' in {}", file.toString());
                }

                if (object.has("boots")) {
                    BOOTS.put(stage, WeightedRandom.ITEM.fromJson(object.getAsJsonArray("boots")));
                } else {
                    DungeonCrawl.LOGGER.warn("Missing entry 'boots' in {}", file.toString());
                }

            } catch (Exception e) {
                DungeonCrawl.LOGGER.error("Failed to load {}", file.toString());
                e.printStackTrace();
            }
        } else {
            throw new FileNotFoundException("Missing file: " + file.toString());
        }
    }

    /**
     * Convenience method to load a weapon file from json.
     */
    private static void loadWeaponsFromJson(IResourceManager resourceManager, ResourceLocation file, JsonParser parser, int stage) throws IOException {
        if (resourceManager.hasResource(file)) {
            DungeonCrawl.LOGGER.debug("Loading {}", file.toString());
            JsonObject object = parser.parse(new JsonReader(new InputStreamReader(resourceManager.getResource(file).getInputStream()))).getAsJsonObject();

            if (object.has("melee")) {
                MELEE_WEAPON.put(stage, WeightedRandom.ITEM.fromJson(object.getAsJsonArray("melee")));
            } else {
                DungeonCrawl.LOGGER.error("Missing entry 'melee' in {}", file.toString());
            }

            if (object.has("ranged")) {
                RANGED_WEAPON.put(stage, WeightedRandom.ITEM.fromJson(object.getAsJsonArray("ranged")));
            } else {
                DungeonCrawl.LOGGER.error("Missing entry 'ranged' in {}", file.toString());
            }
        } else {
            throw new FileNotFoundException("Missing file: " + file.toString());
        }
    }

    public static ItemStack[] createArmor(Random rand, int stage) {
        if (stage > HIGHEST_STAGE)
            stage = HIGHEST_STAGE;

        ItemStack[] items = new ItemStack[4];
        float chance = 0.4F + 0.15F * stage;

        if (HELMET.containsKey(stage)) {
            if (rand.nextFloat() < chance) {
                Item item = HELMET.get(stage).roll(rand);
                items[3] = createItemStack(rand, item, stage);
                if (item instanceof DyeableArmorItem) {
                    setArmorColor(items[3], getRandomColor(rand));
                }
            } else {
                items[3] = ItemStack.EMPTY;
            }
        } else {
            // This can only happen if a monster equipment file in the datapack is incomplete.
            items[3] = ItemStack.EMPTY;
        }

        if (CHESTPLATE.containsKey(stage)) {
            if (rand.nextFloat() < chance) {
                Item item = CHESTPLATE.get(stage).roll(rand);
                items[2] = createItemStack(rand, item, stage);
                if (item instanceof DyeableArmorItem) {
                    setArmorColor(items[2], getRandomColor(rand));
                }
            } else {
                items[2] = ItemStack.EMPTY;
            }
        } else {
            // This can only happen if a monster equipment file in the datapack is incomplete.
            items[2] = ItemStack.EMPTY;
        }

        if (LEGGINGS.containsKey(stage)) {
            if (rand.nextFloat() < chance) {
                Item item = LEGGINGS.get(stage).roll(rand);
                items[1] = createItemStack(rand, item, stage);
                if (item instanceof DyeableArmorItem) {
                    setArmorColor(items[1], getRandomColor(rand));
                }
            } else {
                items[1] = ItemStack.EMPTY;
            }
        } else {
            // This can only happen if a monster equipment file in the datapack is incomplete.
            items[1] = ItemStack.EMPTY;
        }

        if (BOOTS.containsKey(stage)) {
            if (rand.nextFloat() < chance) {
                Item item = BOOTS.get(stage).roll(rand);
                items[0] = createItemStack(rand, item, stage);
                if (item instanceof DyeableArmorItem) {
                    setArmorColor(items[0], getRandomColor(rand));
                }
            } else {
                items[0] = ItemStack.EMPTY;
            }
        } else {
            // This can only happen if a monster equipment file in the datapack is incomplete.
            items[0] = ItemStack.EMPTY;
        }

        //DungeonCrawl.LOGGER.info("ARMOR ({}) : {}", stage, Arrays.toString(items));

        return items;
    }

    public static ItemStack createItemStack(Random rand, Item item, int stage) {
        ItemStack itemStack = EnchantmentHelper.enchantItem(rand, new ItemStack(item), 10 + 3 * stage, false);
        applyDamage(itemStack, rand);
        return itemStack;
    }

    public static void applyDamage(ItemStack item, Random rand) {
        if (item.isDamageableItem())
            item.setDamageValue(rand.nextInt(Math.max(1, item.getMaxDamage() / 2)));
    }

    public static void enchantItem(ItemStack item, Enchantment enchantment, double multiplier) {
        int level = (int) ((double) enchantment.getMaxLevel() * multiplier);
        if (level < 1)
            level = 1;
        item.enchant(enchantment, level);
    }

    public static ItemStack setArmorColor(ItemStack item, int color) {
        CompoundNBT tag = item.getTag();
        if (tag == null)
            tag = new CompoundNBT();
        INBT displayNBT = tag.get("display");
        CompoundNBT display;
        if (displayNBT == null)
            display = new CompoundNBT();
        else
            display = (CompoundNBT) displayNBT;
        display.putInt("color", color);
        tag.put("display", display);
        item.setTag(tag);
        return item;
    }

    public static ItemStack getMeleeWeapon(Random rand, int stage) {
        if (stage > HIGHEST_STAGE)
            stage = HIGHEST_STAGE;
        if (MELEE_WEAPON.containsKey(stage)) {
            return createItemStack(rand, MELEE_WEAPON.get(stage).roll(rand), stage);
        } else {
            // This can only happen if a monster equipment file in the datapack is incomplete.
            return ItemStack.EMPTY;
        }
    }

    public static ItemStack getRangedWeapon(Random rand, int stage) {
        if (stage > HIGHEST_STAGE)
            stage = HIGHEST_STAGE;
        if (RANGED_WEAPON.containsKey(stage)) {
            return createItemStack(rand, RANGED_WEAPON.get(stage).roll(rand), stage);
        } else {
            // This can only happen if a monster equipment file in the datapack is incomplete.
            return ItemStack.EMPTY;
        }
    }

    public static double getStageMultiplier(int stage) {
        if (stage > HIGHEST_STAGE)
            return 1.0D;
        return 1D / (HIGHEST_STAGE - stage + 1);
    }

    public static int getRandomColor(Random rand) {
        return ARMOR_COLORS[rand.nextInt(ARMOR_COLORS.length)];
    }

    public static Item getItem(ResourceLocation resourceLocation) {
        if (ForgeRegistries.ITEMS.containsKey(resourceLocation))
            return ForgeRegistries.ITEMS.getValue(resourceLocation);
        DungeonCrawl.LOGGER.warn("Failed to get {} from the item registry.", resourceLocation.toString());
        return null;
    }

}
