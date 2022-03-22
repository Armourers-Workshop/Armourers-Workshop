package moe.plushie.armourers_workshop.core.skin.part.head;

import moe.plushie.armourers_workshop.core.api.common.skin.ISkinPartTypeTextured;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinProperties;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.utils.Rectangle3i;
import net.minecraft.util.math.vector.Vector3i;

import java.awt.*;

public class HeadPartType extends SkinPartType implements ISkinPartTypeTextured {

    public HeadPartType() {
        super();
        this.buildingSpace = new Rectangle3i(-32, -24, -32, 64, 56, 64);
        this.guideSpace = new Rectangle3i(-4, 0, -4, 8, 8, 8);
        this.offset = new Vector3i(0, 0, 0);
    }

    @Override
    public void renderBuildingGuide(float scale, ISkinProperties skinProps, boolean showHelper) {
//        GL11.glTranslated(0, this.buildingSpace.getY() * scale, 0);
//        GL11.glTranslated(0, -this.guideSpace.getY() * scale, 0);
//        ModelHead.MODEL.render(scale, !SkinProperty.MODEL_HIDE_OVERLAY_HEAD.getValue(skinProps));
//        GL11.glTranslated(0, this.guideSpace.getY() * scale, 0);
//        GL11.glTranslated(0, -this.buildingSpace.getY() * scale, 0);
    }

    @Override
    public Point getTextureSkinPos() {
        return new Point(0, 0);
    }

    @Override
    public boolean isTextureMirrored() {
        return false;
    }

    @Override
    public Point getTextureBasePos() {
        return new Point(0, 0);
    }

    @Override
    public Point getTextureOverlayPos() {
        return new Point(32, 0);
    }

    @Override
    public Vector3i getTextureModelSize() {
        return new Vector3i(8, 8, 8);
    }

    @Override
    public Vector3i getRenderOffset() {
        return new Vector3i(0, 0, 0);
    }

    @Override
    public Rectangle3i getItemRenderTextureBounds() {
        return new Rectangle3i(-4, -8, -4, 8, 8, 8);
    }

    @Override
    public boolean isModelOverridden(ISkinProperties skinProps) {
        return skinProps.get(SkinProperty.MODEL_OVERRIDE_HEAD);
    }

    @Override
    public boolean isOverlayOverridden(ISkinProperties skinProps) {
        return skinProps.get(SkinProperty.MODEL_HIDE_OVERLAY_HEAD);
    }
}
