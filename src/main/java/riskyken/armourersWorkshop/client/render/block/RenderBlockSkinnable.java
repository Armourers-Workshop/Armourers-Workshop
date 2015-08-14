package riskyken.armourersWorkshop.client.render.block;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.client.handler.EquipmentRenderHandler;
import riskyken.armourersWorkshop.client.model.ClientModelCache;
import riskyken.armourersWorkshop.client.model.block.ModelBlockSkinnable;
import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.common.tileentities.TileEntitySkinnable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockSkinnable extends TileEntitySpecialRenderer {

    private static final ModelBlockSkinnable loadingModel = new ModelBlockSkinnable();
    
    public void renderTileEntityAt(TileEntitySkinnable tileEntity, double x, double y, double z, float partialTickTime) {
        Minecraft.getMinecraft().mcProfiler.startSection("skinnableBlock");
        SkinPointer skinPointer = tileEntity.getSkinPointer();
        if (skinPointer != null) {
            if (ClientModelCache.INSTANCE.isEquipmentInCache(skinPointer.getSkinId())) {
                renderSkin(tileEntity, x, y, z, skinPointer);
            } else {
                ClientModelCache.INSTANCE.requestEquipmentDataFromServer(skinPointer.getSkinId());
                GL11.glPushMatrix();
                GL11.glTranslated(x + 0.5F, y + 0.5F, z + 0.5F);
                loadingModel.render(tileEntity, partialTickTime, 0.0625F);
                GL11.glPopMatrix();
            }
        }
        Minecraft.getMinecraft().mcProfiler.endSection();
    }
    
    private void renderSkin(TileEntitySkinnable tileEntity, double x, double y, double z, SkinPointer skinPointer) {
        GL11.glPushMatrix();
        ModRenderHelper.enableAlphaBlend();
        int rotation = tileEntity.getBlockMetadata();
        
        GL11.glTranslated(x + 0.5F, y, z + 0.5F);
        
        GL11.glScalef(-1, -1, 1);
        GL11.glRotatef(22.5F * rotation, 0, 1, 0);
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_CULL_FACE);
        EquipmentRenderHandler.INSTANCE.renderSkin(skinPointer);
        GL11.glPopAttrib();
        ModRenderHelper.disableAlphaBlend();
        GL11.glPopMatrix();
    }
    
    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTickTime) {
        renderTileEntityAt((TileEntitySkinnable)tileEntity, x, y, z, partialTickTime);
    }
}
