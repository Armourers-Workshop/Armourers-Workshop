package moe.plushie.armourers_workshop.common.skin.type.bow;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.skin.Point3D;
import moe.plushie.armourers_workshop.api.common.skin.Rectangle3D;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinProperties;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.model.armourer.ModelHand;
import moe.plushie.armourers_workshop.common.skin.type.AbstractSkinPartTypeBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SkinBowPartFrame2 extends AbstractSkinPartTypeBase {
    
    public SkinBowPartFrame2(ISkinType baseType) {
        super(baseType);
        this.buildingSpace = new Rectangle3D(-10, -20, -46, 20, 62, 64);
        this.guideSpace = new Rectangle3D(-2, -2, 2, 4, 4, 8);
        this.offset = new Point3D(21, 0, 0);
    }

    @Override
    public String getPartName() {
        return "frame3";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderBuildingGuide(float scale, ISkinProperties skinProps, boolean showHelper) {
        GL11.glTranslated(0, this.buildingSpace.getY() * scale, 0);
        GL11.glTranslated(0, -this.guideSpace.getY() * scale, 0);
        ModelHand.MODEL.render(scale);
        GL11.glTranslated(0, this.guideSpace.getY() * scale, 0);
        GL11.glTranslated(0, -this.buildingSpace.getY() * scale, 0);
    }
    
    @Override
    public int getMinimumMarkersNeeded() {
        return 1;
    }
    
    @Override
    public int getMaximumMarkersNeeded() {
        return 1;
    }
    
    @Override
    public boolean isPartRequired() {
        return true;
    }
}
