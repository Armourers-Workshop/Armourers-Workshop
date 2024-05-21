package moe.plushie.armourers_workshop.core.skin.transformer.blockbench;

import java.util.ArrayList;
import java.util.List;

public class BlockBenchAnimator extends BlockBenchObject {

    private final String type;
    private final List<BlockBenchKeyFrame> keyframes;

    public BlockBenchAnimator(String uuid, String name, String type, List<BlockBenchKeyFrame> keyframes) {
        super(uuid, name);
        this.type = type;
        this.keyframes = keyframes;
    }

    public String getType() {
        return type;
    }

    public List<BlockBenchKeyFrame> getKeyframes() {
        return keyframes;
    }

    public static class Builder extends BlockBenchObject.Builder {

        private String type = "bone";
        private final ArrayList<BlockBenchKeyFrame> keyframes = new ArrayList<>();

        public Builder(String uuid) {
            this.uuid = uuid;
        }

        // "override": false,
        // "snapping": 24,
        // "anim_time_update": "",
        // "blend_weight": "",

        public void type(String type) {
            this.type = type;
        }

        public void addFrame(BlockBenchKeyFrame frame) {
            this.keyframes.add(frame);
        }

        public BlockBenchAnimator build() {
            return new BlockBenchAnimator(uuid, name, type, keyframes);
        }
    }
}
