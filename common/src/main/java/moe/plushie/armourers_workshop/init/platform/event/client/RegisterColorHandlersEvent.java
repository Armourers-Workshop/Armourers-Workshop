package moe.plushie.armourers_workshop.init.platform.event.client;

import moe.plushie.armourers_workshop.api.common.IBlockTintColorProvider;
import moe.plushie.armourers_workshop.api.common.IItemTintColorProvider;

public interface RegisterColorHandlersEvent {

    interface Block {

        void register(IBlockTintColorProvider arg, net.minecraft.world.level.block.Block... args);
    }

    interface Item {

        void register(IItemTintColorProvider arg, net.minecraft.world.item.Item... args);
    }
}
