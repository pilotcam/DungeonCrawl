package xiroc.dungeoncrawl.part.block;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.tileentity.SmokerTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.storage.loot.RandomValueRange;
import xiroc.dungeoncrawl.config.Kitchen;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure.Type;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;

public class Furnace implements IBlockPlacementHandler {

	public static final RandomValueRange COAL_AMOUNT = new RandomValueRange(1, 16);

	@Override
	public void setupBlock(IWorld world, BlockState state, BlockPos pos, Random rand, Treasure.Type treasureType,
			int theme, int lootLevel) {
		world.setBlockState(pos, state, 2);
		FurnaceTileEntity tile = (FurnaceTileEntity) world.getTileEntity(pos);
		tile.setInventorySlotContents(1, new ItemStack(Items.COAL, COAL_AMOUNT.generateInt(rand)));
	}

	public static class Smoker implements IBlockPlacementHandler {

		@Override
		public void setupBlock(IWorld world, BlockState state, BlockPos pos, Random rand, Type treasureType, int theme,
				int lootLevel) {
			world.setBlockState(pos, state, 2);
			SmokerTileEntity tile = (SmokerTileEntity) world.getTileEntity(pos);
			tile.setInventorySlotContents(1, new ItemStack(Items.CHARCOAL, COAL_AMOUNT.generateInt(rand)));
			tile.setInventorySlotContents(2, theme == 3 ? Kitchen.SMOKER_OCEAN.getItemStack(rand, theme, lootLevel)
					: Kitchen.SMOKER.getItemStack(rand, theme, lootLevel));
		}

	}

}