package riskyken.armourersWorkshop.common.equipment.skin.type;

import java.awt.Point;

import javax.vecmath.Point3i;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.api.common.equipment.skin.IEquipmentSkinPartTextured;
import riskyken.armourersWorkshop.api.common.equipment.skin.IEquipmentSkinType;
import riskyken.armourersWorkshop.client.model.armourer.ModelChest;
import riskyken.armourersWorkshop.common.Rectangle3D;
import riskyken.armourersWorkshop.common.equipment.skin.EquipmentSkinPartBase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SkinChestPartBase extends EquipmentSkinPartBase implements IEquipmentSkinPartTextured {

    @SideOnly(Side.CLIENT)
    private static final ModelChest partModel = new ModelChest();
    
    private Rectangle3D buildingSpace;
    private Rectangle3D guideSpace;
    private Point3i offset;
    
    public SkinChestPartBase(IEquipmentSkinType baseType) {
        super(baseType);
        this.buildingSpace = new Rectangle3D(-6, -13, -7, 12, 14, 17);
        this.guideSpace = new Rectangle3D(-4, -12, -2, 8, 12, 4);
        this.offset = new Point3i(0, 0, 0);
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
    public void renderBuildingGuide(float scale, boolean showSkinOverlay, boolean showHelper) {
        GL11.glTranslated(0, this.buildingSpace.y * scale, 0);
        GL11.glTranslated(0, -this.guideSpace.y * scale, 0);
        partModel.renderChest(scale);
        GL11.glTranslated(0, this.guideSpace.y * scale, 0);
        GL11.glTranslated(0, -this.buildingSpace.y * scale, 0);
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
    public Point3i getTextureModelSize() {
        return new Point3i(8, 12, 4);
    }
}
