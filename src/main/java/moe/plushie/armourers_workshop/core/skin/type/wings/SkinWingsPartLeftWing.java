package moe.plushie.armourers_workshop.core.skin.type.wings;

import moe.plushie.armourers_workshop.core.api.client.render.IHasRotation;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinProperties;
import moe.plushie.armourers_workshop.core.skin.type.AbstractSkinPartType;
import moe.plushie.armourers_workshop.core.skin.type.Point3D;
import moe.plushie.armourers_workshop.core.skin.type.Rectangle3D;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SkinWingsPartLeftWing extends AbstractSkinPartType implements IHasRotation {

    public SkinWingsPartLeftWing() {
        super();
        this.buildingSpace = new Rectangle3D(-32, -24, -20, 32, 48, 48);
        this.guideSpace = new Rectangle3D(-4, -12, -4, 8, 12, 4);
        this.offset = new Point3D(0, -1, 2);
    }

    @Override
    public void renderBuildingGuide(float scale, ISkinProperties skinProps, boolean showHelper) {
//        GL11.glTranslated(0, this.buildingSpace.getY() * scale, 0);
//
//        GL11.glTranslated(0, -this.guideSpace.getY() * scale, 0);
//        GL11.glTranslated(0, 0, -2 * scale);
//
//        ModelChest.MODEL.renderChest(scale);
//
//        GL11.glTranslated(0, 0, 2 * scale);
//        GL11.glTranslated(0, this.guideSpace.getY() * scale, 0);
//
//        GL11.glTranslated(0, -this.buildingSpace.getY() * scale, 0);
    }

    @Override
    public Point3D getRenderOffset() {
        return new Point3D(0, 0, 2);
    }

    @Override
    public int getMaximumMarkersNeeded() {
        return 1;
    }

    @Override
    public int getMinimumMarkersNeeded() {
        return 1;
    }

//    @Override
//    public Collection<ISkinProperty<?>> getProperties() {
//        return Arrays.asList(
//                SkinProperty.WINGS_FLYING_SPEED,
//                SkinProperty.WINGS_IDLE_SPEED,
//                SkinProperty.WINGS_MAX_ANGLE,
//                SkinProperty.WINGS_MIN_ANGLE,
//                SkinProperty.WINGS_MOVMENT_TYPE);
//    }
}
