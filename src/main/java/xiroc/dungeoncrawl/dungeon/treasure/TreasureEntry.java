package xiroc.dungeoncrawl.dungeon.treasure;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.util.ItemProcessor;

public class TreasureEntry {

	public ResourceLocation item;
	public String resourceName;

	public int min, max, weight;

	public ItemEnchantment[] enchantments;

	public CompoundNBT nbt;

	private ItemProcessor<Random, Integer, Integer> itemProcessor;

	public TreasureEntry(String resourceName, int weight) {
		this(resourceName, 1, 1, weight, null, null);
	}

	public TreasureEntry(String resourceName, int min, int max, int weight) {
		this(resourceName, min, max, weight, null, null);
	}

//	public TreasureEntry(String item, int min, int max, int weight) {
//		this(item, min, max, weight, null, null);
//	}

	public TreasureEntry(String resourceName, int min, int max, int weight, CompoundNBT nbt,
			ItemEnchantment[] enchantments) {
		this.resourceName = resourceName;
		this.min = min;
		this.max = max;
		this.weight = weight;
		this.nbt = nbt;
		this.enchantments = enchantments;
	}

	public TreasureEntry(String resourceName, int min, int max, int weight, CompoundNBT nbt,
			ItemEnchantment[] enchantments, ItemProcessor<Random, Integer, Integer> itemProcessor) {
		this(resourceName, min, max, weight, nbt, enchantments);
		this.itemProcessor = itemProcessor;
	}

	public TreasureEntry withWeight(int weight) {
		return new TreasureEntry(this.resourceName, this.min, this.max, weight, this.nbt, this.enchantments,
				this.itemProcessor);
	}

	public TreasureEntry withEnchantments(ItemEnchantment[] enchantments) {
		return new TreasureEntry(this.resourceName, this.min, this.max, this.weight, this.nbt, enchantments,
				this.itemProcessor);
	}

	public TreasureEntry withProcessor(ItemProcessor<Random, Integer, Integer> itemProcessor) {
		return new TreasureEntry(this.resourceName, this.min, this.max, this.weight, this.nbt, this.enchantments,
				itemProcessor);
	}

	public TreasureEntry setNBT(CompoundNBT nbt) {
		this.nbt = nbt.copy();
		return this;
	}

	public TreasureEntry setEnchantments(ItemEnchantment[] enchantments) {
		this.enchantments = enchantments;
		return this;
	}

	public ItemStack generate(Random rand, int theme, int lootLevel) {
		if (itemProcessor != null)
			return itemProcessor.generate(rand, theme, lootLevel);
		IItemProvider itemIn = ForgeRegistries.ITEMS.getValue(item), blockIn = ForgeRegistries.BLOCKS.getValue(item);
		ItemStack stack = new ItemStack(itemIn == null ? blockIn : itemIn);
		stack.setTag(nbt);
		if (max > 1)
			stack.setCount(min + rand.nextInt(max - min + 1));
		return stack;
	}

	public void readResourceLocation() {
		String[] resource = resourceName.split(":");
		this.item = new ResourceLocation(resource[0], resource[1]);
		DungeonCrawl.LOGGER.debug("Resource: {} Hash: {}", resourceName, this.item.hashCode());
	}

	public static class ItemEnchantment {

		ResourceLocation id;
		int level;

		public ItemEnchantment(ResourceLocation id, int level) {
			this.id = id;
			this.level = level;
		}

	}

}