package moe.plushie.armourers_workshop.core.skin.type.chest;

import moe.plushie.armourers_workshop.core.api.common.skin.ISkinPartTypeTextured;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinProperties;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinProperty;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.type.AbstractSkinPartType;
import moe.plushie.armourers_workshop.core.skin.type.Point3D;
import moe.plushie.armourers_workshop.core.skin.type.Rectangle3D;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;
import java.util.Arrays;
import java.util.Collection;

public class SkinChestPartBase extends AbstractSkinPartType implements ISkinPartTypeTextured {

    public SkinChestPartBase() {
        super();
        this.buildingSpace = new Rectangle3D(-6, -24, -30, 12, 38, 60);
        this.guideSpace = new Rectangle3D(-4, -12, -2, 8, 12, 4);
        this.offset = new Point3D(0, -1, 0);
    }

    @Override
    public void renderBuildingGuide(float scale, ISkinProperties skinProps, boolean showHelper) {
//        GL11.glTranslated(0, this.buildingSpace.getY() * scale, 0);
//        GL11.glTranslated(0, -this.guideSpace.getY() * scale, 0);
//        ModelChest.MODEL.renderChest(scale);
//        GL11.glTranslated(0, this.guideSpace.getY() * scale, 0);
//        GL11.glTranslated(0, -this.buildingSpace.getY() * scale, 0);
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
    public Point3D getTextureModelSize() {
        return new Point3D(8, 12, 4);
    }

    @Override
    public Point3D getRenderOffset() {
        return new Point3D(0, 0, 0);
    }

    @Override
    public Rectangle3D getItemRenderTextureBounds() {
        return new Rectangle3D(-4, 0, -2, 8, 12, 4);
    }

    @Override
    public boolean isModelOverridden(ISkinProperties skinProps) {
        return skinProps.get(SkinProperty.MODEL_OVERRIDE_CHEST);
    }

    @Override
    public boolean isOverlayOverridden(ISkinProperties skinProps) {
        return skinProps.get(SkinProperty.MODEL_HIDE_OVERLAY_CHEST);
    }

    @Override
    public Collection<ISkinProperty<?>> getProperties() {
        return Arrays.asList(
                SkinProperty.MODEL_OVERRIDE_CHEST,
                SkinProperty.MODEL_HIDE_OVERLAY_CHEST
        );
    }
}
