package moe.plushie.armourers_workshop.core.skin.serializer.importer.blockbench;

import moe.plushie.armourers_workshop.core.math.OpenVector2f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;

import java.util.HashMap;
import java.util.Map;

public class BlockBenchCube extends BlockBenchElement {

    private final boolean boxUV;
    private final boolean mirrorUV;
    private final boolean allowMirrorModeling;

    private final String renderOrder;

    private final OpenVector2f uvOffset;

    private final OpenVector3f from;
    private final OpenVector3f to;

    private final OpenVector3f origin;
    private final OpenVector3f rotation;
    private final float inflate;

    private final Map<OpenDirection, BlockBenchCubeFace> faces;

    public BlockBenchCube(String uuid, String name, String type, boolean allowExport, boolean allowMirrorModeling, String renderOrder, boolean boxUV, boolean mirrorUV, OpenVector2f uvOffset, OpenVector3f from, OpenVector3f to, OpenVector3f origin, OpenVector3f rotation, float inflate, Map<OpenDirection, BlockBenchCubeFace> faces) {
        super(uuid, name, type, allowExport);
        this.boxUV = boxUV;
        this.mirrorUV = mirrorUV;
        this.allowMirrorModeling = allowMirrorModeling;
        this.renderOrder = renderOrder;
        this.uvOffset = uvOffset;
        this.from = from;
        this.to = to;
        this.origin = origin;
        this.rotation = rotation;
        this.inflate = inflate;
        this.faces = faces;
    }

    public OpenVector3f getFrom() {
        return from;
    }

    public OpenVector3f getTo() {
        return to;
    }

    public OpenVector3f getOrigin() {
        return origin;
    }

    public OpenVector3f getRotation() {
        return rotation;
    }

    public float getInflate() {
        return inflate;
    }

    public boolean isBoxUV() {
        return boxUV;
    }

    public boolean isMirrorUV() {
        return mirrorUV;
    }

    public String getRenderOrder() {
        return renderOrder;
    }

    public OpenVector2f getUVOffset() {
        return uvOffset;
    }

    public Map<OpenDirection, BlockBenchCubeFace> getFaces() {
        return faces;
    }

    protected static class Builder extends BlockBenchElement.Builder {

        protected boolean boxUV = false;
        protected boolean mirrorUV = false;
        protected boolean allowMirrorModeling = false;

        protected OpenVector2f uvOffset = OpenVector2f.ZERO;

        protected OpenVector3f from = OpenVector3f.ZERO;
        protected OpenVector3f to = OpenVector3f.ZERO;

        protected OpenVector3f origin = OpenVector3f.ZERO;
        protected OpenVector3f rotation = OpenVector3f.ZERO;

        protected String renderOrder = "default"; // default,behind,in_front

        protected float inflate = 0;

        protected final Map<OpenDirection, BlockBenchCubeFace> faces = new HashMap<>();

        public void boxUV(boolean boxUV) {
            this.boxUV = boxUV;
        }

        public void mirrorUV(boolean mirrorUV) {
            this.mirrorUV = mirrorUV;
        }

        public void allowMirrorModeling(boolean allowMirrorModeling) {
            this.allowMirrorModeling = allowMirrorModeling;
        }

        public void renderOrder(String renderOrder) {
            this.renderOrder = renderOrder;
        }

        public void from(OpenVector3f from) {
            this.from = from;
        }

        public void to(OpenVector3f to) {
            this.to = to;
        }

        public void origin(OpenVector3f origin) {
            this.origin = origin;
        }

        public void rotation(OpenVector3f rotation) {
            this.rotation = rotation;
        }

        public void inflate(float inflate) {
            this.inflate = inflate;
        }

        public void uvOffset(OpenVector2f uvOffset) {
            this.uvOffset = uvOffset;
        }

        public void addFace(OpenDirection dir, BlockBenchCubeFace face) {
            this.faces.put(dir, face);
        }

        @Override
        public BlockBenchCube build() {
            return new BlockBenchCube(uuid, name, type, allowExport, allowMirrorModeling, renderOrder, boxUV, mirrorUV, uvOffset, from, to, origin, rotation, inflate, faces);
        }
    }
}
