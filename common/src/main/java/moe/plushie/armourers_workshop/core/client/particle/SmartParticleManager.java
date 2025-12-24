package moe.plushie.armourers_workshop.core.client.particle;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleData;

import java.util.IdentityHashMap;

@OnlyIn(Dist.CLIENT)
public class SmartParticleManager {

    private static final SmartParticleManager INSTANCE = new SmartParticleManager();

    protected final IdentityHashMap<Object, SmartParticle> particles = new IdentityHashMap<>();

    public static SmartParticleManager getInstance() {
        return INSTANCE;
    }

    public static void start() {
    }

    public static void stop() {
        // release all registered particles.
        INSTANCE.particles.values().forEach(SmartParticle::unbind);
        INSTANCE.particles.clear();
    }

    public synchronized SmartParticle register(SkinParticleData provider) {
        var particle = particles.get(provider);
        if (particle == null) {
            particle = new SmartParticle(provider);
            particles.put(provider, particle);
        }
        return particle;
    }

    public void open(SmartParticle particle) {
        particle.retain();
    }

    public void close(SmartParticle particle) {
        particle.release();
    }
}
