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

package xiroc.dungeoncrawl.dungeon.decoration;

import net.minecraft.block.BlockState;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.dungeon.block.WeightedRandomBlock;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.util.IBlockStateProvider;

public class ScatteredDecoration implements IDungeonDecoration {

    private final IBlockStateProvider blockStateProvider;
    private final float chance;

    public ScatteredDecoration(BlockState state, float chance) {
        this(() -> state, chance);
    }

    public ScatteredDecoration(IBlockStateProvider blockStateProvider, float chance) {
        this.blockStateProvider = blockStateProvider;
        this.chance = chance;
    }

    @Override
    public void decorate(DungeonModel model, IWorld world, BlockPos pos, int width, int height, int length, MutableBoundingBox worldGenBounds, MutableBoundingBox structureBounds, DungeonPiece piece, int stage) {
        boolean ew = piece.rotation == Rotation.NONE || piece.rotation == Rotation.CLOCKWISE_180;
        int maxX = ew ? width : length;
        int maxZ = ew ? length : width;
        for (int x = 1; x < maxX - 1; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 1; z < maxZ - 1; z++) {
                    BlockPos currentPos = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    if (world.isAirBlock(currentPos) && worldGenBounds.isVecInside(currentPos) && structureBounds.isVecInside(currentPos) && WeightedRandomBlock.RANDOM.nextFloat() < chance) {
                        BlockPos north = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z - 1);
                        BlockPos east = new BlockPos(north.getX() + 1, north.getY(), pos.getZ() + z);
                        BlockPos south = new BlockPos(north.getX(), north.getY(), east.getZ() + 1);
                        BlockPos west = new BlockPos(north.getX() - 1, north.getY(), east.getZ());
                        BlockPos up = new BlockPos(north.getX(), north.getY() + 1, east.getZ());

//                        if (worldGenBounds.isVecInside(north) && structureBounds.isVecInside(north) && world.isAirBlock(north) && WeightedRandomBlock.RANDOM.nextFloat() < chance) {
//                            world.setBlockState(north, blockStateProvider.get(), 2);
//                        }
//
//                        if (worldGenBounds.isVecInside(east) && structureBounds.isVecInside(east) && world.isAirBlock(east) && WeightedRandomBlock.RANDOM.nextFloat() < chance) {
//                            world.setBlockState(east, blockStateProvider.get(), 2);
//                        }
//
//                        if (worldGenBounds.isVecInside(south) && structureBounds.isVecInside(south) && world.isAirBlock(south) && WeightedRandomBlock.RANDOM.nextFloat() < chance) {
//                            world.setBlockState(south, blockStateProvider.get(), 2);
//                        }
//
//                        if (worldGenBounds.isVecInside(west) && structureBounds.isVecInside(west) && world.isAirBlock(west) && WeightedRandomBlock.RANDOM.nextFloat() < chance) {
//                            world.setBlockState(west, blockStateProvider.get(), 2);
//                        }
//
//                        if (worldGenBounds.isVecInside(down) && structureBounds.isVecInside(down) && world.isAirBlock(down) && WeightedRandomBlock.RANDOM.nextFloat() < chance) {
//                            world.setBlockState(down, blockStateProvider.get(), 2);
//                        }

                        boolean _north = worldGenBounds.isVecInside(north) && structureBounds.isVecInside(north) && world.getBlockState(north).isSolid();
                        boolean _east = worldGenBounds.isVecInside(east) && structureBounds.isVecInside(east) && world.getBlockState(east).isSolid();
                        boolean _south = worldGenBounds.isVecInside(south) && structureBounds.isVecInside(south) && world.getBlockState(south).isSolid();
                        boolean _west = worldGenBounds.isVecInside(west) && structureBounds.isVecInside(west) && world.getBlockState(west).isSolid();
                        boolean _up = worldGenBounds.isVecInside(up) && structureBounds.isVecInside(up) && world.getBlockState(up).isSolid();

                        if (_north || _east || _south || _west || _up) {
                            world.setBlockState(currentPos, blockStateProvider.get(), 2);
                        }

                    }
                }
            }
        }
    }

}