package moe.plushie.armourers_workshop.core.skin.data;

import moe.plushie.armourers_workshop.core.bake.SkinTexture;
import moe.plushie.armourers_workshop.core.utils.CustomVoxelShape;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperties;
import moe.plushie.armourers_workshop.core.utils.Rectangle3i;

import java.util.ArrayList;


public class SkinPaintPart extends SkinPart {

    public SkinPaintPart(SkinTexture texturedModel, Rectangle3i bounds, CustomVoxelShape renderShape) {
        super(texturedModel.getPartType(), new ArrayList<>(), new SkinCubeData());
        this.partBounds = bounds;
        this.renderShape = renderShape;
        this.setProperties(new SkinProperties());
    }
}
