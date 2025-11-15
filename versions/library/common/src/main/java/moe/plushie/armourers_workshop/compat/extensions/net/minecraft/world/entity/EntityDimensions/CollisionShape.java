package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.world.entity.EntityDimensions;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.data.EntityCollisionShape;
import net.minecraft.world.entity.EntityDimensions;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[1.16, )")
@Extension
public class CollisionShape {

    public static EntityDimensions withCollisionShape(@This EntityDimensions dimensions, EntityCollisionShape shape) {
        var rect = shape.rect();
        float newWidth = rect.width();
        float newHeight = rect.height();
        return EntityDimensions.scalable(newWidth, newHeight).withEyeHeight(rect.y());
    }
}
