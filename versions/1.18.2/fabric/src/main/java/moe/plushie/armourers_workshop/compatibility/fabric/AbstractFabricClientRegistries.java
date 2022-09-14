package moe.plushie.armourers_workshop.compatibility.fabric;

import moe.plushie.armourers_workshop.api.common.IItemModelProperty;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public interface AbstractFabricClientRegistries {

    static void registerItemProperty(Item item, ResourceLocation key, IItemModelProperty property) {
        ItemProperties.register(item, key, property::getValue);
        // FabricModelPredicateProviderRegistry.register(item, key, (itemStack, level, entity) -> property.getValue(itemStack, level, entity, 0)
    }
}
