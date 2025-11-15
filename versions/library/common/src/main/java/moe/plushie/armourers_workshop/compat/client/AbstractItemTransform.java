package moe.plushie.armourers_workshop.compat.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.utils.OpenItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransform;

@Available("[1.16, 1.22)")
public class AbstractItemTransform {

    public static OpenItemTransform wrap(ItemTransform transform) {
        var translation = transform.translation;
        var rotation = transform.rotation;
        var scale = transform.scale;
        return OpenItemTransform.create(new OpenVector3f(translation.x(), translation.y(), translation.z()), new OpenVector3f(rotation.x(), rotation.y(), rotation.z()), new OpenVector3f(scale.x(), scale.y(), scale.z()));
    }
}

