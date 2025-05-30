package moe.plushie.armourers_workshop.core.skin.geometry.cube;

import moe.plushie.armourers_workshop.core.math.OpenRectangle3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3i;
import moe.plushie.armourers_workshop.core.math.OpenVoxelShape;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometry;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryType;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryTypes;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTexturePos;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;

public abstract class SkinCube extends SkinGeometry {

    protected OpenRectangle3f boundingBox = OpenRectangle3f.ZERO;

    protected final EnumMap<OpenDirection, SkinPaintColor> paintColors = new EnumMap<>(OpenDirection.class);

    public void setType(SkinGeometryType type) {
        throw new UnsupportedOperationException();
    }

    public void setBoundingBox(OpenRectangle3f box) {
        throw new UnsupportedOperationException();
    }

    public void setPaintColor(OpenDirection dir, SkinPaintColor paintColor) {
        throw new UnsupportedOperationException();
    }

    public OpenRectangle3f getBoundingBox() {
        return boundingBox;
    }

    public SkinPaintColor getPaintColor(OpenDirection dir) {
        return paintColors.getOrDefault(dir, SkinPaintColor.CLEAR);
    }

    public abstract SkinTexturePos getTexture(OpenDirection dir);

    @Nullable
    public SkinCubeFace getFace(OpenDirection dir) {
        var id = dir.get3DDataValue();
        var texturePos = getTexture(dir);
        var paintColor = getPaintColor(dir);
        var geometryType = getType();
        var alpha = 255;
        if (SkinGeometryTypes.isGlassBlock(geometryType)) {
            alpha = 127;
        }
        var transform = getTransform();
        var boundingBox = getBoundingBox();
        return new SkinCubeFace(id, geometryType, options, transform, texturePos, boundingBox, dir, paintColor, alpha);
    }

    @Override
    public OpenVoxelShape getShape() {
        return OpenVoxelShape.box(getBoundingBox());
    }

    @Override
    public Iterable<SkinCubeFace> getFaces() {
        return Collections.compactMap(OpenDirection.values(), this::getFace);
    }

    public OpenVector3i getBlockPos() {
        var boundingBox = getBoundingBox();
        return new OpenVector3i(boundingBox.x(), boundingBox.y(), boundingBox.z());
    }
}
