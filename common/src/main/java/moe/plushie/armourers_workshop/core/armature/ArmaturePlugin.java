package moe.plushie.armourers_workshop.core.armature;

import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import net.minecraft.world.entity.Entity;

public abstract class ArmaturePlugin {

    public void prepare(Entity entity, Context context) {
    }

    public void activate(Entity entity, Context context) {
    }

    public void deactivate(Entity entity, Context context) {
    }

    public boolean freeze() {
        return true;
    }


    public interface Context {

        int overlay();

        int lightmap();

        float partialTicks();

        double animationTicks();

        IPoseStack poseStack();

        EntityRenderData renderData();
    }
}
