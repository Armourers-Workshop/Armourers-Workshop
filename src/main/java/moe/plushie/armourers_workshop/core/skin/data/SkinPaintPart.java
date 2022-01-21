package moe.plushie.armourers_workshop.core.skin.data;

import moe.plushie.armourers_workshop.core.api.ISkinPartType;
import moe.plushie.armourers_workshop.core.render.other.SkinRenderShape;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.utils.Rectangle3f;
import moe.plushie.armourers_workshop.core.utils.Rectangle3i;
import net.minecraft.util.Direction;

import java.awt.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;


public class SkinPaintPart extends SkinPart {

    // u,v,w,h,partType

    public SkinPaintPart(SkinTexturedModel model) {
        super(model.getPartType(), new ArrayList<>(), new SkinCubeData());
        Rectangle3i bounds = model.getBounds();
        SkinRenderShape shape = SkinRenderShape.empty();
        shape.add(new Rectangle3f(bounds));
        this.renderShape = shape;
        this.partBounds = bounds;
    }



}
