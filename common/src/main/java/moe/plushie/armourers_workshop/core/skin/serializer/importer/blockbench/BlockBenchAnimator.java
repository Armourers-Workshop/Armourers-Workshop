package moe.plushie.armourers_workshop.core.skin.serializer.importer.blockbench;

import java.util.ArrayList;
import java.util.List;

public class BlockBenchAnimator extends BlockBenchObject {

    private final String type;
    private final List<BlockBenchKeyframe> keyframes;

    public BlockBenchAnimator(String uuid, String name, String type, List<BlockBenchKeyframe> keyframes) {
        super(uuid, name);
        this.type = type;
        this.keyframes = keyframes;
    }

    public String type() {
        return type;
    }

    public List<BlockBenchKeyframe> keyframes() {
        return keyframes;
    }

    protected static class Builder extends BlockBenchObject.Builder {

        private String type = "bone";
        private final List<BlockBenchKeyframe> keyframes = new ArrayList<>();

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

        public void addFrame(BlockBenchKeyframe frame) {
            this.keyframes.add(frame);
        }

        public BlockBenchAnimator build() {
            // block bench will i18n the effects name, which means it is a dynamic name.
            if (type.equals("effect")) {
                name = "armourers:effects";
            }
            return new BlockBenchAnimator(uuid, name, type, keyframes);
        }
    }
}
