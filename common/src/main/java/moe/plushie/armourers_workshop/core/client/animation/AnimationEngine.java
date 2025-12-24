package moe.plushie.armourers_workshop.core.client.animation;

import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.other.ConcurrentRenderingContext;
import moe.plushie.armourers_workshop.core.skin.molang.MolangVirtualMachine;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.core.ast.Constant;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.SyntaxException;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;
import org.jetbrains.annotations.Nullable;

public class AnimationEngine {

    private static final MolangVirtualMachine VM = new MolangVirtualMachine();

    public static void apply(@Nullable Object source, BakedSkin skin, ConcurrentRenderingContext context) {
        // If not found it means it is not any animation active.
        var animationContext = context.animationManager().getAnimationContext(skin);
        if (animationContext == null) {
            return;
        }
        VM.beginVariableCaching();
        apply(source, skin.id(), context.partialTick(), context.animationTick(), animationContext);
        VM.endVariableCaching();
    }

    public static void apply(@Nullable Object source, int skinId, float partialTick, double animationTime, AnimationContext context) {
        context.beginUpdates(animationTime);
        var executionContext = context.executionContext();
        for (var animationController : context.animationControllers()) {
            // query the current play state of the animation controller.
            var playState = context.getPlayState(animationController);
            if (playState == null) {
                continue;
            }
            // we only bind it when transformer use the molang environment.
            var adjustedTime = playState.adjustedTime(animationTime);
            if (animationController.isRequiresVirtualMachine()) {
                executionContext.upload(skinId, playState.time(), adjustedTime, animationTime, partialTick);
            }

            // check/switch frames of animation and write to applier.
            animationController.process(adjustedTime, playState, executionContext);
        }
        context.commitUpdates();
    }


    public static Expression compile(String source) throws SyntaxException {
        return VM.compile(source);
    }

    public static Expression compile(OpenPrimitive object, double defaultValue) {
        try {
            if (object.isNumber()) {
                return new Constant(object.doubleValue());
            }
            if (object.isString()) {
                return compile(object.stringValue());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return new Constant(defaultValue);
    }
}

