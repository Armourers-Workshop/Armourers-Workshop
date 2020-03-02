package moe.plushie.armourers_workshop.common.skin.type.feet;

import java.awt.Point;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.IPoint3D;
import moe.plushie.armourers_workshop.api.common.IRectangle3D;
import moe.plushie.armourers_workshop.api.common.skin.Point3D;
import moe.plushie.armourers_workshop.api.common.skin.Rectangle3D;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinProperties;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartTypeTextured;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.model.armourer.ModelLegs;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.type.AbstractSkinPartTypeBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SkinFeetPartLeftFoot extends AbstractSkinPartTypeBase implements ISkinPartTypeTextured  {
    
    public SkinFeetPartLeftFoot(ISkinType baseType) {
        super(baseType);
        this.buildingSpace = new Rectangle3D(-8, -13, -8, 11, 5, 16);
        this.guideSpace = new Rectangle3D(-2, -12, -2, 4, 12, 4);
        this.offset = new Point3D(6, 0, 0);
    }
    
    @Override
    public String getPartName() {
        return "leftFoot";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderBuildingGuide(float scale, ISkinProperties skinProps, boolean showHelper) {
        GL11.glTranslated(0, this.buildingSpace.getY() * scale, 0);
        GL11.glTranslated(0, -this.guideSpace.getY() * scale, 0);
        ModelLegs.MODEL.renderLeftLeft(scale);
        GL11.glTranslated(0, this.guideSpace.getY() * scale, 0);
        GL11.glTranslated(0, -this.buildingSpace.getY() * scale, 0);
    }

    @Override
    public Point getTextureSkinPos() {
        return new Point(0, 16);
    }

    @Override
    public boolean isTextureMirrored() {
        return true;
    }
    
    @Override
    public Point getTextureBasePos() {
        return new Point(16, 48);
    }
    
    @Override
    public Point getTextureOverlayPos() {
        return new Point(0, 48);
    }
    
    @Override
    public IPoint3D getTextureModelSize() {
        return new Point3D(4, 12, 4);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public IPoint3D getItemRenderOffset() {
        return new Point3D(2, 12, 0);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public IRectangle3D getItemRenderTextureBounds() {
        return new Rectangle3D(0, 12, -2, 4, 12, 4);
    }
    
    @Override
    public boolean isModelOverridden(ISkinProperties skinProps) {
        return SkinProperties.PROP_MODEL_OVERRIDE_LEG_LEFT.getValue(skinProps);
    }
    
    @Override
    public boolean isOverlayOverridden(ISkinProperties skinProps) {
        return SkinProperties.PROP_MODEL_HIDE_OVERLAY_LEG_LEFT.getValue(skinProps);
    }
}
