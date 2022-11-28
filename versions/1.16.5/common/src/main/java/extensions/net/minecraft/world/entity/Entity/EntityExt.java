package extensions.net.minecraft.world.entity.Entity;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import net.minecraft.world.entity.Entity;

@Extension
public class EntityExt {


    public static void setYRot(@This Entity entity, float value) {
        entity.yRot = value;
    }

    public static float getYRot(@This Entity entity) {
        return entity.yRot;
    }

    public static void setXRot(@This Entity entity, float value) {
        entity.xRot = value;
    }

    public static float getXRot(@This Entity entity) {
        return entity.xRot;
    }
}
