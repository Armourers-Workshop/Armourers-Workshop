package riskyken.armourersWorkshop.common.equipment.skin.type;

import javax.vecmath.Point3i;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.api.common.equipment.skin.ISkinType;
import riskyken.armourersWorkshop.client.model.armourer.ModelChest;
import riskyken.armourersWorkshop.common.Rectangle3D;
import riskyken.armourersWorkshop.common.equipment.skin.SkinTypePartBase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SkinChestPartLeftArm extends SkinTypePartBase {
    
    @SideOnly(Side.CLIENT)
    private static final ModelChest partModel = new ModelChest();
    
    private Rectangle3D buildingSpace;
    private Rectangle3D guideSpace;
    private Point3i offset;
    
    public SkinChestPartLeftArm(ISkinType baseType) {
        super(baseType);
        this.buildingSpace = new Rectangle3D(-6, -11, -4, 9, 17, 8);
        this.guideSpace = new Rectangle3D(-3, -10, -2, 4, 12, 4);
        this.offset = new Point3i(10, 0, 0);
    }
    
    @Override
    public String getPartName() {
        return "leftArm";
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
        partModel.renderLeftArm(scale);
        GL11.glTranslated(0, this.guideSpace.y * scale, 0);
        GL11.glTranslated(0, -this.buildingSpace.y * scale, 0);
    }
}
