package riskyken.armourersWorkshop.client.render.tileEntity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import riskyken.armourersWorkshop.client.render.ItemStackRenderHelper;
import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import riskyken.armourersWorkshop.common.config.ConfigHandlerClient;
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
        
        int rot = tileEntity.getBlockMetadata();
        
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_NORMALIZE);

        GL11.glTranslated(x + 0.5F, y + 0.5D, z + 0.5F);
        
        GL11.glRotatef(180, 0, 0, 1);
        
        if (rot == 1) {
            GL11.glRotatef(180, 0, 0, 1);
        }
        if (rot == 2) {
            GL11.glRotatef(90, -1, 0, 0);
        }
        if (rot == 3) {
            GL11.glRotatef(90, 1, 0, 0);
        }
        if (rot == 4) {
            GL11.glRotatef(90, 0, 0, -1);
        }
        if (rot == 5) {
            GL11.glRotatef(90, 0, 0, 1);
        }
        
        float scale = 0.0625F;
        
        
        
        GL11.glTranslated(tileEntity.getOffsetX() * scale, tileEntity.getOffsetY() * scale, tileEntity.getOffsetZ() * scale);
        
        GL11.glScalef(-1, -1, 1);
        
        int speedX = tileEntity.getRotationSpeedX();
        int speedY = tileEntity.getRotationSpeedY();
        int speedZ = tileEntity.getRotationSpeedZ();
        
        float angleX = 0;
        float angleY = 0;
        float angleZ = 0;
        
        if (speedX != 0) {
            angleX = (System.currentTimeMillis() % speedX);
            angleX = angleX / speedX * 360F;
        }
        if (speedY != 0) {
            angleY = (System.currentTimeMillis() % speedY);
            angleY = angleY / speedY * 360F;
        }
        if (speedZ != 0) {
            angleZ = (System.currentTimeMillis() % speedZ);
            angleZ = angleZ / speedZ * 360F;
        }
        

        GL11.glPushMatrix();
        
        GL11.glTranslated(
                (-tileEntity.getRotationOffsetX() + tileEntity.getRotationOffsetX()) * scale,
                (-tileEntity.getRotationOffsetY() + tileEntity.getRotationOffsetY()) * scale,
                (-tileEntity.getRotationOffsetZ() + tileEntity.getRotationOffsetZ()) * scale);
        
        
        if (angleX != 0) {
            GL11.glRotatef((float)angleX, 1, 0, 0);
        }
        if (angleY != 0) {
            GL11.glRotatef((float)angleY, 0, 1, 0);
        }
        if (angleZ != 0) {
            GL11.glRotatef((float)angleZ, 0, 0, 1);
        }
        
        GL11.glTranslated(tileEntity.getRotationOffsetX() * scale, tileEntity.getRotationOffsetY() * scale, tileEntity.getRotationOffsetZ() * scale);
        
        
        ModRenderHelper.disableLighting();
        ModRenderHelper.enableAlphaBlend();
        
        
        ItemStackRenderHelper.renderSkinWithoutHelper(skinPointer, true);
        
        GL11.glPopMatrix();
        if (tileEntity.isShowRotationPoint()) {
            AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(0, 0, 0, scale, scale, scale);
            renderBox(aabb, 1F, 0F, 1F);
        }
        //ItemStackRenderHelper.renderItemModelFromSkinPointer(skinPointer, true, true);
        
        ModRenderHelper.disableAlphaBlend();
        ModRenderHelper.enableLighting();
        GL11.glDisable(GL11.GL_NORMALIZE);
        GL11.glPopMatrix();
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTickTime) {
        renderTileEntityAt((TileEntityHologramProjector)tileEntity, x, y, z, partialTickTime);
        if (ConfigHandlerClient.showSkinRenderBounds) {
            if (tileEntity != null && !(tileEntity instanceof TileEntityHologramProjector)) {
                return;
            }
            AxisAlignedBB aabb = tileEntity.getRenderBoundingBox().copy();
            aabb.offset(x - tileEntity.xCoord, y - tileEntity.yCoord, z - tileEntity.zCoord);
            renderBox(aabb, 1.0F, 1.0F, 0.0F);
        }
    }
    
    private void renderBox(AxisAlignedBB aabb, float r, float g, float b) {
        float f1 = 0.002F;
        ModRenderHelper.disableLighting();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LIGHTING);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(r, g, b, 0.4F);
        GL11.glLineWidth(1.0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        
        
        RenderGlobal.drawOutlinedBoundingBox(aabb.contract(f1, f1, f1), -1);
        
        
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        ModRenderHelper.enableLighting();
    }
}
