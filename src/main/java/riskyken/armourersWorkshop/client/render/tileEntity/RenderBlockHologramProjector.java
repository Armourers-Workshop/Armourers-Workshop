package riskyken.armourersWorkshop.client.render.tileEntity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import riskyken.armourersWorkshop.client.render.ItemStackRenderHelper;
import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.common.tileentities.TileEntityHologramProjector;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;

@SideOnly(Side.CLIENT)
public class RenderBlockHologramProjector extends TileEntitySpecialRenderer {

    public void renderTileEntityAt(TileEntityHologramProjector tileEntity, double x, double y, double z, float partialTickTime) {
        ItemStack itemStack = tileEntity.getStackInSlot(0);
        SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(itemStack);
        if (skinPointer == null) {
            return;
        }
        
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_NORMALIZE);
        GL11.glTranslated(x + 0.5F, y + 2D, z + 0.5F);
        GL11.glScalef(-1, -1, 1);
        //GL11.glScalef(2, 2, 2);
        
        if (tileEntity != null) {
            float angle = (((tileEntity.getWorldObj().getTotalWorldTime() + tileEntity.hashCode()) % 360) + partialTickTime);
            GL11.glRotatef(angle * 5, 0, 1, 0);
        }
        //GL11.glTranslated(0, 0, -1F);
        
        ModRenderHelper.disableLighting();
        ModRenderHelper.enableAlphaBlend();
        
        ItemStackRenderHelper.renderItemModelFromSkinPointer(skinPointer, true, true);
        
        ModRenderHelper.disableAlphaBlend();
        ModRenderHelper.enableLighting();
        GL11.glDisable(GL11.GL_NORMALIZE);
        GL11.glPopMatrix();
    }
    
    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTickTime) {
        renderTileEntityAt((TileEntityHologramProjector)tileEntity, x, y, z, partialTickTime);
    }
}
