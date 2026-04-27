package moe.plushie.armourers_workshop.core.skin.animation.runtime;

import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimation;
import moe.plushie.armourers_workshop.core.skin.animation.core.SkinAnimationEffect;
import moe.plushie.armourers_workshop.core.skin.animation.core.SkinAnimationTransform;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTransform;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Objects;

import java.util.Map;

public class SkinAnimationLinker {

    private final Map<String, SkinPartTransform> partTransforms;

    public SkinAnimationLinker(Map<String, SkinPartTransform> partTransforms) {
        this.partTransforms = partTransforms;
    }

    public void link(SkinAnimation animation) {
        // search all bone output, and then link to part transform.
        Collections.filter(animation.outputs(), SkinAnimation.Output::isBone).forEach(output -> {
            // create or get the transform by the part transform.
            var targetTransform = createTransform(animation, output);
            if (targetTransform == null) {
                return;
            }
            // attach to animation transform.
            output.addListener(targetTransform::setDirty);
            targetTransform.add(Objects.unsafeCast(output));

            // a transform corresponds to multiple channels, we can't add it repeatedly.
            var affectedTransforms = animation.affectedTransforms();
            if (!affectedTransforms.contains(targetTransform)) {
                affectedTransforms.add(targetTransform);
            }
        });
        // search all bone output, and then link to affected effects.
        Collections.filter(animation.outputs(), SkinAnimation.Output::isEffect).forEach(output -> {
            // create or get the effect by the animation.
            var targetEffect = createEffect(animation, output);
            if (targetEffect == null) {
                return;
            }
            // attach to animation transform.
            output.addListener(targetEffect::setDirty);
            targetEffect.add(Objects.unsafeCast(output));
        });
    }

    private SkinAnimationEffect createEffect(SkinAnimation animation, SkinAnimation.Output<?> output) {
        var affectedEffects = animation.affectedEffects();
        for (var effect : affectedEffects) {
            return effect;
        }
        var effect = new SkinAnimationEffect();
        affectedEffects.add(effect);
        return effect;
    }

    private SkinAnimationTransform createTransform(SkinAnimation animation, SkinAnimation.Output<?> output) {
        var partTransform = partTransforms.get(output.bone());
        if (partTransform != null) {
            return SkinAnimationTransform.of(partTransform);
        }
        return null;
    }
}
