package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.world.entity.Entity;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[16, )")
@Extension
public class EntityInfoExt {

    public static boolean shouldSit(@This Entity entity) {
        if (entity.isPassenger()) {
            var vehicle = entity.getVehicle();
            if (vehicle != null) {
                //return vehicle.shouldRiderSit();
                return true;
            }
        }
        return false;
    }

    public static float getHeadPatch(@This Entity entity, float partialTick) {
        return -OpenMath.lerp(partialTick, entity.xRotO, entity.getXRot());
    }

    public static float getHeadYaw(@This Entity entity, float partialTick) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return 0;
        }
        float lerpBodyRot = OpenMath.rotLerp(partialTick, livingEntity.yBodyRotO, livingEntity.yBodyRot);
        float lerpHeadRot = OpenMath.rotLerp(partialTick, livingEntity.yHeadRotO, livingEntity.yHeadRot);
        float netHeadYaw = lerpHeadRot - lerpBodyRot;
        if (livingEntity.isPassenger() && livingEntity.getVehicle() instanceof LivingEntity livingEntity2) {
            lerpBodyRot = OpenMath.rotLerp(partialTick, livingEntity2.yBodyRotO, livingEntity2.yBodyRot);
            netHeadYaw = lerpHeadRot - lerpBodyRot;
            float clampedHeadYaw = OpenMath.clamp(OpenMath.wrapDegrees(netHeadYaw), -85, 85);
            lerpBodyRot = lerpHeadRot - clampedHeadYaw;
            if (clampedHeadYaw * clampedHeadYaw > 2500.0F) {
                lerpBodyRot += clampedHeadYaw * 0.2F;
            }
            netHeadYaw = lerpHeadRot - lerpBodyRot;
        }
        return -OpenMath.clamp(OpenMath.wrapDegrees(netHeadYaw), -85, 85);
    }
}
