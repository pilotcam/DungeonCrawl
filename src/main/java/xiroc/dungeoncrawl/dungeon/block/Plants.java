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

package xiroc.dungeoncrawl.dungeon.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;

import java.util.Random;

public class Plants {

    public static class Farmland implements IBlockPlacementHandler {

        @Override
        public void place(IWorld world, BlockState state, BlockPos pos, Random rand, Theme theme, Theme.SecondaryTheme secondaryTheme,
                          int lootLevel, boolean worldGen) {
            state = state.setValue(BlockStateProperties.MOISTURE, 7);
            world.setBlock(pos, state, 2);
            BlockPos cropPos = new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ());
            if (rand.nextFloat() < 0.6) {
                BlockState crop = BlockTags.CROPS.getRandomElement(rand).defaultBlockState();
                if (crop.hasProperty(BlockStateProperties.AGE_7))
                    crop = crop.setValue(BlockStateProperties.AGE_7, 4 + rand.nextInt(4));
                world.setBlock(cropPos, crop, 2);
            } else {
                world.setBlock(cropPos, Blocks.CAVE_AIR.defaultBlockState(), 2);
            }
        }

    }

    public static class FlowerPot implements IBlockPlacementHandler {

        @Override
        public void place(IWorld world, BlockState state, BlockPos pos, Random rand, Theme theme, Theme.SecondaryTheme secondaryTheme,
                          int lootLevel, boolean worldGen) {
            world.setBlock(pos, BlockTags.FLOWER_POTS.getRandomElement(rand).defaultBlockState(), 2);
        }

    }

    public static class Podzol implements IBlockPlacementHandler {

        @Override
        public void place(IWorld world, BlockState state, BlockPos pos, Random rand, Theme theme, Theme.SecondaryTheme secondaryTheme,
                          int lootLevel, boolean worldGen) {
            world.setBlock(pos, state, 2);
            BlockState flower = BlockTags.TALL_FLOWERS.getRandomElement(rand).defaultBlockState();
            BlockPos lowerPart = pos.above();
            BlockPos upperPart = lowerPart.above();
            world.setBlock(lowerPart, DungeonBlocks.applyProperty(flower, BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER), 2);
            world.setBlock(upperPart, DungeonBlocks.applyProperty(flower, BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER), 2);
        }

    }

}
