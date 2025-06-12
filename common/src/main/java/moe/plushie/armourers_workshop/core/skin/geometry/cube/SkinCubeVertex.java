package moe.plushie.armourers_workshop.core.skin.geometry.cube;

import moe.plushie.armourers_workshop.core.math.OpenRectangle3f;
import moe.plushie.armourers_workshop.core.math.OpenVector2f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryVertex;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;

public class SkinCubeVertex extends SkinGeometryVertex {

    private final SkinCubeFace face;

    public SkinCubeVertex(int id, OpenVector3f position, OpenVector3f normal, OpenVector2f textureCoords, Color color, SkinCubeFace face) {
        this.id = id;
        this.face = face;
        this.position = position;
        this.normal = normal;
        this.textureCoords = textureCoords;
        this.color = color;
    }

    public OpenRectangle3f boundingBox() {
        return face.boundingBox();
    }

    public OpenDirection direction() {
        return face.direction();
    }
}
