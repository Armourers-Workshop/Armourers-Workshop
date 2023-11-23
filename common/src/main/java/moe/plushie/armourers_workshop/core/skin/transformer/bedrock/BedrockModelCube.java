package moe.plushie.armourers_workshop.core.skin.transformer.bedrock;

import moe.plushie.armourers_workshop.utils.math.Size3f;
import moe.plushie.armourers_workshop.utils.math.Vector3f;

public class BedrockModelCube {

    private final Vector3f pivot;
    private final Vector3f rotation;
    private final Vector3f origin;
    private final Size3f size;
    private final BedrockModelUV uv;
    private final float inflate;
    private final boolean mirror;

    public BedrockModelCube(Vector3f pivot, Vector3f rotation, Vector3f origin, Size3f size, BedrockModelUV uv, float inflate, boolean mirror) {
        this.pivot = pivot;
        this.rotation = rotation;
        this.origin = origin;
        this.size = size;
        this.uv = uv;
        this.inflate = inflate;
        this.mirror = mirror;
    }

    public Vector3f getPivot() {
        return pivot;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public Vector3f getOrigin() {
        return origin;
    }

    public Size3f getSize() {
        return size;
    }

    public BedrockModelUV getUV() {
        return uv;
    }

    public float getInflate() {
        return inflate;
    }

    public boolean isMirror() {
        return mirror;
    }

    public static class Builder {

        private Vector3f pivot = Vector3f.ZERO;
        private Vector3f rotation = Vector3f.ZERO;
        private Vector3f origin = Vector3f.ZERO;
        private Size3f size = Size3f.ZERO;
        private BedrockModelUV uv = BedrockModelUV.EMPTY;
        private float inflate = 0;
        private boolean mirror = false;

        public void pivot(Vector3f pivot) {
            this.pivot = pivot;
        }

        public void rotation(Vector3f rotation) {
            this.rotation = rotation;
        }

        public void origin(Vector3f origin) {
            this.origin = origin;
        }

        public void size(Size3f size) {
            this.size = size;
        }

        public void uv(BedrockModelUV uv) {
            this.uv = uv;
        }

        public void inflate(float inflate) {
            this.inflate = inflate;
        }

        public void mirror(boolean mirror) {
            this.mirror = mirror;
        }

        public BedrockModelCube build() {
            return new BedrockModelCube(pivot, rotation, origin, size, uv, inflate, mirror);
        }
    }
}
