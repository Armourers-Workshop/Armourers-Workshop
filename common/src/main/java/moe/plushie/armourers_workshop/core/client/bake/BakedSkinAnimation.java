package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.core.client.animation.AnimationOutput;
import moe.plushie.armourers_workshop.core.client.animation.AnimationProcessor;
import moe.plushie.armourers_workshop.core.client.animation.AnimationTransform;
import moe.plushie.armourers_workshop.core.data.transform.SkinTransform;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimation;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationLoop;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationValue;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.ThreadUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class BakedSkinAnimation {

    private final int id = ThreadUtils.nextId(BakedSkinAnimation.class);

    private final float duration;

    private final String name;
    private final SkinAnimationLoop loop;

    private final SkinAnimation animation;

    private final ArrayList<AnimationOutput> outputs = new ArrayList<>();
    private final ArrayList<AnimationProcessor> processors = new ArrayList<>();

    public BakedSkinAnimation(SkinAnimation animation) {
        this.name = animation.getName();
        this.duration = animation.getDuration();
        this.loop = animation.getLoop();
        this.animation = animation;
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
        return outputs.isEmpty();
    }

    public int getId() {
        return id;
    }

    public int getChannels() {
        return processors.size();
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

    public ArrayList<AnimationOutput> getOutputs() {
        return outputs;
    }

    public List<AnimationProcessor> getProcessors() {
        return processors;
    }

    private void build(List<SkinAnimationValue> linkedValues, List<BakedSkinPart> linkedParts) {
        var nameProcessors = new LinkedHashMap<String, AnimationProcessor>();
        for (var value : linkedValues) {
            nameProcessors.computeIfAbsent(value.getKey(), AnimationProcessor::create).add(value);
        }
        for (var part : linkedParts) {
            var output = buildStream(part);
            nameProcessors.values().forEach(it -> it.add(output));
            outputs.add(output);
        }
        for (var processor : nameProcessors.values()) {
            if (processor.freeze()) {
                processor.setChannel(processors.size());
                processors.add(processor);
            }
        }
    }

    private AnimationOutput buildStream(BakedSkinPart part) {
        // when animation transform already been created, we just link it directly.
        for (var transform : part.getTransform().getChildren()) {
            if (transform instanceof AnimationTransform animatedTransform) {
                return new AnimationOutput(animatedTransform);
            }
        }
        // if part have a non-standard transform (preview mode),
        // we wil think this part can't be support animation.
        if (!(part.getPart().getTransform() instanceof SkinTransform parent)) {
            return new AnimationOutput(null);
        }
        // we will replace the standard transform to animated transform.
        var animatedTransform = new AnimationTransform(parent);
        part.getTransform().replaceChild(parent, animatedTransform);
        return new AnimationOutput(animatedTransform);
    }
}
