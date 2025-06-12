package moe.plushie.armourers_workshop.core.skin.part.chest;

import moe.plushie.armourers_workshop.api.skin.part.ISkinPartTypeTextured;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3i;
import moe.plushie.armourers_workshop.core.math.OpenVector2i;
import moe.plushie.armourers_workshop.core.math.OpenVector3i;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;

public class ChestPartType extends SkinPartType implements ISkinPartTypeTextured {

    public ChestPartType() {
        super();
        this.buildingSpace = new OpenRectangle3i(-24, -30, -32, 48, 44, 64);
        this.guideSpace = new OpenRectangle3i(-4, -12, -2, 8, 12, 4);
        this.offset = new OpenVector3i(0, -1, 0);
        this.renderOffset = OpenVector3i.ZERO;
        this.renderPolygonOffset = 1;
    }

    @Override
    public OpenVector2i textureSkinPos() {
        return new OpenVector2i(16, 16);
    }

    @Override
    public OpenVector3i textureModelSize() {
        return new OpenVector3i(8, 12, 4);
    }
}
