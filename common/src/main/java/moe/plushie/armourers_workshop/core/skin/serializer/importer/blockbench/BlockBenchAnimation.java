package moe.plushie.armourers_workshop.core.skin.serializer.importer.blockbench;

import java.util.ArrayList;
import java.util.List;

public class BlockBenchAnimation extends BlockBenchObject {

    private final float duration;
    private final String loop;
    private final List<BlockBenchAnimator> animators;

    public BlockBenchAnimation(String uuid, String name, float duration, String loop, List<BlockBenchAnimator> animators) {
        super(uuid, name);
        this.duration = duration;
        this.loop = loop;
        this.animators = animators;
    }

    public float duration() {
        return duration;
    }

    public String loop() {
        return loop;
    }

    public List<BlockBenchAnimator> animators() {
        return animators;
    }

    protected static class Builder extends BlockBenchObject.Builder {

        private float duration = 0;
        private String loop = "loop"; // once, hold, loop
        private final List<BlockBenchAnimator> animators = new ArrayList<>();

        public void loop(String mode) {
            this.loop = mode;
        }

        public void duration(float duration) {
            this.duration = duration;
        }

        public void addAnimator(BlockBenchAnimator animator) {
            this.animators.add(animator);
        }

        public BlockBenchAnimation build() {
            return new BlockBenchAnimation(uuid, name, duration, loop, animators);
        }
    }
}
