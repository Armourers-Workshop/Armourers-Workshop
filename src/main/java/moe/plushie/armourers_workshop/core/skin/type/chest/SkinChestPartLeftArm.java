package moe.plushie.armourers_workshop.core.skin.type.chest;

import moe.plushie.armourers_workshop.core.api.common.skin.ISkinPartTypeTextured;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinProperties;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.type.AbstractSkinPartType;
import moe.plushie.armourers_workshop.core.skin.type.Point3D;
import moe.plushie.armourers_workshop.core.skin.type.Rectangle3D;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;

public class SkinChestPartLeftArm extends AbstractSkinPartType implements ISkinPartTypeTextured {

    public SkinChestPartLeftArm() {
        super();
        this.buildingSpace = new Rectangle3D(-11, -16, -14, 14, 32, 28);
        this.guideSpace = new Rectangle3D(-3, -10, -2, 4, 12, 4);
        this.offset = new Point3D(10, -7, 0);
    }

    @Override
    public void renderBuildingGuide(float scale, ISkinProperties skinProps, boolean showHelper) {
//        GL11.glTranslated(0, this.buildingSpace.getY() * scale, 0);
//        GL11.glTranslated(0, -this.guideSpace.getY() * scale, 0);
//        ModelChest.MODEL.renderLeftArm(scale);
//        GL11.glTranslated(0, this.guideSpace.getY() * scale, 0);
//        GL11.glTranslated(0, -this.buildingSpace.getY() * scale, 0);
    }

    @Override
    public Point getTextureSkinPos() {
        return new Point(40, 16);
    }

    @Override
    public boolean isTextureMirrored() {
        return true;
    }

    @Override
    public Point getTextureBasePos() {
        return new Point(32, 48);
    }

    @Override
    public Point getTextureOverlayPos() {
        return new Point(48, 48);
    }

    @Override
    public Point3D getTextureModelSize() {
        return new Point3D(4, 12, 4);
    }

    @Override
    public Point3D getRenderOffset() {
        return new Point3D(5, 2, 0);
    }

    @Override
    public Rectangle3D getItemRenderTextureBounds() {
        return new Rectangle3D(-8, 0, -2, 4, 12, 4);
    }

    @Override
    public boolean isModelOverridden(ISkinProperties skinProps) {
        return skinProps.get(SkinProperty.MODEL_OVERRIDE_ARM_LEFT);
    }

    @Override
    public boolean isOverlayOverridden(ISkinProperties skinProps) {
        return skinProps.get(SkinProperty.MODEL_HIDE_OVERLAY_ARM_LEFT);
    }

//    @Override
//    public ISkinProperty[] getProperties() {
//        return Arrays.asList(
//                SkinProperty.MODEL_OVERRIDE_ARM_LEFT,
//                SkinProperty.MODEL_HIDE_OVERLAY_ARM_LEFT);
//    }
}
