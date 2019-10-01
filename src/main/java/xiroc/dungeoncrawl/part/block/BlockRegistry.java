package xiroc.dungeoncrawl.part.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Tuple;
import xiroc.dungeoncrawl.DungeonCrawl;

public class BlockRegistry {

	public static final BlockState SPAWNER = Blocks.SPAWNER.getDefaultState();

	public static final BlockState GRASS = Blocks.GRASS.getDefaultState();
	public static final BlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
	public static final BlockState COBBLESTONE = Blocks.COBBLESTONE.getDefaultState();

	public static final BlockState STONE = Blocks.STONE.getDefaultState();

	public static final BlockState ACACIA_LOG = Blocks.ACACIA_LOG.getDefaultState();
	public static final BlockState BIRCH_LOG = Blocks.BIRCH_LOG.getDefaultState();
	public static final BlockState JUNGLE_LOG = Blocks.JUNGLE_LOG.getDefaultState();
	public static final BlockState OAK_LOG = Blocks.OAK_LOG.getDefaultState();
	public static final BlockState DARK_OAK_LOG = Blocks.DARK_OAK_LOG.getDefaultState();
	public static final BlockState SPRUCE_LOG = Blocks.SPRUCE_LOG.getDefaultState();

	public static final BlockState STONE_BRICKS = Blocks.STONE_BRICKS.getDefaultState();
	public static final BlockState MOSSY_STONE_BRICKS = Blocks.MOSSY_STONE_BRICKS.getDefaultState();
	public static final BlockState CRACKED_STONE_BRICKS = Blocks.CRACKED_STONE_BRICKS.getDefaultState();
	public static final BlockState INFESTED_STONE_BRICKS = Blocks.INFESTED_STONE_BRICKS.getDefaultState();

	public static final BlockState STAIRS_STONE_BRICKS = Blocks.STONE_BRICK_STAIRS.getDefaultState();
	public static final BlockState STAIRS_COBBLESTONE = Blocks.COBBLESTONE_STAIRS.getDefaultState();

	public static final BlockState NETHERRACK = Blocks.NETHERRACK.getDefaultState();
	public static final BlockState NETHER_BRICK = Blocks.NETHER_BRICKS.getDefaultState();
	public static final BlockState SOUL_SAND = Blocks.SOUL_SAND.getDefaultState();

	public static final BlockState STAIRS_NETHER_BRICK = Blocks.NETHER_BRICK_STAIRS.getDefaultState();
	public static final BlockState STAIRS_QUARTZ = Blocks.QUARTZ_STAIRS.getDefaultState();

	public static final BlockState IRON_BARS_WATERLOGGED = Blocks.IRON_BARS.getDefaultState()
			.with(BlockStateProperties.WATERLOGGED, true);

	public static final TupleIntBlock TIB_GRASS = new TupleIntBlock(1, GRASS);
	public static final TupleIntBlock TIB_GRAVEL = new TupleIntBlock(2, GRAVEL);
	public static final TupleIntBlock TIB_COBBLESTONE = new TupleIntBlock(2, COBBLESTONE);
	public static final TupleIntBlock TIB_MOSSY_COBBLESTONE = new TupleIntBlock(2,
			Blocks.MOSSY_COBBLESTONE.getDefaultState());
	public static final TupleIntBlock TIB_NETHERRACK = new TupleIntBlock(2, NETHERRACK);
	public static final TupleIntBlock TIB_NETHER_BRICK = new TupleIntBlock(2, NETHER_BRICK);
	public static final TupleIntBlock TIB_SOUL_SAND = new TupleIntBlock(1, SOUL_SAND);

	public static final TupleIntBlock TIB_ACACIA_LOG = new TupleIntBlock(1, ACACIA_LOG);
	public static final TupleIntBlock TIB_BIRCH_LOG = new TupleIntBlock(1, BIRCH_LOG);
	public static final TupleIntBlock TIB_JUNGLE_LOG = new TupleIntBlock(1, JUNGLE_LOG);
	public static final TupleIntBlock TIB_OAK_LOG = new TupleIntBlock(1, OAK_LOG);
	public static final TupleIntBlock TIB_DARK_OAK_LOG = new TupleIntBlock(1, DARK_OAK_LOG);
	public static final TupleIntBlock TIB_SPRUCE_LOG = new TupleIntBlock(1, SPRUCE_LOG);

