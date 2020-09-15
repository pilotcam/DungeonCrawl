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

package xiroc.dungeoncrawl.theme;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.Property;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.block.DungeonBlocks;
import xiroc.dungeoncrawl.dungeon.block.WeightedRandomBlock;
import xiroc.dungeoncrawl.dungeon.decoration.IDungeonDecoration;
import xiroc.dungeoncrawl.theme.Theme.SubTheme;
import xiroc.dungeoncrawl.util.IBlockStateProvider;

import java.util.Optional;

public class JsonThemeHandler {

    public JsonBaseTheme theme;
    public JsonSubTheme subTheme;

    private static final Logger LOGGER = LogManager.getLogger("DungeonCrawl/JsonThemeHandler");

    public static void deserialize(JsonObject object) {
        if (object.has("type")) {
            String type = object.get("type").getAsString();
            switch (type) {
                case "theme":
                    JsonBaseTheme.deserialize(object);
                    break;
                case "sub_theme":
                    JsonSubTheme.deserialize(object);
                    break;
                case "theme_randomizer": {
                    WeightedThemeRandomizer randomizer = DungeonCrawl.GSON.fromJson(object, WeightedThemeRandomizer.class);
                    Theme.THEME_RANDOMIZERS.put(randomizer.base, randomizer);
                    break;
                }
                case "sub_theme_randomizer": {
                    WeightedThemeRandomizer randomizer = DungeonCrawl.GSON.fromJson(object, WeightedThemeRandomizer.class);
                    Theme.SUB_THEME_RANDOMIZERS.put(randomizer.base, randomizer);
                    break;
                }
                case "theme_mapping":
                    object.get("mapping").getAsJsonObject().entrySet().forEach((entry) -> {
                        String key = entry.getKey();
                        if (!WorldGenRegistries.field_243657_i.containsKey(new ResourceLocation(key))) {
                            LOGGER.warn("The biome {} does not exist", key);
                        }
                        int id = entry.getValue().getAsInt();
                        Theme.BIOME_TO_THEME_MAP.put(key, id);
                    });
                    break;
                case "sub_theme_mapping":
                    object.get("mapping").getAsJsonObject().entrySet().forEach((entry) -> {
                        String key = entry.getKey();
                        if (!WorldGenRegistries.field_243657_i.containsKey(new ResourceLocation(key))) {
                            LOGGER.warn("The biome {} does not exist", key);
                        }
                        int id = entry.getValue().getAsInt();
                        Theme.BIOME_TO_SUBTHEME_MAP.put(key, id);
                    });
                    break;
                default:
                    LOGGER.error("Invalid json theme type: {}", type);
                    break;
            }
        } else {
            LOGGER.error("Invalid json theme: missing type specification.");
        }
    }

    public static class JsonBaseTheme {

        // Mandatory entries
        public IBlockStateProvider solid, normal, normal_2, floor, solidStairs, stairs, material, vanillaWall, column, slab, solidSlab;

        public static void deserialize(JsonObject object) {
            JsonObject themeObject = object.get("theme").getAsJsonObject();

            JsonBaseTheme theme = new JsonBaseTheme();

            theme.normal = JsonThemeHandler.deserialize(themeObject, "normal");
            theme.solid = JsonThemeHandler.deserialize(themeObject, "solid");

            theme.normal_2 = JsonThemeHandler.deserialize(themeObject, "normal_2");

            theme.floor = JsonThemeHandler.deserialize(themeObject, "floor");

            theme.stairs = JsonThemeHandler.deserialize(themeObject, "stairs");
            theme.solidStairs = JsonThemeHandler.deserialize(themeObject, "solid_stairs");

            theme.slab = JsonThemeHandler.deserialize(themeObject, "slab");
            theme.solidSlab = JsonThemeHandler.deserialize(themeObject, "solid_slab");

            theme.material = JsonThemeHandler.deserialize(themeObject, "material");
            theme.vanillaWall = JsonThemeHandler.deserialize(themeObject, "wall");

            int id = object.get("id").getAsInt();

            IDungeonDecoration[] decorations = null;

            if (object.has("decorations")) {
                JsonArray array = object.getAsJsonArray("decorations");
                decorations = new IDungeonDecoration[array.size()];
                for (int i = 0; i < decorations.length; i++) {
                    decorations[i] = IDungeonDecoration.fromJson(array.get(i).getAsJsonObject());
                }
            }

            Theme result = theme.toTheme().withDecorations(decorations);

            if (object.has("sub_theme")) {
                result.subTheme = object.get("sub_theme").getAsInt();
            }

            Theme.ID_TO_THEME_MAP.put(id, result);
        }

        public Theme toTheme() {
            return new Theme(null, solid, normal, floor, solidStairs, stairs, material, vanillaWall, null, normal_2, slab, solidSlab);
        }

    }

    public static class JsonSubTheme {

