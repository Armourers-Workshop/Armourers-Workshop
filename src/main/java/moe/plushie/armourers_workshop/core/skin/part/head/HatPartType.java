package moe.plushie.armourers_workshop.core.skin.part.head;

import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.utils.Rectangle3i;
import net.minecraft.util.math.vector.Vector3i;

public class HatPartType extends SkinPartType {

    public HatPartType() {
        super();
        this.buildingSpace = new Rectangle3i(-32, -24, -32, 64, 56, 64);
        this.guideSpace = new Rectangle3i(-4, 0, -4, 8, 8, 8);
        this.offset = new Vector3i(0, 0, 0);
    }

    @Override
    public Vector3i getRenderOffset() {
        return new Vector3i(0, 0, 0);
    }

    @Override
    public float getRenderPolygonOffset() {
        return -0.08f;
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
