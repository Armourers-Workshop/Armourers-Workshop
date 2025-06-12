package moe.plushie.armourers_workshop.core.skin.part.head;

import moe.plushie.armourers_workshop.api.skin.part.ISkinPartTypeTextured;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3i;
import moe.plushie.armourers_workshop.core.math.OpenVector2i;
import moe.plushie.armourers_workshop.core.math.OpenVector3i;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;

public class HeadPartType extends SkinPartType implements ISkinPartTypeTextured {

    public HeadPartType() {
        super();
        this.buildingSpace = new OpenRectangle3i(-32, -24, -32, 64, 56, 64);
        this.guideSpace = new OpenRectangle3i(-4, 0, -4, 8, 8, 8);
        this.offset = new OpenVector3i(0, 0, 0);
        this.renderOffset = OpenVector3i.ZERO;
        this.renderPolygonOffset = 6;
    }

    @Override
    public OpenVector2i textureSkinPos() {
        return OpenVector2i.ZERO;
    }

    @Override
    public OpenVector3i textureModelSize() {
        return new OpenVector3i(8, 8, 8);
    }
}
