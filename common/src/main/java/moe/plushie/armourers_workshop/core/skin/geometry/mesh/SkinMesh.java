package moe.plushie.armourers_workshop.core.skin.geometry.mesh;

import moe.plushie.armourers_workshop.core.math.OpenVoxelShape;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometry;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTexturePos;

import java.util.List;

public abstract class SkinMesh extends SkinGeometry {

    protected SkinTexturePos texturePos;

    public SkinTexturePos texturePos() {
        return texturePos;
    }

    @Override
    public OpenVoxelShape shape() {
        var shape = new OpenVoxelShape();
        faces().forEach(face -> face.vertices().forEach(vertex -> shape.add(vertex.position())));
        return OpenVoxelShape.box(shape.bounds());
    }

    public abstract List<? extends SkinMeshFace> faces();
}