	public static final TupleIntBlock TIB_STONE_BRICKS = new TupleIntBlock(4, STONE_BRICKS);
	public static final TupleIntBlock TIB_MOSSY_STONE_BRICKS = new TupleIntBlock(2, MOSSY_STONE_BRICKS);
	public static final TupleIntBlock TIB_CRACKED_STONE_BRICKS = new TupleIntBlock(2, CRACKED_STONE_BRICKS);
	public static final TupleIntBlock TIB_INFESTED_STONE_BRICKS = new TupleIntBlock(1, INFESTED_STONE_BRICKS);

	public static final TupleIntBlock TIB_STAIRS_STONE_BRICKS = new TupleIntBlock(2, STAIRS_STONE_BRICKS);
	public static final TupleIntBlock TIB_STAIRS_COBBLESTONE = new TupleIntBlock(1, STAIRS_COBBLESTONE);
	public static final TupleIntBlock TIB_STAIRS_NETHER_BRICK = new TupleIntBlock(3, STAIRS_NETHER_BRICK);
	public static final TupleIntBlock TIB_STAIRS_QUARTZ = new TupleIntBlock(1, STAIRS_QUARTZ);

	public static WeightedRandomBlock STONE_BRICKS_NORMAL_MOSSY_CRACKED;
	public static WeightedRandomBlock STONE_BRICKS_NORMAL_MOSSY_CRACKED_COBBLESTONE;
	public static WeightedRandomBlock STONE_BRICKS_GRAVEL_COBBLESTONE;
	public static WeightedRandomBlock NETHERRACK_NETHERBRICK;
	public static WeightedRandomBlock NETHERRACK_NETHERBRICK_SOULSAND;
	public static WeightedRandomBlock NETHER_BRICK_STAIRS;
	public static WeightedRandomBlock STAIRS_STONE_COBBLESTONE;
	public static WeightedRandomBlock STAIRS_NETHERBRICK_QUARTZ;
	public static WeightedRandomBlock SANDSTONE_DEFAULT_CHSELED_SMOOTH;
	public static WeightedRandomBlock SANDSTONE_DEFAULT_SMOOTH_SAND;
	public static WeightedRandomBlock STAIRS_SANDSTONE_DEFAULT_SMOOTH;
	public static WeightedRandomBlock RED_SANDSTONE_DEFAULT_CHSELED_SMOOTH;
	public static WeightedRandomBlock RED_SANDSTONE_DEFAULT_SMOOTH_RED_SAND;
	public static WeightedRandomBlock STAIRS_RED_SANDSTONE_DEFAULT_SMOOTH;
	public static WeightedRandomBlock ICE_DEFAULT_PACKED;
	public static WeightedRandomBlock DARK_PRISMARINE_PRISMARINE;
	public static WeightedRandomBlock CLAY_FLOOR;

