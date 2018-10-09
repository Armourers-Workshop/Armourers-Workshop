package moe.plushie.armourers_workshop.client.render.tileEntity;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.client.model.block.ModelBlockSkinnable;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.common.tileentities.TileEntitySkinnable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockSkinnable extends TileEntitySpecialRenderer {

    private static final ModelBlockSkinnable loadingModel = new ModelBlockSkinnable();
    private ArrayList<RenderLast> renderList;
    
    public RenderBlockSkinnable() {
        MinecraftForge.EVENT_BUS.register(this);
        renderList = new ArrayList<RenderLast>();
    }
    
    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        /*
        Minecraft mc = Minecraft.getMinecraft();
        mc.profiler.startSection("renderListSort");
        Collections.sort(renderList);
        mc.profiler.endSection();
        mc.entityRenderer.enableLightmap();
        RenderHelper.enableStandardItemLighting();
        ModRenderHelper.enableAlphaBlend();
        Minecraft.getMinecraft().profiler.startSection("skinnableBlock");
        for (int i = 0; i < renderList.size(); i++) {
            RenderLast rl = renderList.get(i);
            renderTileEntityAt((TileEntitySkinnable)rl.tileEntity, rl.x, rl.y, rl.z, event.getPartialTicks());
        }
        Minecraft.getMinecraft().profiler.endSection();
        RenderHelper.disableStandardItemLighting();
        renderList.clear();
        ModRenderHelper.disableAlphaBlend();
        mc.entityRenderer.disableLightmap();
        */
    }
    
    public void renderTileEntityAt(TileEntitySkinnable tileEntity, double x, double y, double z, float partialTickTime) {
        //ModRenderHelper.setLightingForBlock(tileEntity.getWorld(), tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
        SkinDescriptor skinPointer = tileEntity.getSkinPointer();
        if (skinPointer != null) {
            Skin skin = ClientSkinCache.INSTANCE.getSkin(skinPointer);
            if (skin != null) {
                //renderSkin(tileEntity, x, y, z, skin);
            } else {
                ClientSkinCache.INSTANCE.requestSkinFromServer(skinPointer);
                GL11.glPushMatrix();
                GL11.glTranslated(x + 0.5F, y + 0.5F, z + 0.5F);
                loadingModel.render(tileEntity, partialTickTime, 0.0625F);
                GL11.glPopMatrix();
            }
        }
    }
    /*
    private void renderSkin(TileEntitySkinnable tileEntity, double x, double y, double z, Skin skin) {
        int rotation = tileEntity.getBlockMetadata();
        
        double distance = Minecraft.getMinecraft().player.getDistance(
                tileEntity.xCoord + 0.5F,
                tileEntity.yCoord + 0.5F,
                tileEntity.zCoord + 0.5F);
        
        if (ConfigHandlerClient.showLodLevels) {
            int colour = 0x00FF00;
            int lod = MathHelper.floor_double(distance / ConfigHandlerClient.lodDistance);
            lod = MathHelper.clamp_int(lod, 0, ConfigHandlerClient.maxLodLevels);
            if (lod == 1) {
                colour = 0xFFFF00;
            } else if (lod == 2) {
                colour = 0xFF0000;
            }
            else if (lod == 3) {
                colour = 0xFF00FF;
            }
            AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glColor4f(1F, 1F, 1F, 1F);
            GL11.glLineWidth(1.0F);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            RenderGlobal.drawOutlinedBoundingBox(aabb, colour);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glColor4f(1F, 1F, 1F, 1F);
        }
        
        GL11.glTranslated(x + 0.5F, y + 0.5F, z + 0.5F);
        GL11.glScalef(-1, -1, 1);
        
        if (rotation != 0) {
            GL11.glRotatef((90F * rotation), 0, 1, 0);
        }

        
        
        for (int i = 0; i < skin.getParts().size(); i++) {
            SkinPart skinPart = skin.getParts().get(i);
            SkinPartRenderer.INSTANCE.renderPart(skinPart, 0.0625F, tileEntity.getSkinPointer().getSkinDye(), null, distance, true);
        }
        
        
        if (rotation != 0) {
            GL11.glRotatef((90F * -rotation), 0, 1, 0);
        }
        
        GL11.glScalef(-1, -1, 1);
        GL11.glTranslated(-x - 0.5F, -y - 0.5F, -z - 0.5F);
        
    }
    
    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTickTime) {
        if (!(tileEntity instanceof TileEntitySkinnableChild)) {
            renderList.add(new RenderLast(tileEntity, x, y, z));
        }
        if (ConfigHandlerClient.showSkinBlockBounds) {
            if (!(tileEntity.getBlockType() instanceof BlockSkinnable)) {
                return;
            }
            
            BlockSkinnable block = (BlockSkinnable) tileEntity.getBlockType();
            block.setBlockBoundsBasedOnState(tileEntity.getWorldObj(), tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
            
            
            double minX = block.getBlockBoundsMinX();
            double minY = block.getBlockBoundsMinY();
            double minZ = block.getBlockBoundsMinZ();
            double maxX = block.getBlockBoundsMaxX();
            double maxY = block.getBlockBoundsMaxY();
            double maxZ = block.getBlockBoundsMaxZ();
            
            float f1 = 0.002F;
            
            AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
            aabb.offset(x, y, z);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_LIGHTING);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glColor4f(0.0F, 1.0F, 0.0F, 0.4F);
            GL11.glLineWidth(1.0F);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDepthMask(false);
            RenderGlobal.drawOutlinedBoundingBox(aabb.contract(f1, f1, f1), -1);
            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_BLEND);
        }
        if (ConfigHandlerClient.showSkinRenderBounds) {
            if ((tileEntity instanceof TileEntitySkinnableChild)) {
                return;
            }
            
            float f1 = 0.002F;
            
            AxisAlignedBB aabb = tileEntity.getRenderBoundingBox().copy();
            aabb.offset(x - tileEntity.xCoord, y - tileEntity.yCoord, z - tileEntity.zCoord);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_LIGHTING);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glColor4f(1.0F, 1.0F, 0.0F, 0.4F);
            GL11.glLineWidth(1.0F);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDepthMask(false);
            RenderGlobal.drawOutlinedBoundingBox(aabb.contract(f1, f1, f1), -1);
            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_BLEND);
        }
    }
    */
    private class RenderLast implements Comparable<RenderLast> {
        public final TileEntity tileEntity;
        public final double x;
        public final double y;
        public final double z;
        
        public RenderLast(TileEntity tileEntity, double x, double y, double z) {
            this.tileEntity = tileEntity;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public int compareTo(RenderLast o) {
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            double dist = getDistanceFrom(player.posX, player.posY + player.getEyeHeight(), player.posZ);
            double otherDist = o.getDistanceFrom(player.posX, player.posY + player.getEyeHeight(), player.posZ);
            return (int) ((otherDist - dist) * 128);
        }
        
        public double getDistanceFrom(double x, double y, double z) {
            double d3 = (double)tileEntity.getPos().getX() + 0.5D - x;
            double d4 = (double)tileEntity.getPos().getY() + 0.5D - y;
            double d5 = (double)tileEntity.getPos().getZ() + 0.5D - z;
            return d3 * d3 + d4 * d4 + d5 * d5;
        }
    }
}
