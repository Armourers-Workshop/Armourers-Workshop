package riskyken.armourersWorkshop.client.render.tileEntity;

import java.util.ArrayList;
import java.util.Collections;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.client.model.block.ModelBlockSkinnable;
import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import riskyken.armourersWorkshop.client.render.SkinPartRenderer;
import riskyken.armourersWorkshop.client.skin.cache.ClientSkinCache;
import riskyken.armourersWorkshop.common.blocks.BlockSkinnable;
import riskyken.armourersWorkshop.common.config.ConfigHandlerClient;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.common.tileentities.TileEntitySkinnable;
import riskyken.armourersWorkshop.common.tileentities.TileEntitySkinnableChild;

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
        Minecraft mc = Minecraft.getMinecraft();
        mc.mcProfiler.startSection("renderListSort");
        Collections.sort(renderList);
        mc.mcProfiler.endSection();
        mc.entityRenderer.enableLightmap(event.partialTicks);
        RenderHelper.enableStandardItemLighting();
        ModRenderHelper.enableAlphaBlend();
        Minecraft.getMinecraft().mcProfiler.startSection("skinnableBlock");
        for (int i = 0; i < renderList.size(); i++) {
            RenderLast rl = renderList.get(i);
            renderTileEntityAt((TileEntitySkinnable)rl.tileEntity, rl.x, rl.y, rl.z, event.partialTicks);
        }
        Minecraft.getMinecraft().mcProfiler.endSection();
        RenderHelper.disableStandardItemLighting();
        renderList.clear();
        ModRenderHelper.disableAlphaBlend();
        mc.entityRenderer.disableLightmap(event.partialTicks);
    }
    
    public void renderTileEntityAt(TileEntitySkinnable tileEntity, double x, double y, double z, float partialTickTime) {
        ModRenderHelper.setLightingForBlock(tileEntity.getWorldObj(), tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
        SkinPointer skinPointer = tileEntity.getSkinPointer();
        if (skinPointer != null) {
            Skin skin = ClientSkinCache.INSTANCE.getSkin(skinPointer);
            if (skin != null) {
                renderSkin(tileEntity, x, y, z, skin);
            } else {
                ClientSkinCache.INSTANCE.requestSkinFromServer(skinPointer);
                GL11.glPushMatrix();
                GL11.glTranslated(x + 0.5F, y + 0.5F, z + 0.5F);
                loadingModel.render(tileEntity, partialTickTime, 0.0625F);
                GL11.glPopMatrix();
            }
        }
    }
    
    private void renderSkin(TileEntitySkinnable tileEntity, double x, double y, double z, Skin skin) {
        ForgeDirection dir = tileEntity.getRotation();
        int rotation = tileEntity.getBlockMetadata();
        double distance = Minecraft.getMinecraft().thePlayer.getDistance(
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
            float scale = 0.0625F;
            
            
            AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
            aabb.offset(x, y, z);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_LIGHTING);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glColor4f(0.0F, 1.0F, 0.0F, 0.4F);
            GL11.glLineWidth(2.0F);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDepthMask(false);
            RenderGlobal.drawOutlinedBoundingBox(aabb.contract(f1, f1, f1), -1);
            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_BLEND);
        }
    }
    
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
            EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
            double dist = getDistanceFrom(player.posX, player.posY + player.getEyeHeight(), player.posZ);
            double otherDist = o.getDistanceFrom(player.posX, player.posY + player.getEyeHeight(), player.posZ);
            return (int) ((otherDist - dist) * 128);
        }
        
        public double getDistanceFrom(double x, double y, double z) {
            double d3 = (double)tileEntity.xCoord + 0.5D - x;
            double d4 = (double)tileEntity.yCoord + 0.5D - y;
            double d5 = (double)tileEntity.zCoord + 0.5D - z;
            return d3 * d3 + d4 * d4 + d5 * d5;
        }
    }
}
