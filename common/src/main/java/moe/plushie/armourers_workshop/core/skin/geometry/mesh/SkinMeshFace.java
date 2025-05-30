package moe.plushie.armourers_workshop.core.skin.geometry.mesh;

import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryFace;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryOptions;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryType;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryVertex;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTexturePos;

import java.util.List;

public class SkinMeshFace extends SkinGeometryFace {

    protected SkinGeometryType type;
    protected SkinGeometryOptions options;
    protected List<SkinGeometryVertex> vertices;

    public SkinMeshFace(int id, SkinGeometryType type, SkinGeometryOptions options, OpenTransform3f transform, SkinTexturePos texturePos, List<SkinGeometryVertex> vertices) {
        this.id = id;
        this.type = type;
        this.options = options;
        this.transform = transform;
        this.texturePos = texturePos;
        this.vertices = vertices;
    }

    @Override
    public SkinGeometryType getType() {
        return type;
    }

    @Override
    public SkinGeometryOptions getOptions() {
        return options;
    }

    @Override
    public float getPriority() {
        var priority = 0;
        return switch (options.getRenderOrder()) {
            case 1 -> priority - 1000; // behind
            case 2 -> priority + 1000; // in_front
            default -> priority;
        };
    }

    @Override
    public List<SkinGeometryVertex> getVertices() {
        return vertices;
    }
}
