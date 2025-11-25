package moe.plushie.armourers_workshop.core.skin.part.legs;

import moe.plushie.armourers_workshop.api.skin.part.ISkinPartTypeTextured;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3i;
import moe.plushie.armourers_workshop.core.math.OpenVector2i;
import moe.plushie.armourers_workshop.core.math.OpenVector3i;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;

public class RightLegPartType extends SkinPartType implements ISkinPartTypeTextured {

    public RightLegPartType() {
        super();
        this.buildingSpace = new OpenRectangle3i(-6, -16, -16, 24, 32, 32);
        this.guideSpace = new OpenRectangle3i(-2, -12, -2, 4, 12, 4);
        this.offset = new OpenVector3i(-9, -1, 0);
        this.renderOffset = new OpenVector3i(-2, 12, 0);
        this.renderPolygonOffset = 2;
        this.guideOrigin = new OpenVector3i(-2, 0, -2);
    }

    @Override
    public OpenVector2i texturePos() {
        return new OpenVector2i(0, 16);
    }

    @Override
    public OpenVector3i textureModelSize() {
        return new OpenVector3i(4, 12, 4);
    }
}
