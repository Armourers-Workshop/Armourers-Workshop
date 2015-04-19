package riskyken.armourersWorkshop.common.equipment.skin;

import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.api.common.equipment.skin.ISkinPart;
import riskyken.armourersWorkshop.api.common.equipment.skin.ISkinType;
import riskyken.armourersWorkshop.client.render.block.RenderBlockMiniArmourer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class SkinTypePartBase implements ISkinPart {

    private ISkinType baseType;
    
    public SkinTypePartBase(ISkinType baseType) {
        this.baseType = baseType;
    }
    
    @Override
    public String getRegistryName() {
        return baseType.getRegistryName() + "." + getPartName();
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void renderBuildingGrid(float scale) {
        GL11.glTranslated(0, this.getBuildingSpace().y * scale, 0);
        GL11.glScalef(-1, -1, 1);
        RenderBlockMiniArmourer.renderGuidePart(this, scale);
        GL11.glScalef(-1, -1, 1);
        GL11.glTranslated(0, -this.getBuildingSpace().y * scale, 0);
    }
    
    @Override
    public void createBoundingBoxesForPart(World world, int x, int y, int z) {
        // TODO Auto-generated method stub
    }
    
    @Override
    public void removeBoundingBoxesForPart(World world, int x, int y, int z) {
        // TODO Auto-generated method stub
    }
}
