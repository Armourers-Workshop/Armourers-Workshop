package moe.plushie.armourers_workshop.core.skin.transformer.blockbench;

import moe.plushie.armourers_workshop.utils.math.Vector2f;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.core.Direction;

import java.util.HashMap;
import java.util.Map;

public class BlockBenchElement extends BlockBenchObject {

    private final String type;

    private final boolean boxUV;
    private final boolean allowMirrorModeling;
    private final boolean allowExport;

    private final Vector2f uvOffset;

    private final Vector3f from;
    private final Vector3f to;

    private final Vector3f origin;
    private final Vector3f rotation;
    private final float inflate;

    private final Map<Direction, BlockBenchFace> faces;

    public BlockBenchElement(String uuid, String name, String type, boolean boxUV, boolean allowMirrorModeling, boolean allowExport, Vector2f uvOffset, Vector3f from, Vector3f to, Vector3f origin, Vector3f rotation, float inflate, Map<Direction, BlockBenchFace> faces) {
        super(uuid, name);
        this.type = type;
        this.boxUV = boxUV;
        this.allowMirrorModeling = allowMirrorModeling;
        this.allowExport = allowExport;
        this.uvOffset = uvOffset;
        this.from = from;
        this.to = to;
        this.origin = origin;
        this.rotation = rotation;
        this.inflate = inflate;
        this.faces = faces;
    }

    public String getType() {
        return type;
    }

    public Vector3f getFrom() {
        return from;
    }

    public Vector3f getTo() {
        return to;
    }

    public Vector3f getOrigin() {
        return origin;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public float getInflate() {
        return inflate;
    }

    public boolean isBoxUV() {
        return boxUV;
    }

    public boolean allowExport() {
        return allowExport;
    }

    public Vector2f getUVOffset() {
        return uvOffset;
    }

    public Map<Direction, BlockBenchFace> getFaces() {
        return faces;
    }

    public static class Builder extends BlockBenchObject.Builder {

        private String type = "cube";

        private boolean boxUV = false;
        private boolean allowMirrorModeling = false;
        private boolean allowExport = true;

        private Vector2f uvOffset = Vector2f.ZERO;

        private Vector3f from = Vector3f.ZERO;
        private Vector3f to = Vector3f.ZERO;

        private Vector3f origin = Vector3f.ZERO;
        private Vector3f rotation = Vector3f.ZERO;
        private float inflate = 0;

        private final HashMap<Direction, BlockBenchFace> faces = new HashMap<>();

        public void type(String type) {
            this.type = type;
        }

        public void boxUV(boolean boxUV) {
            this.boxUV = boxUV;
        }

        public void allowMirrorModeling(boolean allowMirrorModeling) {
            this.allowMirrorModeling = allowMirrorModeling;
        }

        public void from(Vector3f from) {
            this.from = from;
        }

        public void to(Vector3f to) {
            this.to = to;
        }

        public void origin(Vector3f origin) {
            this.origin = origin;
        }

        public void rotation(Vector3f rotation) {
            this.rotation = rotation;
        }

        public void inflate(float inflate) {
            this.inflate = inflate;
        }

        public void uvOffset(Vector2f uvOffset) {
            this.uvOffset = uvOffset;
        }

        public void export(boolean allowExport) {
            this.allowExport = allowExport;
        }

        public void addFace(Direction dir, BlockBenchFace face) {
            this.faces.put(dir, face);
        }

        public BlockBenchElement build() {
            return new BlockBenchElement(uuid, name, type, boxUV, allowMirrorModeling, allowExport, uvOffset, from, to, origin, rotation, inflate, faces);
        }
    }
}
