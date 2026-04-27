package moe.plushie.armourers_workshop.compat.client.particle;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.compat.client.AbstractCamera;
import moe.plushie.armourers_workshop.core.client.particle.SmartParticleEmitter;
import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.particle.runtime.GraphicsAccessor;

@Available("[16, )")
@OnlyIn(Dist.CLIENT)
public interface AbstractParticleRenderer extends GraphicsAccessor {

    void prepare(AbstractCamera camera, float partialTick, SmartParticleEmitter emitter);

    void flush(IGraphicsContext context);

    void setTintColor(int tintColor);

    void setInitialPosition(OpenVector3f position);

    void setInitialRotation(OpenQuaternionf position);
}
