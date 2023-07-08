package extensions.net.minecraft.world.entity.LivingEntity;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.world.entity.LivingEntity;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
@Available("[1.20, )")
public class AnimationModifier {

    public static void applyLimitLimbs(@This LivingEntity entity) {
        if (entity.walkAnimation.speed() > 0.25f) {
            entity.walkAnimation.setSpeed(0.25f);
            entity.walkAnimation.update(0, 1); // keep position and set speedOld
            entity.walkAnimation.setSpeed(0.25f);
        }
    }
}
