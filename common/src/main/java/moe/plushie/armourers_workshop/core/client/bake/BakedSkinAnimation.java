package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.core.client.animation.AnimationEngine;
import moe.plushie.armourers_workshop.core.client.animation.AnimationTransform;
import moe.plushie.armourers_workshop.core.client.animation.AnimationTransformer;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimation;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationLoop;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationValue;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.ThreadUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class BakedSkinAnimation {

    private final int id = ThreadUtils.BAKED_SKIN_ANIMATION_COUNTER.getAndIncrement();

    private final float duration;

    private final String name;
    private final SkinAnimationLoop loop;

    private final SkinAnimation animation;

    private final ArrayList<AnimationTransform> transforms = new ArrayList<>();
    private final ArrayList<AnimationTransformer> transformers = new ArrayList<>();

    public BakedSkinAnimation(SkinAnimation animation) {
        this.name = animation.getName();
        this.duration = animation.getDuration();
        this.loop = animation.getLoop();
        this.animation = animation;
    }

    public void setup(Entity entity, SkinRenderContext context) {
        // we needs reset the applier.
        var state = context.getAnimationState(this);
        if (state == null) {
            transforms.forEach(AnimationTransform::reset);
            return;
        }
        // we only bind it when transformer use the molang environment.
        var partialTicks = state.getPartialTicks(context.getAnimationTicks());
        if (state.isRequiresVirtualMachine()) {
            AnimationEngine.upload(entity, partialTicks, state.getStartTime());
        }
        // check/switch frames of animation and write to applier.
        for (var transformer : transformers) {
            transformer.process(state, partialTicks, entity, context);
        }
    }

    public void link(Map<String, List<BakedSkinPart>> namedParts) {
        animation.getValues().forEach((name, linkedValues) -> {
            var linkedParts = namedParts.get(name);
            if (linkedParts != null) {
                build(linkedValues, linkedParts);
            }
        });
    }

    @Override
    public String toString() {
        return ObjectUtils.makeDescription(this, "name", name, "duration", duration);
    }

    public boolean isEmpty() {
        return transforms.isEmpty();
    }

    public int getId() {
        return id;
    }

    public int getChannels() {
        return transformers.size();
    }

    public float getDuration() {
        return duration;
    }

    public String getName() {
        return name;
    }

    public SkinAnimationLoop getLoop() {
        return loop;
    }

    public List<AnimationTransformer> getTransformers() {
        return transformers;
    }

    private void build(List<SkinAnimationValue> linkedValues, List<BakedSkinPart> linkedParts) {
        var namedAnimators = new LinkedHashMap<String, AnimationTransformer>();
        for (var value : linkedValues) {
            namedAnimators.computeIfAbsent(value.getKey(), AnimationTransformer::create).add(value);
        }
        for (var part : linkedParts) {
            var transform = new AnimationTransform(part);
            part.getTransform().addTransform(transform);
            namedAnimators.values().forEach(it -> it.add(transform));
            transforms.add(transform);
        }
        for (var transformer : namedAnimators.values()) {
            if (transformer.freeze()) {
                transformer.setChannel(transformers.size());
                transformers.add(transformer);
            }
        }
    }
}
