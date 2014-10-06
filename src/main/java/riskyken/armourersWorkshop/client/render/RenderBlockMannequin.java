package riskyken.armourersWorkshop.client.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.client.model.ModelMannequin;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockMannequin extends TileEntitySpecialRenderer {
    
    private final ModelMannequin modelMannequin;
    
    public RenderBlockMannequin(ModelMannequin modelMannequin) {
        this.modelMannequin = modelMannequin;
    }
    
    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float tickTime) {
        GL11.glPushMatrix();
        float scale = 0.0625F;
        
        int rotaion = ((TileEntityMannequin)tileEntity).getRotation();
        
        GL11.glTranslated(x + 0.5D, y + 1.5D, z + 0.5D);
        GL11.glScalef(-1, -1, 1);
        GL11.glRotatef(rotaion * 22.5F, 0, 1, 0);
        modelMannequin.render(null, 0, 0.0001F, 0, 0, 0, scale);
        if (tileEntity instanceof TileEntityMannequin) {
            EquipmentPlayerRenderCache.INSTANCE.renderMannequinEquipment(((TileEntityMannequin)tileEntity), modelMannequin);
        }
        GL11.glPopMatrix();
    }
}
