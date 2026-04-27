package moe.plushie.armourers_workshop.core.skin.serializer.importer.blockbench;

import java.util.ArrayList;
import java.util.List;

public class BlockBenchAnimator extends BlockBenchObject {

    private final String type;
    private final List<BlockBenchKeyframe> keyframes;

    private final boolean rotationGlobal;
    private final boolean quaternionInterpolation;

    public BlockBenchAnimator(String uuid, String name, String type, List<BlockBenchKeyframe> keyframes, boolean rotationGlobal, boolean quaternionInterpolation) {
        super(uuid, name);
        this.type = type;
        this.keyframes = keyframes;
        this.rotationGlobal = rotationGlobal;
        this.quaternionInterpolation = quaternionInterpolation;
    }

    public String type() {
        return type;
    }

    public List<BlockBenchKeyframe> keyframes() {
        return keyframes;
    }

    public boolean isRotationGlobal() {
        return rotationGlobal;
    }

    public boolean isQuaternionInterpolation() {
        return quaternionInterpolation;
    }

    protected static class Builder extends BlockBenchObject.Builder {

        private String type = "bone";
        private boolean rotationGlobal = false;
        private boolean quaternionInterpolation = false;
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

        public void rotationGlobal(boolean rotationGlobal) {
            this.rotationGlobal = rotationGlobal;
        }

        public void quaternionInterpolation(boolean quaternionInterpolation) {
            this.quaternionInterpolation = quaternionInterpolation;
        }

        public void addFrame(BlockBenchKeyframe frame) {
            this.keyframes.add(frame);
        }

        public BlockBenchAnimator build() {
            // block bench will i18n the effects name, which means it is a dynamic name.
            if (type.equals("effect")) {
                name = "armourers:effects";
            }
            return new BlockBenchAnimator(uuid, name, type, keyframes, rotationGlobal, quaternionInterpolation);
        }
    }
}
