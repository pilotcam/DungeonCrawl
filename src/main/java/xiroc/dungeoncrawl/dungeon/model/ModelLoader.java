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

package xiroc.dungeoncrawl.dungeon.model;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3i;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public interface ModelLoader {

    ModelLoader VERSION_1 = (nbt, file) -> {
        List<DungeonModelBlock> modelBlocks = new ArrayList<>();

        ListNBT blocks = nbt.getList("blocks", 10);

        for (int i = 0; i < blocks.size(); i++) {
            modelBlocks.add(DungeonModelBlock.fromNBT(blocks.getCompound(i)));
        }

        DungeonModel.FeaturePosition[] featurePositions = null;

        if (nbt.contains("featurePositions", 9)) {
            ListNBT list = nbt.getList("featurePositions", 10);
            int amount = list.getCompound(0).getInt("amount");
            featurePositions = new DungeonModel.FeaturePosition[amount];
            for (int i = 1; i < list.size(); i++) {
                CompoundNBT compound = list.getCompound(i);
                if (compound.contains("facing")) {
                    featurePositions[i - 1] = new DungeonModel.FeaturePosition(compound.getInt("x"), compound.getInt("y"),
                            compound.getInt("z"),
                            Direction.valueOf(compound.getString("facing").toUpperCase(Locale.ROOT)));
                } else {
                    featurePositions[i - 1] = new DungeonModel.FeaturePosition(compound.getInt("x"), compound.getInt("y"),
                            compound.getInt("z"));
                }
            }
        }

        return new DungeonModel(modelBlocks, featurePositions, nbt.getInt("width"), nbt.getInt("height"), nbt.getInt("length"));
    };

    ModelLoader LEGACY = (nbt, file) -> {
        int width = nbt.getInt("width"), height = nbt.getInt("height"), length = nbt.getInt("length");

        ListNBT blocks = nbt.getList("model", 9);

        List<DungeonModelBlock> modelBlocks = new ArrayList<>();

        for (int x = 0; x < width; x++) {
            ListNBT blocks2 = blocks.getList(x);
            for (int y = 0; y < height; y++) {
                ListNBT blocks3 = blocks2.getList(y);
                for (int z = 0; z < length; z++) {
                    DungeonModelBlock block = DungeonModelBlock.fromNBT(blocks3.getCompound(z));
                    block.position = new Vector3i(x, y, z);
                    modelBlocks.add(block);
                }
            }
        }

        DungeonModel.FeaturePosition[] featurePositions = null;

        if (nbt.contains("featurePositions", 9)) {
            ListNBT list = nbt.getList("featurePositions", 10);
            int amount = list.getCompound(0).getInt("amount");
            featurePositions = new DungeonModel.FeaturePosition[amount];
            for (int i = 1; i < list.size(); i++) {
                CompoundNBT compound = list.getCompound(i);
                if (compound.contains("facing")) {
                    featurePositions[i - 1] = new DungeonModel.FeaturePosition(compound.getInt("x"), compound.getInt("y"),
                            compound.getInt("z"),
                            Direction.valueOf(compound.getString("facing").toUpperCase(Locale.ROOT)));
                } else {
                    featurePositions[i - 1] = new DungeonModel.FeaturePosition(compound.getInt("x"), compound.getInt("y"),
                            compound.getInt("z"));
                }
            }
        }

        return new DungeonModel(modelBlocks, featurePositions, width, height, length);
    };

    DungeonModel load(CompoundNBT nbt, ResourceLocation file);

}
