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

public class SkinWingsPartRightWing extends AbstractSkinPartType implements IHasRotation {

    public SkinWingsPartRightWing() {
        super();
        this.buildingSpace = new Rectangle3D(0, -24, -20, 32, 48, 48);
        this.guideSpace = new Rectangle3D(-4, -12, -4, 8, 12, 4);
        this.offset = new Point3D(0, -1, 2);
    }

    @Override
    public void renderBuildingGuide(float scale, ISkinProperties skinProps, boolean showHelper) {
        //GL11.glTranslated(0, this.buildingSpace.getY() * scale, 0);
        //GL11.glTranslated(0, -this.guideSpace.getY() * scale, 0);
        //ModelChest.MODEL.renderChest(scale);
        //GL11.glTranslated(0, this.guideSpace.getY() * scale, 0);
        //GL11.glTranslated(0, -this.buildingSpace.getY() * scale, 0);
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

}
