package moe.plushie.armourers_workshop.core.client.animation.handler;

import moe.plushie.armourers_workshop.compat.client.particle.AbstractParticleManagerImpl;
import moe.plushie.armourers_workshop.core.client.particle.SmartParticle;
import moe.plushie.armourers_workshop.core.client.particle.SmartParticleEmitter;
import moe.plushie.armourers_workshop.core.client.particle.SmartParticleManager;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationPoint;
import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OptimizedExpression;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.minecraft.client.Minecraft;

public class AnimationParticleHandler implements OptimizedExpression<Object> {

    private final String effect;
    private final String locator;

    private final SmartParticle particle;

    public AnimationParticleHandler(SkinAnimationPoint.Particle particle) {
        this.effect = particle.effect();
        this.locator = particle.locator();
        this.particle = SmartParticleManager.getInstance().register(particle.provider());
    }

    @Override
    public Runnable evaluate(ExecutionContext context) {
        SmartParticleManager.getInstance().open(particle);
        var particleEmitter = createParticleEmitter(context);
        startPlay(particleEmitter);
        return () -> {
            stopPlay(particleEmitter);
            RenderSystem.recordRenderCall(() -> SmartParticleManager.getInstance().close(this.particle));
        };
    }

    private void startPlay(SmartParticleEmitter particleEmitter) {
        getParticleManager().aw2$add(particleEmitter);
        if (ModConfig.Client.enableAnimationDebug) {
            ModLog.debug("start play {}", this);
        }
    }

    private void stopPlay(SmartParticleEmitter particleEmitter) {
        getParticleManager().aw2$remove(particleEmitter);
        if (ModConfig.Client.enableAnimationDebug) {
            ModLog.debug("stop play {}", this);
        }
    }

    private SmartParticleEmitter createParticleEmitter(ExecutionContext context) {
        return particle.spawn(context);
    }

    private AbstractParticleManagerImpl getParticleManager() {
        return (AbstractParticleManagerImpl) Minecraft.getInstance().particleEngine;
    }

    @Override
    public String toString() {
        return Objects.toString(this, "particle", particle);
    }
}
