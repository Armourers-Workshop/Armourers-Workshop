package moe.plushie.armourers_workshop.common.skin.type.chest;

import java.awt.Point;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.IPoint3D;
import moe.plushie.armourers_workshop.api.common.IRectangle3D;
import moe.plushie.armourers_workshop.api.common.skin.Point3D;
import moe.plushie.armourers_workshop.api.common.skin.Rectangle3D;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinProperties;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartTypeTextured;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.model.armourer.ModelChest;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.type.AbstractSkinPartTypeBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SkinChestPartBase extends AbstractSkinPartTypeBase implements ISkinPartTypeTextured {

    public SkinChestPartBase(ISkinType baseType) {
        super(baseType);
        this.buildingSpace = new Rectangle3D(-6, -24, -30, 12, 38, 60);
        this.guideSpace = new Rectangle3D(-4, -12, -2, 8, 12, 4);
        this.offset = new Point3D(0, -1, 0);
    }

    @Override
    public String getPartName() {
        return "base";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderBuildingGuide(float scale, ISkinProperties skinProps, boolean showHelper) {
        GL11.glTranslated(0, this.buildingSpace.getY() * scale, 0);
        GL11.glTranslated(0, -this.guideSpace.getY() * scale, 0);
        ModelChest.MODEL.renderChest(scale);
        GL11.glTranslated(0, this.guideSpace.getY() * scale, 0);
        GL11.glTranslated(0, -this.buildingSpace.getY() * scale, 0);
    }

    @Override
    public Point getTextureSkinPos() {
        return new Point(16, 16);
    }

    @Override
    public boolean isTextureMirrored() {
        return false;
    }

    @Override
    public Point getTextureBasePos() {
        return new Point(16, 16);
    }

    @Override
    public Point getTextureOverlayPos() {
        return new Point(16, 32);
    }

    @Override
    public IPoint3D getTextureModelSize() {
        return new Point3D(8, 12, 4);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IPoint3D getItemRenderOffset() {
        return new Point3D(0, 0, 0);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IRectangle3D getItemRenderTextureBounds() {
        return new Rectangle3D(-4, 0, -2, 8, 12, 4);
    }

    @Override
    public boolean isModelOverridden(ISkinProperties skinProps) {
        return SkinProperties.PROP_MODEL_OVERRIDE_CHEST.getValue(skinProps);
    }

    @Override
    public boolean isOverlayOverridden(ISkinProperties skinProps) {
        return SkinProperties.PROP_MODEL_HIDE_OVERLAY_CHEST.getValue(skinProps);
    }
}
