package moe.plushie.armourers_workshop.compat.client.particle;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;

@Available("[1.16, )")
@OnlyIn(Dist.CLIENT)
public interface AbstractParticleManagerImpl {

    void aw2$add(AbstractParticleInstance particleInstance);

    void aw2$remove(AbstractParticleInstance particleInstance);
}
