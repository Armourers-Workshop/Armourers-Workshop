package moe.plushie.armourers_workshop.compat.client.item;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.client.render.model.SkinItemProperty;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.entity.LivingEntity;

@Available("[21, 26)")
public class AbstractItemProperties {

    public static SkinItemProperty getProperty(OpenResourceKey key) {
        var key1 = key.get();
        return (itemStack, entity, level, flags, displayContext) -> {
            var property = ItemProperties.getProperty(itemStack, key1);
            if (property != null) {
                return property.call(itemStack, (ClientLevel) level, (LivingEntity) entity, flags);
            }
            return Float.NEGATIVE_INFINITY;
        };
    }
}
