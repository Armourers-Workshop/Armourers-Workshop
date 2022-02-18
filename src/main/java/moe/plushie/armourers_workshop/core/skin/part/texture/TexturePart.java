package moe.plushie.armourers_workshop.core.skin.part.texture;

import moe.plushie.armourers_workshop.core.render.bake.PlayerTexture;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubeData;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.utils.CustomVoxelShape;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperties;
import moe.plushie.armourers_workshop.core.utils.Rectangle3i;

import java.util.ArrayList;


public class TexturePart extends SkinPart {

    public TexturePart(PlayerTexture texturedModel, Rectangle3i bounds, CustomVoxelShape renderShape) {
        super(texturedModel.getPartType(), new ArrayList<>(), new SkinCubeData());
        this.partBounds = bounds;
        this.renderShape = renderShape;
        this.setProperties(new SkinProperties());
    }
}
