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

package xiroc.dungeoncrawl.dungeon.treasure.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.treasure.RandomItems;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;

public class SpecialItem extends LootFunction {

    public int lootLevel;

    protected SpecialItem(ILootCondition[] conditionsIn, int lootLevel) {
        super(conditionsIn);
        this.lootLevel = Math.max(0, lootLevel);
    }

    @Override
    protected ItemStack doApply(ItemStack stack, LootContext context) {
        if (context.has(LootParameters.ORIGIN)) {
            return RandomItems.generateSpecialItem(context.getWorld(), context.getRandom(), lootLevel);
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public LootFunctionType getFunctionType() {
        return Treasure.SPECIAL_ITEM;
    }

    public static class Serializer extends LootFunction.Serializer<SpecialItem> {

        public Serializer() {
            super();
        }

        @Override
        public void serialize(JsonObject object, SpecialItem functionClazz, JsonSerializationContext serializationContext) {
            object.add("loot_level", DungeonCrawl.GSON.toJsonTree(functionClazz.lootLevel));
        }

        @Override
        public SpecialItem deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
            return new SpecialItem(conditionsIn, object.get("loot_level").getAsInt());
        }
    }

}
