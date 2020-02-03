package moe.plushie.armourers_workshop.common.init.blocks;

import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

public interface ICustomItemBlock {

    public void registerItemBlock(IForgeRegistry<Item> registry);

}
