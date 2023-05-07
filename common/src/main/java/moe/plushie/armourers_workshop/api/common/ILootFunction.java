package moe.plushie.armourers_workshop.api.common;

import moe.plushie.armourers_workshop.api.data.IDataPackObject;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;

public interface ILootFunction extends LootContextUser {

    ItemStack apply(ItemStack itemStack, LootContext lootContext);

    interface Serializer<T extends ILootFunction> {

        void serialize(IDataPackObject object, T function);

        T deserialize(IDataPackObject object);
    }
}
