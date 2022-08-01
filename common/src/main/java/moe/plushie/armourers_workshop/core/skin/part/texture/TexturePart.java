package moe.plushie.armourers_workshop.core.skin.part.texture;

import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubeData;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.utils.ext.OpenVoxelShape;
import moe.plushie.armourers_workshop.utils.math.Rectangle3i;

import java.util.ArrayList;

public class TexturePart extends SkinPart {

    public TexturePart(ISkinPartType partType, Rectangle3i bounds, OpenVoxelShape renderShape) {
        super(partType, new ArrayList<>(), new SkinCubeData());
        this.partBounds = bounds;
        this.renderShape = renderShape;
        this.setProperties(new SkinProperties());
    }
}
