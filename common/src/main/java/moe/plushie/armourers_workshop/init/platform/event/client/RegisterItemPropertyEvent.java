package moe.plushie.armourers_workshop.init.platform.event.client;

import moe.plushie.armourers_workshop.api.common.IItemModelProperty;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import net.minecraft.world.item.Item;

public interface RegisterItemPropertyEvent {

    void register(IResourceLocation registryName, Item item, IItemModelProperty property);
}
