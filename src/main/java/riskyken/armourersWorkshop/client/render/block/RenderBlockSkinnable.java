package riskyken.armourersWorkshop.client.render.block;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import riskyken.armourersWorkshop.client.handler.EquipmentRenderHandler;
import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.common.tileentities.TileEntitySkinnable;

@SideOnly(Side.CLIENT)
public class RenderBlockSkinnable extends TileEntitySpecialRenderer {

    public void renderTileEntityAt(TileEntitySkinnable tileEntity, double x, double y, double z, float partialTickTime) {
        Minecraft.getMinecraft().mcProfiler.startSection("skinnableBlock");
        SkinPointer skinPointer = tileEntity.getSkinPointer();
        if (skinPointer != null) {
            GL11.glPushMatrix();
            ModRenderHelper.enableAlphaBlend();
            int rotation = tileEntity.getBlockMetadata();
            
            GL11.glTranslated(x + 0.5F, y, z + 0.5F);
            
            GL11.glScalef(-1, -1, 1);
            GL11.glRotatef(22.5F * rotation, 0, 1, 0);
            GL11.glEnable(GL11.GL_CULL_FACE);
            EquipmentRenderHandler.INSTANCE.renderSkin(skinPointer);
            GL11.glDisable(GL11.GL_CULL_FACE);
            ModRenderHelper.disableAlphaBlend();
            GL11.glPopMatrix();
        }
        Minecraft.getMinecraft().mcProfiler.endSection();
    }
    
    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTickTime) {
        renderTileEntityAt((TileEntitySkinnable)tileEntity, x, y, z, partialTickTime);
    }
}
