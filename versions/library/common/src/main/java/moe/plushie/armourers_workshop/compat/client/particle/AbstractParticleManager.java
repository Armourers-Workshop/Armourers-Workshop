package moe.plushie.armourers_workshop.compat.client.particle;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.compat.client.AbstractCamera;

import java.util.ArrayList;

@Available("[16, )")
@OnlyIn(Dist.CLIENT)
public class AbstractParticleManager {

    private final ArrayList<AbstractParticleInstance> particleInstances = new ArrayList<>();

    public void render(AbstractCamera camera, float partialTick, IGraphicsContext context) {
        context.saveGraphicsState();
        context.translateCTM(camera.position().scaling(-1));

        for (var particleInstance : particleInstances) {
            particleInstance.render(camera, partialTick, context);
        }

        context.restoreGraphicsState();
    }

    public void tick() {
        particleInstances.forEach(AbstractParticleInstance::tick);
        particleInstances.removeIf(AbstractParticleInstance::isRemoved);
    }

    public void add(AbstractParticleInstance particleInstance) {
        particleInstances.add(particleInstance);
        particleInstance.prepare();
    }

    public void remove(AbstractParticleInstance particleInstance) {
        particleInstances.remove(particleInstance);
    }

    public void clear() {
        particleInstances.clear();
    }
}