        // Mandatory entries
        public IBlockStateProvider wallLog, trapDoor, door, material, stairs, slab, fence, button, pressurePlate, fenceGate;

        public static void deserialize(JsonObject object) {

            JsonObject themeObject = object.get("theme").getAsJsonObject();

            JsonSubTheme theme = new JsonSubTheme();

            theme.wallLog = JsonThemeHandler.deserialize(themeObject, "pillar");
            theme.trapDoor = JsonThemeHandler.deserialize(themeObject, "trapdoor");
            theme.door = JsonThemeHandler.deserialize(themeObject, "door");
            theme.material = JsonThemeHandler.deserialize(themeObject, "material");
            theme.stairs = JsonThemeHandler.deserialize(themeObject, "stairs");
            theme.slab = JsonThemeHandler.deserialize(themeObject, "slab");
            theme.fence = JsonThemeHandler.deserialize(themeObject, "fence");
            theme.fenceGate = JsonThemeHandler.deserialize(themeObject, "fence_gate");
            theme.button = JsonThemeHandler.deserialize(themeObject, "button");
            theme.pressurePlate = JsonThemeHandler.deserialize(themeObject, "pressure_plate");

            int id = object.get("id").getAsInt();

            Theme.ID_TO_SUBTHEME_MAP.put(id, theme.toSubTheme());
        }

        public SubTheme toSubTheme() {
            return new SubTheme(wallLog, trapDoor, null, door, material, stairs, slab, fence, fenceGate, button, pressurePlate);
        }

    }

    public static IBlockStateProvider deserialize(JsonObject base, String name) {
        if (!base.has(name)) {
            LOGGER.warn("Missing BlockState Provider \"{}\"", name);
            return null;
        }
        JsonObject object = (JsonObject) base.get(name);
        if (object.has("type")) {
            String type = object.get("type").getAsString();
            if (type.equalsIgnoreCase("random_block")) {
                JsonArray blockObjects = object.get("blocks").getAsJsonArray();
                TupleIntBlock[] blocks = new TupleIntBlock[blockObjects.size()];

                int i = 0;
                for (JsonElement blockObject : blockObjects) {
                    JsonObject element = (JsonObject) blockObject;
                    Block block = ForgeRegistries.BLOCKS
                            .getValue(new ResourceLocation(element.get("block").getAsString()));
                    if (block != null) {
                        BlockState state = block.getDefaultState();
                        if (element.has("data")) {
                            JsonObject data = element.get("data").getAsJsonObject();
                            for (Property<?> property : state.getProperties()) {
                                if (data.has(property.getName())) {
                                    state = parseProperty(state, property, data.get(property.getName()).getAsString());
                                }
                            }
                        }
                        blocks[i++] = new TupleIntBlock(element.has("weight") ? element.get("weight").getAsInt() : 1, state);
                    } else {
                        LOGGER.error("Unknown block: {}", element.get("block").getAsString());
                    }
                }
                return new WeightedRandomBlock(blocks);
            } else if (type.equalsIgnoreCase("block")) {
                Block block = ForgeRegistries.BLOCKS
                        .getValue(new ResourceLocation(object.get("block").getAsString()));
                if (block != null) {
                    BlockState state = block.getDefaultState();
                    if (object.has("data")) {
                        JsonObject data = object.get("data").getAsJsonObject();
                        for (Property<?> property : state.getProperties()) {
                            if (data.has(property.getName())) {
                                state = parseProperty(state, property, data.get(property.getName()).getAsString());
                            }
                        }
                    }
                    return new BlockStateHolder(state);
                } else {
                    LOGGER.error("Unknown block: {}", object.get("block").getAsString());
                    return Blocks.CAVE_AIR::getDefaultState;
                }
            } else {
                LOGGER.error("Failed to load BlockState Provider {}: Unknown type {}.", object, type);
                return null;
            }
        } else {
            LOGGER.error("Invalid BlockState Provider \"{}\": Type not specified.", name);
            return null;
        }
    }

    /**
     * Applies the property value to the state.
     */
    private static <T extends Comparable<T>> BlockState parseProperty(BlockState state, Property<T> property, String value) {
        if (state.hasProperty(property)) {
            Optional<T> optional = property.parseValue(value);
            if (optional.isPresent()) {
                T t = optional.get();
                return state.with(property, t);
            } else {
                LOGGER.warn("Couldn't apply {} : {} for {}", property.getName(), value, state.getBlock());
            }
        }
        return state;
    }

    private static final class TupleIntBlock extends Tuple<Integer, BlockState> {

        public TupleIntBlock(Integer aIn, BlockState bIn) {
            super(aIn, bIn);
        }

    }

    private static final class BlockStateHolder implements IBlockStateProvider {

        private final BlockState state;

        public BlockStateHolder(BlockState state) {
            this.state = state;
        }

        @Override
        public BlockState get() {
            return state;
        }

    }

}
