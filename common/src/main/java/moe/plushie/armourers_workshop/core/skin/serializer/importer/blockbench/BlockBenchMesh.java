package moe.plushie.armourers_workshop.core.skin.serializer.importer.blockbench;

import moe.plushie.armourers_workshop.core.math.OpenVector2f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;

import java.util.LinkedHashMap;
import java.util.Map;

/// https://github.com/JannisX11/blockbench/blob/master/js/outliner/mesh.js
public class BlockBenchMesh extends BlockBenchElement {

    private final boolean boxUV;
    private final boolean mirrorUV;
    private final boolean allowMirrorModeling;

    private final String renderOrder;

    private final OpenVector2f uvOffset;

    private final OpenVector3f origin;
    private final OpenVector3f rotation;

    private final Map<String, BlockBenchMeshFace> faces;
    private final Map<String, OpenVector3f> vertices;

    public BlockBenchMesh(String uuid, String name, String type, boolean allowExport, boolean allowMirrorModeling, String renderOrder, boolean boxUV, boolean mirrorUV, OpenVector2f uvOffset, OpenVector3f origin, OpenVector3f rotation, Map<String, BlockBenchMeshFace> faces, Map<String, OpenVector3f> vertices) {
        super(uuid, name, type, allowExport);
        this.origin = origin;
        this.rotation = rotation;
        this.boxUV = boxUV;
        this.mirrorUV = mirrorUV;
        this.allowMirrorModeling = allowMirrorModeling;
        this.renderOrder = renderOrder;
        this.uvOffset = uvOffset;
        this.faces = faces;
        this.vertices = vertices;
    }

    public OpenVector3f getOrigin() {
        return origin;
    }

    public OpenVector3f getRotation() {
        return rotation;
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

    public Map<String, BlockBenchMeshFace> getFaces() {
        return faces;
    }

    public Map<String, OpenVector3f> getVertices() {
        return vertices;
    }

    protected static class Builder extends BlockBenchElement.Builder {

        protected boolean boxUV = false;
        protected boolean mirrorUV = false;
        protected boolean allowMirrorModeling = false;

        protected String renderOrder = "default"; // default,behind,in_front

        protected OpenVector2f uvOffset = OpenVector2f.ZERO;

        protected OpenVector3f origin = OpenVector3f.ZERO;
        protected OpenVector3f rotation = OpenVector3f.ZERO;

        protected final Map<String, BlockBenchMeshFace> faces = new LinkedHashMap<>();
        protected final Map<String, OpenVector3f> vertices = new LinkedHashMap<>();

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

        public void origin(OpenVector3f origin) {
            this.origin = origin;
        }

        public void rotation(OpenVector3f rotation) {
            this.rotation = rotation;
        }

        public void uvOffset(OpenVector2f uvOffset) {
            this.uvOffset = uvOffset;
        }

        public void addFace(String key, BlockBenchMeshFace face) {
            this.faces.put(key, face);
        }

        public void addVertex(String key, OpenVector3f vertex) {
            this.vertices.put(key, vertex);
        }

        @Override
        public BlockBenchMesh build() {
            return new BlockBenchMesh(uuid, name, type, allowExport, allowMirrorModeling, renderOrder, boxUV, mirrorUV, uvOffset, origin, rotation, faces, vertices);
        }
    }
}
