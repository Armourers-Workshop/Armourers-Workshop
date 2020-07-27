package moe.plushie.armourers_workshop.client.render.tileentities;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.client.model.block.ModelBlockSkinnable;
import moe.plushie.armourers_workshop.client.render.SkinPartRenderData;
import moe.plushie.armourers_workshop.client.render.SkinPartRenderer;
import moe.plushie.armourers_workshop.client.render.item.RenderItemEquipmentSkin;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.init.blocks.BlockSkinnable;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import moe.plushie.armourers_workshop.common.tileentities.TileEntitySkinnable;
import moe.plushie.armourers_workshop.common.tileentities.TileEntitySkinnableChild;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockSkinnable extends TileEntitySpecialRenderer<TileEntitySkinnable> {

    private static final ModelBlockSkinnable loadingModel = new ModelBlockSkinnable();
    private static final float SCALE = 0.0625F;
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
    
    @Override
    public void render(TileEntitySkinnable te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        ISkinDescriptor skinPointer = te.getSkinPointer();
        IBlockState state = te.getWorld().getBlockState(te.getPos());
        
        if (!(state.getBlock() instanceof BlockSkinnable)) {
            return;
        }
        
        if (skinPointer != null) {
            Skin skin = ClientSkinCache.INSTANCE.getSkin(skinPointer);
            if (skin != null) {
                if (te.isParent()) {
                    GlStateManager.pushMatrix();
                    GlStateManager.pushAttrib();
                    GlStateManager.translate(x + 0.5F, y + 0.5F, z + 0.5F);
                    GlStateManager.enableBlend();
                    GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
                    EnumFacing facing = state.getValue(BlockSkinnable.STATE_FACING);
                    //RenderItemEquipmentSkin.renderLoadingIcon(te.getSkinPointer());
                    if (facing == EnumFacing.EAST) {
                        GlStateManager.rotate(-90F, 0, 1, 0);
                    }
                    if (facing == EnumFacing.SOUTH) {
                        GlStateManager.rotate(180F, 0, 1, 0);
                    }
                    if (facing == EnumFacing.WEST) {
                        GlStateManager.rotate(90F, 0, 1, 0);
                    }
                    
                    double distance = Minecraft.getMinecraft().player.getDistance(te.getPos().getX(), te.getPos().getY(), te.getPos().getZ());
                    //GL11.glRotatef((90F * ((-facing.getIndex() + 4))), 0, 1, 0);
                    GlStateManager.scale(-1, -1, 1);
                    for (int i = 0; i < skin.getParts().size(); i++) {
                        SkinPart skinPart = skin.getParts().get(i);
                        SkinPartRenderer.INSTANCE.renderPart(new SkinPartRenderData(skinPart, SCALE, te.getSkinPointer().getSkinDye(), null, distance, true, true, true, null));
                    }
                    //renderSkin(tileEntity, x, y, z, skin);
                    GlStateManager.disableBlend();
                    GlStateManager.popAttrib();
                    GlStateManager.popMatrix();
                    return;
                } else {
                    if (!((TileEntitySkinnableChild)te).isParentValid()) {
                        GlStateManager.pushMatrix();
                        GlStateManager.translate(x + 0.5F, y + 0.5F, z + 0.5F);
                        RenderItemEquipmentSkin.renderLoadingIcon(te.getSkinPointer());
                        GlStateManager.popMatrix();
                    }
                    return;
                }
            } else {
                ClientSkinCache.INSTANCE.requestSkinFromServer(skinPointer);
            }
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5F, y + 0.5F, z + 0.5F);
        RenderItemEquipmentSkin.renderLoadingIcon(te.getSkinPointer());
        GlStateManager.popMatrix();
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
            double d3 = tileEntity.getPos().getX() + 0.5D - x;
            double d4 = tileEntity.getPos().getY() + 0.5D - y;
            double d5 = tileEntity.getPos().getZ() + 0.5D - z;
            return d3 * d3 + d4 * d4 + d5 * d5;
        }
    }
}
