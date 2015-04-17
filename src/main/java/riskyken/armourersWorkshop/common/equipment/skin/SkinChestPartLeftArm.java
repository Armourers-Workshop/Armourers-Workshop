package riskyken.armourersWorkshop.common.equipment.skin;

import javax.vecmath.Point3i;

import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.client.model.armourer.ModelChest;
import riskyken.armourersWorkshop.client.render.block.RenderBlockMiniArmourer;
import riskyken.armourersWorkshop.common.Rectangle3D;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SkinChestPartLeftArm implements ISkinPart {
    
    @SideOnly(Side.CLIENT)
    private static final ModelChest partModel = new ModelChest();
    
    private Rectangle3D buildingSpace;
    private Rectangle3D guideSpace;
    private Point3i offset;
    private Point3i origin;
    
    public SkinChestPartLeftArm() {
        this.buildingSpace = new Rectangle3D(-5, -11, -4, 9, 17, 8);
        this.guideSpace = new Rectangle3D(-2, -12, -2, 4, 12, 4);
        this.offset = new Point3i(11, 0, 0);
        this.origin = new Point3i(0, 10, 0);
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

    @Override
    public Point3i getOrigin() {
        return this.origin;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderBuildingGuide(float scale, boolean showSkinOverlay, boolean showHelper) {
        GL11.glTranslated(offset.x * scale, offset.y * scale, offset.z * scale);
        GL11.glTranslated(0, this.buildingSpace.y * scale, 0);
        GL11.glTranslated(origin.x * scale, origin.y * scale, origin.z * scale);
        partModel.renderLeftArm(scale);
        GL11.glTranslated(-origin.x * scale, -origin.y * scale, -origin.z * scale);
        GL11.glTranslated(0, -this.buildingSpace.y * scale, 0);
        GL11.glTranslated(-offset.x * scale, -offset.y * scale, -offset.z * scale);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void renderBuildingGrid(float scale) {
        GL11.glTranslated(offset.x * scale, offset.y * scale, offset.z * scale);
        GL11.glTranslated(0, this.buildingSpace.y * scale, 0);
        GL11.glScalef(-1, -1, 1);
        RenderBlockMiniArmourer.renderGuidePart(this, scale);
        GL11.glScalef(-1, -1, 1);
        GL11.glTranslated(0, -this.buildingSpace.y * scale, 0);
        GL11.glTranslated(-offset.x * scale, -offset.y * scale, -offset.z * scale);
    }
    
    @Override
    public void removeBoundingBoxesForPart(World world, int x, int y, int z) {
        // TODO Auto-generated method stub
    }

    @Override
    public void createBoundingBoxesForPart(World world, int x, int y, int z) {
        // TODO Auto-generated method stub
    }
}
