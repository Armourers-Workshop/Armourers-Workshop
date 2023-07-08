package extensions.net.minecraft.world.entity.LivingEntity;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.world.entity.LivingEntity;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
@Available("[1.16, 1.20)")
public class AnimationModifier {

    public static void applyLimitLimbs(@This LivingEntity entity) {
        if (entity.animationSpeed > 0.25F) {
            entity.animationSpeed = 0.25F;
            entity.animationSpeedOld = 0.25F;
        }
    }
}
