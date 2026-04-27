package moe.plushie.armourers_workshop.compat.client.particle;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.compat.client.AbstractCamera;

@Available("[16, )")
@OnlyIn(Dist.CLIENT)
public interface AbstractParticleInstance {

    void prepare();

    void tick();

    void render(AbstractCamera camera, float partialTick, IGraphicsContext context);

    default boolean isRemoved() {
        return false;
    }

    default boolean shouldRenderInScreen() {
        return false;
    }
}
