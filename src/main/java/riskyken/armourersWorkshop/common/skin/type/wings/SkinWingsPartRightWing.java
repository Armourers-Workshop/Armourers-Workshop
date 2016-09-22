package riskyken.armourersWorkshop.common.skin.type.wings;

import java.awt.Point;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.api.common.IPoint3D;
import riskyken.armourersWorkshop.api.common.IRectangle3D;
import riskyken.armourersWorkshop.api.common.skin.Point3D;
import riskyken.armourersWorkshop.api.common.skin.Rectangle3D;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartTypeTextured;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.skin.type.AbstractSkinPartTypeBase;

public class SkinWingsPartRightWing extends AbstractSkinPartTypeBase implements ISkinPartTypeTextured {

    public SkinWingsPartRightWing(ISkinType baseType) {
        super(baseType);
        this.buildingSpace = new Rectangle3D(0, -24, 0, 32, 32, 8);
        this.guideSpace = new Rectangle3D(-4, -12, -4, 8, 12, 4);
        this.offset = new Point3D(0, -1, 0);
    }
    
    @Override
    public IRectangle3D getBuildingSpace() {
        this.buildingSpace = new Rectangle3D(0, -24, 0, 32, 32, 8);
        return super.getBuildingSpace();
    }
    
    @Override
    public IRectangle3D getGuideSpace() {
        this.guideSpace = new Rectangle3D(-4, -12, -4, 8, 12, 4);
        return super.getGuideSpace();
    }
    
    @Override
    public IPoint3D getOffset() {
        this.offset = new Point3D(0, -1, 0);
        return super.getOffset();
    }
    
    @Override
    public String getPartName() {
        return "rightWing";
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void renderBuildingGuide(float scale, boolean showSkinOverlay, boolean showHelper) {
        GL11.glTranslated(0, this.buildingSpace.getY() * scale, 0);
        GL11.glTranslated(0, -this.guideSpace.getY() * scale, 0);
        //ModelChest.MODEL.renderChest(scale);
        GL11.glTranslated(0, this.guideSpace.getY() * scale, 0);
        GL11.glTranslated(0, -this.buildingSpace.getY() * scale, 0);
    }
    
    @Override
    public Point getTextureLocation() {
        return new Point(16, 16);
    }

    @Override
    public boolean isTextureMirrored() {
        return false;
    }

    @Override
    public IPoint3D getTextureModelSize() {
        return new Point3D(8, 12, 4);
    }
}
