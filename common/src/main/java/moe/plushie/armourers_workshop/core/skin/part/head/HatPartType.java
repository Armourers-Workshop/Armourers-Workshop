package moe.plushie.armourers_workshop.core.skin.part.head;

import moe.plushie.armourers_workshop.core.math.OpenRectangle3i;
import moe.plushie.armourers_workshop.core.math.OpenVector3i;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;

public class HatPartType extends SkinPartType {

    public HatPartType() {
        super();
        this.buildingSpace = new OpenRectangle3i(-32, -24, -32, 64, 56, 64);
        this.guideSpace = new OpenRectangle3i(-4, 0, -4, 8, 8, 8);
        this.offset = new OpenVector3i(0, 0, 0);
        this.renderOffset = OpenVector3i.ZERO;
        this.renderPolygonOffset = 8;
        this.guideOrigin = new OpenVector3i(-4, -8, -4);
    }

    //    @Override
//    public boolean isModelOverridden(ISkinProperties skinProps) {
//        return skinProps.get(SkinProperty.MODEL_OVERRIDE_HEAD);
//    }
//
//    @Override
//    public boolean isOverlayOverridden(ISkinProperties skinProps) {
//        return skinProps.get(SkinProperty.MODEL_HIDE_OVERLAY_HEAD);
//    }
//
//    @Override
//    public ISkinProperty[] getProperties() {
//        return Arrays.asList(
//                SkinProperty.MODEL_OVERRIDE_HEAD,
//                SkinProperty.MODEL_HIDE_OVERLAY_HEAD);
//    }
}
