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

package xiroc.dungeoncrawl.dungeon.piece.room;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.model.ModelSelector;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;

import java.util.List;
import java.util.Random;

public class DungeonMegaNodePart extends DungeonPiece {

    public DungeonMegaNodePart() {
        super(StructurePieceTypes.MEGA_NODE_PART);
    }

    public DungeonMegaNodePart(TemplateManager manager, CompoundNBT nbt) {
        super(StructurePieceTypes.MEGA_NODE_PART, nbt);
    }

    @Override
    public int getDungeonPieceType() {
        return MEGA_NODE_PART;
    }

    @Override
    public void setupModel(DungeonBuilder builder, ModelSelector modelSelector, List<DungeonPiece> pieces, Random rand) {
        this.model = modelSelector.fullNodes.roll(rand);
    }

    @Override
    public void setWorldPosition(int x, int y, int z) {
        super.setWorldPosition(x - 4, y, z - 4);
    }

    @Override
    public boolean postProcess(ISeedReader worldIn, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
        if (model == null) {
            DungeonCrawl.LOGGER.warn("Missing model for  {}", this);
            return true;
        }

        Vector3i offset = model.getOffset(rotation);
        BlockPos pos = new BlockPos(x, y, z).offset(offset);

        buildRotated(model, worldIn, structureBoundingBoxIn, pos, theme, secondaryTheme, stage, rotation, worldGen, false, false);
        entrances(worldIn, structureBoundingBoxIn, model, worldGen);
        placeFeatures(worldIn, structureBoundingBoxIn, theme, secondaryTheme, randomIn, stage, worldGen);
        decorate(worldIn, pos, model.width, model.height, model.length, theme, structureBoundingBoxIn, boundingBox, model, worldGen);
        return true;
    }

}
