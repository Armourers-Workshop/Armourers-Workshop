package moe.plushie.armourers_workshop.api.common;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;

public interface ILootFunction extends LootContextUser {

    ItemStack apply(ItemStack itemStack, LootContext lootContext);
}