	/*
	 * Calculate the WeightedRandomBlocks
	 */
	public static void load() {
		long time = System.currentTimeMillis();
		DungeonCrawl.LOGGER.info("Calculating WeightedRandomBlocks");
		STONE_BRICKS_NORMAL_MOSSY_CRACKED = new WeightedRandomBlock(
				new TupleIntBlock[] { TIB_STONE_BRICKS, TIB_MOSSY_STONE_BRICKS, TIB_CRACKED_STONE_BRICKS });
		STONE_BRICKS_NORMAL_MOSSY_CRACKED_COBBLESTONE = new WeightedRandomBlock(new TupleIntBlock[] { TIB_STONE_BRICKS,
				TIB_MOSSY_STONE_BRICKS, TIB_CRACKED_STONE_BRICKS, TIB_COBBLESTONE, TIB_MOSSY_COBBLESTONE });
		STONE_BRICKS_GRAVEL_COBBLESTONE = new WeightedRandomBlock(new TupleIntBlock[] { TIB_STONE_BRICKS, TIB_GRAVEL,
				TIB_COBBLESTONE, new TupleIntBlock(1, Blocks.MOSSY_COBBLESTONE.getDefaultState()),
				new TupleIntBlock(1, MOSSY_STONE_BRICKS) });
		NETHERRACK_NETHERBRICK = new WeightedRandomBlock(new TupleIntBlock[] { TIB_NETHERRACK, TIB_NETHER_BRICK,
				new TupleIntBlock(2, Blocks.RED_NETHER_BRICKS.getDefaultState()) });
		NETHERRACK_NETHERBRICK_SOULSAND = new WeightedRandomBlock(new TupleIntBlock[] { TIB_NETHERRACK,
				TIB_NETHER_BRICK, TIB_SOUL_SAND, new TupleIntBlock(2, Blocks.RED_NETHER_BRICKS.getDefaultState()) });
		NETHER_BRICK_STAIRS = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(1, Blocks.NETHER_BRICK_STAIRS.getDefaultState()),
						new TupleIntBlock(1, Blocks.RED_NETHER_BRICK_STAIRS.getDefaultState()) });
		STAIRS_STONE_COBBLESTONE = new WeightedRandomBlock(
				new TupleIntBlock[] { TIB_STAIRS_STONE_BRICKS, TIB_STAIRS_COBBLESTONE });
		STAIRS_NETHERBRICK_QUARTZ = new WeightedRandomBlock(
				new TupleIntBlock[] { TIB_STAIRS_NETHER_BRICK, TIB_STAIRS_QUARTZ });
		SANDSTONE_DEFAULT_CHSELED_SMOOTH = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(2, Blocks.SANDSTONE.getDefaultState()),
						new TupleIntBlock(1, Blocks.CHISELED_SANDSTONE.getDefaultState()),
						new TupleIntBlock(1, Blocks.SMOOTH_SANDSTONE.getDefaultState()) });
		SANDSTONE_DEFAULT_SMOOTH_SAND = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(2, Blocks.SAND.getDefaultState()),
						new TupleIntBlock(1, Blocks.SANDSTONE.getDefaultState()),
						new TupleIntBlock(1, Blocks.SMOOTH_SANDSTONE.getDefaultState()) });
		STAIRS_SANDSTONE_DEFAULT_SMOOTH = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(1, Blocks.SANDSTONE_STAIRS.getDefaultState()),
						new TupleIntBlock(1, Blocks.SMOOTH_SANDSTONE_STAIRS.getDefaultState()) });
		RED_SANDSTONE_DEFAULT_CHSELED_SMOOTH = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(2, Blocks.RED_SANDSTONE.getDefaultState()),
						new TupleIntBlock(1, Blocks.CHISELED_RED_SANDSTONE.getDefaultState()),
						new TupleIntBlock(1, Blocks.SMOOTH_RED_SANDSTONE.getDefaultState()) });
		RED_SANDSTONE_DEFAULT_SMOOTH_RED_SAND = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(2, Blocks.RED_SAND.getDefaultState()),
						new TupleIntBlock(1, Blocks.RED_SANDSTONE.getDefaultState()),
						new TupleIntBlock(1, Blocks.SMOOTH_RED_SANDSTONE.getDefaultState()) });
		STAIRS_RED_SANDSTONE_DEFAULT_SMOOTH = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(1, Blocks.RED_SANDSTONE_STAIRS.getDefaultState()),
						new TupleIntBlock(1, Blocks.SMOOTH_RED_SANDSTONE_STAIRS.getDefaultState()) });
		ICE_DEFAULT_PACKED = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(5, Blocks.ICE.getDefaultState()),
						new TupleIntBlock(3, Blocks.PACKED_ICE.getDefaultState()) });
		DARK_PRISMARINE_PRISMARINE = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(1, Blocks.DARK_PRISMARINE.getDefaultState()),
						new TupleIntBlock(1, Blocks.PRISMARINE.getDefaultState()) });
		CLAY_FLOOR = new WeightedRandomBlock(new TupleIntBlock[] { new TupleIntBlock(1, Blocks.CLAY.getDefaultState()),
				new TupleIntBlock(1, Blocks.SMOOTH_STONE.getDefaultState()),
				new TupleIntBlock(1, Blocks.STONE.getDefaultState()) });
		DungeonCrawl.LOGGER.info("Finished calculations (" + (System.currentTimeMillis() - time) + " ms)");
	}

	public static final class TupleIntBlock extends Tuple<Integer, BlockState> {

		public TupleIntBlock(Integer aIn, BlockState bIn) {
			super(aIn, bIn);
		}

	}

	public static final class TupleFloatBlock extends Tuple<Float, BlockState> {

		public TupleFloatBlock(Float aIn, BlockState bIn) {
			super(aIn, bIn);
		}

	}

}