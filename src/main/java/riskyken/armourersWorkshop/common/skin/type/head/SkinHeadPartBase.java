package riskyken.armourersWorkshop.common.skin.type.head;

import java.awt.Point;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.api.common.IPoint3D;
import riskyken.armourersWorkshop.api.common.skin.Point3D;
import riskyken.armourersWorkshop.api.common.skin.Rectangle3D;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartTypeTextured;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.model.armourer.ModelHead;
import riskyken.armourersWorkshop.common.skin.type.AbstractSkinPartTypeBase;

public class SkinHeadPartBase extends AbstractSkinPartTypeBase implements ISkinPartTypeTextured {
    
    public SkinHeadPartBase(ISkinType baseType) {
        super(baseType);
        this.buildingSpace = new Rectangle3D(-32, -24, -32, 64, 56, 64);
        this.guideSpace = new Rectangle3D(-4, 0, -4, 8, 8, 8);
        this.offset = new Point3D(0, 0, 0);
    }
    
    @Override
    public String getPartName() {
        return "base";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderBuildingGuide(float scale, boolean showSkinOverlay, boolean showHelper) {
        GL11.glTranslated(0, this.buildingSpace.getY() * scale, 0);
        GL11.glTranslated(0, -this.guideSpace.getY() * scale, 0);
        ModelHead.MODEL.render(scale, showSkinOverlay);
        GL11.glTranslated(0, this.guideSpace.getY() * scale, 0);
        GL11.glTranslated(0, -this.buildingSpace.getY() * scale, 0);
    }

    @Override
    public Point getTextureLocation() {
        return new Point(0, 0);
    }

    @Override
    public boolean isTextureMirrored() {
        return false;
    }
    
    @Override
    public IPoint3D getTextureModelSize() {
        return new Point3D(8, 8, 8);
    }
}
