package moe.plushie.armourers_workshop.init.event.client;

import moe.plushie.armourers_workshop.api.common.IItemModelProperty;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import net.minecraft.world.item.Item;

public interface RegisterItemPropertyEvent {

    void register(OpenResourceKey registryName, Item item, IItemModelProperty property);
}
