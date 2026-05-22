package moe.plushie.armourers_workshop.core.client.animation;

import moe.plushie.armourers_workshop.core.client.animation.effect.ClientInstructEffect;
import moe.plushie.armourers_workshop.core.client.animation.effect.ClientParticleEffect;
import moe.plushie.armourers_workshop.core.client.animation.effect.ClientSoundEffect;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.other.ConcurrentRenderingContext;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimation;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationData;
import moe.plushie.armourers_workshop.core.skin.animation.runtime.SkinAnimationCompiler;
import moe.plushie.armourers_workshop.core.skin.molang.MolangVirtualMachine;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.utils.OptimizedExpression;
import org.jetbrains.annotations.Nullable;

public class AnimationEngine {

    private static final MolangVirtualMachine VM = new MolangVirtualMachine();
    private static final SkinAnimationCompiler COMPILER = new SkinAnimationCompiler(VM) {

        @Override
        protected OptimizedExpression<?> compile(SkinAnimationData data, SkinAnimationData.Point point) {
            if (point instanceof SkinAnimationData.Point.Instruct instruct) {
                return new ClientInstructEffect(compile(instruct.script(), 0));
            }
            if (point instanceof SkinAnimationData.Point.Sound sound) {
                return new ClientSoundEffect(sound);
            }
            if (point instanceof SkinAnimationData.Point.Particle particle) {
                return new ClientParticleEffect(particle);
            }
            return super.compile(data, point);
        }
    };


    public static Expression compile(String script) {
        return COMPILER.compile(script, 0);
    }

    public static SkinAnimation compile(SkinAnimationData animation) {
        return COMPILER.compile(animation);
    }


    public static void apply(@Nullable Object source, BakedSkin skin, ConcurrentRenderingContext context) {
        var manager = context.animationManager();
        if (manager == AnimationManager.NONE) {
            return;
        }
        beginVariableCaching();
        manager.process(skin, context.animationTick(), context.partialTick());
        endVariableCaching();
    }

    public static void beginVariableCaching() {
        VM.beginVariableCaching();
    }

    public static void endVariableCaching() {
        VM.endVariableCaching();
    }
}
