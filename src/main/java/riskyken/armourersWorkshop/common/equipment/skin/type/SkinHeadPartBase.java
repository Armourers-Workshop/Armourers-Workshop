package riskyken.armourersWorkshop.common.equipment.skin.type;

import javax.vecmath.Point3i;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.client.model.armourer.ModelHead;
import riskyken.armourersWorkshop.common.Rectangle3D;
import riskyken.armourersWorkshop.common.equipment.skin.SkinTypePartBase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SkinHeadPartBase extends SkinTypePartBase {

    @SideOnly(Side.CLIENT)
    private static final ModelHead partModel = new ModelHead();
    
    private Rectangle3D buildingSpace;
    private Rectangle3D guideSpace;
    private Point3i offset;
    
    public SkinHeadPartBase() {
        this.buildingSpace = new Rectangle3D(-10, -12, -10, 20, 32, 20);
        this.guideSpace = new Rectangle3D(-4, 0, -4, 8, 8, 8);
        this.offset = new Point3i(0, 0, 0);
    }

    @Override
    public Rectangle3D getBuildingSpace() {
        return this.buildingSpace;
    }

    @Override
    public Rectangle3D getGuideSpace() {
        return this.guideSpace;
    }

    @Override
    public Point3i getOffset() {
        return this.offset;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderBuildingGuide(float scale, boolean showSkinOverlay, boolean showHelper) {
        GL11.glTranslated(0, this.buildingSpace.y * scale, 0);
        GL11.glTranslated(0, -this.guideSpace.y * scale, 0);
        partModel.render(scale, showSkinOverlay);
        GL11.glTranslated(0, this.guideSpace.y * scale, 0);
        GL11.glTranslated(0, -this.buildingSpace.y * scale, 0);
    }
}
