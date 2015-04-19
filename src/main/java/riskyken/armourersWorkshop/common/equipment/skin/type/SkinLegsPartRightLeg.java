package riskyken.armourersWorkshop.common.equipment.skin.type;

import javax.vecmath.Point3i;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.api.common.equipment.skin.ISkinType;
import riskyken.armourersWorkshop.client.model.armourer.ModelLegs;
import riskyken.armourersWorkshop.common.Rectangle3D;
import riskyken.armourersWorkshop.common.equipment.skin.SkinTypePartBase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SkinLegsPartRightLeg extends SkinTypePartBase {

    @SideOnly(Side.CLIENT)
    private static final ModelLegs partModel = new ModelLegs();
    
    private Rectangle3D buildingSpace;
    private Rectangle3D guideSpace;
    private Point3i offset;
    
    public SkinLegsPartRightLeg(ISkinType baseType) {
        super(baseType);
        this.buildingSpace = new Rectangle3D(-3, -8, -4, 7, 9, 8);
        this.guideSpace = new Rectangle3D(-2, -12, -2, 4, 12, 4);
        this.offset = new Point3i(-6, -5, 0);
    }
    
    @Override
    public String getPartName() {
        return "rightLeg";
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
        partModel.renderRightLeg(scale);
        GL11.glTranslated(0, this.guideSpace.y * scale, 0);
        GL11.glTranslated(0, -this.buildingSpace.y * scale, 0);
    }
}
