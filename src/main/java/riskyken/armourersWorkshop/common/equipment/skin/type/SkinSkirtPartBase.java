package riskyken.armourersWorkshop.common.equipment.skin.type;

import javax.vecmath.Point3i;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.api.common.equipment.skin.IEquipmentSkinType;
import riskyken.armourersWorkshop.client.model.armourer.ModelLegs;
import riskyken.armourersWorkshop.common.Rectangle3D;
import riskyken.armourersWorkshop.common.equipment.skin.EquipmentSkinPartBase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SkinSkirtPartBase extends EquipmentSkinPartBase {

    @SideOnly(Side.CLIENT)
    private static final ModelLegs partModel = new ModelLegs();
    
    private Rectangle3D buildingSpace;
    private Rectangle3D guideSpace;
    private Point3i offset;
    
    public SkinSkirtPartBase(IEquipmentSkinType baseType) {
        super(baseType);
        this.buildingSpace = new Rectangle3D(-10, -12, -10, 20, 15, 20);
        this.guideSpace = new Rectangle3D(-4, -12, -2, 8, 12, 4);
        //Offset -1 to match old skin system.
        this.offset = new Point3i(0, -1, 0);
    }
    
    @Override
    public String getPartName() {
        return "base";
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
    public void renderBuildingGuide(float scale,boolean showSkinOverlay, boolean showHelper) {
        GL11.glTranslated(0, this.buildingSpace.y * scale, 0);
        GL11.glTranslated(0, -this.guideSpace.y * scale, 0);
        GL11.glTranslated(2 * scale, 0, 0);
        partModel.renderLeftLeft(scale);
        GL11.glTranslated(-4 * scale, 0, 0);
        partModel.renderRightLeg(scale);
        GL11.glTranslated(2 * scale, 0, 0);
        GL11.glTranslated(0, this.guideSpace.y * scale, 0);
        GL11.glTranslated(0, -this.buildingSpace.y * scale, 0);
    }
}
