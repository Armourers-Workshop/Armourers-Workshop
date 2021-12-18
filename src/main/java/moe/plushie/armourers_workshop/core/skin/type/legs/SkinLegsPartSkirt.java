package moe.plushie.armourers_workshop.core.skin.type.legs;

import moe.plushie.armourers_workshop.core.api.common.skin.ISkinProperties;
import moe.plushie.armourers_workshop.core.skin.type.AbstractSkinPartType;
import moe.plushie.armourers_workshop.core.skin.type.Point3D;
import moe.plushie.armourers_workshop.core.skin.type.Rectangle3D;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SkinLegsPartSkirt extends AbstractSkinPartType {

    public SkinLegsPartSkirt() {
        super();
        this.buildingSpace = new Rectangle3D(-10, -12, -10, 20, 15, 20);
        this.guideSpace = new Rectangle3D(-4, -12, -2, 8, 12, 4);
        // offset -1 to match old skin system.
        this.offset = new Point3D(0, -1, 20);
    }

    @Override
    public void renderBuildingGuide(float scale, ISkinProperties skinProps, boolean showHelper) {
//        GL11.glTranslated(0, this.buildingSpace.getY() * scale, 0);
//        GL11.glTranslated(0, -this.guideSpace.getY() * scale, 0);
//        GL11.glTranslated(2 * scale, 0, 0);
//        ModelLegs.MODEL.renderLeftLeft(scale);
//        GL11.glTranslated(-4 * scale, 0, 0);
//        ModelLegs.MODEL.renderRightLeg(scale);
//        GL11.glTranslated(2 * scale, 0, 0);
//        GL11.glTranslated(0, this.guideSpace.getY() * scale, 0);
//        GL11.glTranslated(0, -this.buildingSpace.getY() * scale, 0);
    }

    @Override
    public Point3D getRenderOffset() {
        return new Point3D(0, 12, 0);
    }


//    @Override
//    public ISkinProperty[] getProperties() {
//        return Arrays.asList(SkinProperty.MODEL_LEGS_LIMIT_LIMBS);
//    }
}
