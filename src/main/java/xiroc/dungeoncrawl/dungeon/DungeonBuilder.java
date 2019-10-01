package xiroc.dungeoncrawl.dungeon;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.DungeonPieces.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.DungeonPieces.EntranceBuilder;
import xiroc.dungeoncrawl.dungeon.DungeonPieces.Hole;
import xiroc.dungeoncrawl.dungeon.DungeonPieces.Stairs;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentModel;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentModelRegistry;
import xiroc.dungeoncrawl.dungeon.segment.RandomDungeonSegmentModel;
import xiroc.dungeoncrawl.util.Position2D;

public class DungeonBuilder {

	public Random rand;
	public Position2D start;

	public DungeonLayer[] layers;

	public BlockPos startPos;

	public DungeonBuilder(IWorld world, ChunkPos pos, Random rand) {
		this.rand = rand;
		this.start = new Position2D(rand.nextInt(16), rand.nextInt(16));
		this.startPos = new BlockPos(pos.x * 16, world.getChunkProvider().getChunkGenerator().getGroundHeight() - 16,
				pos.z * 16);
		this.layers = new DungeonLayer[startPos.getY() / 16];
		DungeonCrawl.LOGGER.info("DungeonBuilder starts at (" + startPos.getX() + " / " + startPos.getY() + " / "
				+ startPos.getZ() + "), " + +this.layers.length + " layers");
		for (int i = 0; i < layers.length; i++) {
			this.layers[i] = new DungeonLayer(DungeonLayerType.NORMAL);
			this.layers[i].buildMap(rand, (i == 0) ? this.start : layers[i - 1].end, false);
		}
	}

	public DungeonBuilder(ChunkGenerator<?> world, ChunkPos pos, Random rand) {
		this.rand = rand;
		this.start = new Position2D(rand.nextInt(16), rand.nextInt(16));
		this.startPos = new BlockPos(pos.x * 16, world.getGroundHeight() - 16, pos.z * 16);

		this.layers = new DungeonLayer[startPos.getY() / 16];
		DungeonCrawl.LOGGER.info("DungeonBuilder starts at (" + startPos.getX() + " / " + startPos.getY() + " / "
				+ startPos.getZ() + "), " + +this.layers.length + " layers");
		for (int i = 0; i < layers.length; i++) {
			this.layers[i] = new DungeonLayer(DungeonLayerType.NORMAL);
			this.layers[i].buildMap(rand, (i == 0) ? this.start : layers[i - 1].end, i == layers.length - 1);

		}
	}

	public List<DungeonPiece> build() {
		List<DungeonPiece> list = Lists.newArrayList();
		for (int i = 0; i < layers.length; i++) {
			buildLayer(layers[i], i, startPos);
			// list.addAll(buildLayer(layers[i], i, startPos));
			DungeonPiece stairs = i == 0 ? new EntranceBuilder(null, DungeonPieces.DEFAULT_NBT)
					: new Stairs(null, DungeonPieces.DEFAULT_NBT);
			stairs.setRealPosition(startPos.getX() + layers[i].start.x * 8, startPos.getY() + 8 - i * 16,
					startPos.getZ() + layers[i].start.z * 8);
			stairs.stage = 0;
			list.add(stairs);
		}
		postProcessDungeon(list, rand);
		return list;
	}

	public void buildLayer(DungeonLayer layer, int lyr, BlockPos startPos) {
		int stage = lyr > 2 ? 2 : lyr;
		for (int x = 0; x < layer.width; x++) {
			for (int z = 0; z < layer.length; z++) {
				if (layer.segments[x][z] != null) {
					layer.segments[x][z].setRealPosition(startPos.getX() + x * 8, startPos.getY() - lyr * 16,
							startPos.getZ() + z * 8);
					layer.segments[x][z].stage = stage;
				}
			}
		}
	}

	public void postProcessDungeon(List<DungeonPiece> list, Random rand) {
		int lyrs = layers.length;
		for (int i = 0; i < layers.length; i++) {
			int stage = i > 2 ? 2 : i;
			DungeonLayer layer = layers[i];
			for (int x = 0; x < layer.width; x++) {
				for (int z = 0; z < layer.length; z++) {
					if (layer.segments[x][z] != null) {
						if (layer.segments[x][z].getType() == 0) {
							if ((i < lyrs - 1 ? layers[i + 1].segments[x][z] == null : true)
									&& rand.nextDouble() < 0.02) {
								Hole hole = new Hole(null, DungeonPieces.DEFAULT_NBT);
								hole.sides = layer.segments[x][z].sides;
								hole.connectedSides = layer.segments[x][z].connectedSides;
								hole.setRealPosition(startPos.getX() + x * 8, startPos.getY() - i * 16,
										startPos.getZ() + z * 8);
								hole.stage = stage;
								hole.lava = stage == 2;
								layer.segments[x][z] = hole;
							} else {
								DungeonFeatures.processCorridor(layer, x, z, rand, i, stage, startPos);
							}
						}
					}
				}
			}

			for (int x = 0; x < layer.width; x++)
				for (int z = 0; z < layer.length; z++) {
					if (layer.segments[x][z] != null) {
						if (i == lyrs - 1)
							layer.segments[x][z].theme = 1;
						list.add(layer.segments[x][z]);
					}
				}
		}
	}

	public static DungeonSegmentModel getModel(DungeonPiece piece, Random rand) {
		boolean north = piece.sides[0];
		boolean east = piece.sides[1];
		boolean south = piece.sides[2];
		boolean west = piece.sides[3];
		switch (piece.getType()) {
		case 0:
			switch (piece.connectedSides) {
			case 2:
				switch (((DungeonPieces.Corridor) piece).specialType) {
				case 1:
					return DungeonSegmentModelRegistry.CORRIDOR_FIRE;
				case 2:
					return DungeonSegmentModelRegistry.CORRIDOR_GRASS;
				default:
					if (north && south || east && west)
						return RandomDungeonSegmentModel.CORRIDOR_STRAIGHT.roll(rand);
					// return DungeonSegmentModelRegistry.CORRIDOR_EW;
					// return DungeonSegmentModelRegistry.CORRIDOR_EW_TURN;
					return RandomDungeonSegmentModel.CORRIDOR_TURN.roll(rand);
				}

			case 3:
				// return DungeonSegmentModelRegistry.CORRIDOR_EW_OPEN;
				return RandomDungeonSegmentModel.CORRIDOR_OPEN.roll(rand);
			case 4:
				// return
				// DungeonSegmentModelRegistry.CORRIDOR_EW_ALL_OPEN;
				return RandomDungeonSegmentModel.CORRIDOR_ALL_OPEN.roll(rand);
			default:
				return null;
			}
		case 1:
			return DungeonSegmentModelRegistry.STAIRS_BOTTOM;
		case 2:
			return DungeonSegmentModelRegistry.STAIRS;
		case 3:
			return DungeonSegmentModelRegistry.STAIRS_TOP;
		case 5:
			return DungeonSegmentModelRegistry.ROOM;
		default:
			return null;
		}
	}

}