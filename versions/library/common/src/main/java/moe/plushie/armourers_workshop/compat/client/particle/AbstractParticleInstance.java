package moe.plushie.armourers_workshop.compat.client.particle;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.compat.client.AbstractCamera;

@Available("[1.16, )")
public interface AbstractParticleInstance {

    void prepare();

    void tick();

    void render(AbstractCamera camera, float partialTick, IGraphicsContext context);

    default boolean shouldRenderInScreen() {
        return false;
    }
}
