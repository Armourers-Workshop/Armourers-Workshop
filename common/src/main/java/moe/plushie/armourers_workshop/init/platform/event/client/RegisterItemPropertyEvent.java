package moe.plushie.armourers_workshop.init.platform.event.client;

import moe.plushie.armourers_workshop.api.common.IItemModelProperty;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public interface RegisterItemPropertyEvent {

    void register(ResourceLocation registryName, Item item, IItemModelProperty property);
}
