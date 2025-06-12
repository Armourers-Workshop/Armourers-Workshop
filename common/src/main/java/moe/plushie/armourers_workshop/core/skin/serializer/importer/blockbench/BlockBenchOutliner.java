package moe.plushie.armourers_workshop.core.skin.serializer.importer.blockbench;

import moe.plushie.armourers_workshop.core.math.OpenVector3f;

import java.util.ArrayList;
import java.util.List;

public class BlockBenchOutliner extends BlockBenchObject {

    private final boolean allowExport;

    private final OpenVector3f origin;
    private final OpenVector3f rotation;

    private final List<Object> children;

    public BlockBenchOutliner(String uuid, String name, OpenVector3f origin, OpenVector3f rotation, boolean allowExport, List<Object> children) {
        super(uuid, name);
        this.origin = origin;
        this.rotation = rotation;
        this.allowExport = allowExport;
        this.children = children;
    }

    public boolean allowExport() {
        return allowExport;
    }

    public OpenVector3f origin() {
        return origin;
    }

    public OpenVector3f rotation() {
        return rotation;
    }

    public List<Object> children() {
        return children;
    }

    protected static class Builder extends BlockBenchObject.Builder {

        private boolean allowExport = true;

        private OpenVector3f origin = OpenVector3f.ZERO;
        private OpenVector3f rotation = OpenVector3f.ZERO;

        private final List<Object> children = new ArrayList<>();

        public void origin(OpenVector3f origin) {
            this.origin = origin;
        }

        public void rotation(OpenVector3f rotation) {
            this.rotation = rotation;
        }

        public void export(boolean allowExport) {
            this.allowExport = allowExport;
        }

        public void addChild(Object obj) {
            this.children.add(obj);
        }

        public BlockBenchOutliner build() {
            return new BlockBenchOutliner(uuid, name, origin, rotation, allowExport, children);
        }
    }
}
