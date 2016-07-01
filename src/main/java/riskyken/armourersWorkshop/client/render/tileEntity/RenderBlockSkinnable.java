package riskyken.armourersWorkshop.client.render.tileEntity;

import java.util.ArrayList;
import java.util.Collections;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.client.model.block.ModelBlockSkinnable;
import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import riskyken.armourersWorkshop.client.render.SkinPartRenderer;
import riskyken.armourersWorkshop.client.skin.ClientSkinCache;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.common.tileentities.TileEntitySkinnable;

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
        //mc.entityRenderer.enableLightmap();
        //RenderHelper.disableStandardItemLighting();
        //RenderHelper.enableStandardItemLighting();
        //ModRenderHelper.enableAlphaBlend();
        Minecraft.getMinecraft().mcProfiler.startSection("skinnableBlock");
        for (int i = 0; i < renderList.size(); i++) {
            RenderLast rl = renderList.get(i);
            renderTileEntityAt((TileEntitySkinnable)rl.tileEntity, rl.x, rl.y, rl.z, event.getPartialTicks());
        }
        Minecraft.getMinecraft().mcProfiler.endSection();
        RenderHelper.disableStandardItemLighting();
        renderList.clear();
        ModRenderHelper.disableAlphaBlend();
        //mc.entityRenderer.disableLightmap();
    }
    
    public void renderTileEntityAt(TileEntitySkinnable tileEntity, double x, double y, double z, float partialTickTime) {
        GL11.glPushMatrix();
        //OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
        //ModRenderHelper.setLightingForBlock(tileEntity.getWorld(), tileEntity.getPos());
        //GL11.glColor4f(1, 1, 1, 1);
        SkinPointer skinPointer = tileEntity.getSkinPointer();
        if (skinPointer != null) {
            Skin skin = ClientSkinCache.INSTANCE.getSkin(skinPointer);
            if (skin != null) {
                renderSkin(tileEntity, x, y, z, skin);
            } else {
                ClientSkinCache.INSTANCE.requestSkinFromServer(skinPointer);
                GL11.glPushMatrix();
                //GL11.glTranslated(x + 0.5F, y + 0.5F, z + 0.5F);
                loadingModel.render(tileEntity, partialTickTime, 0.0625F);
                GL11.glPopMatrix();
            }
        }
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glPopMatrix();
    }
    
    private void renderSkin(TileEntitySkinnable tileEntity, double x, double y, double z, Skin skin) {
        int rotation = tileEntity.getBlockMetadata();
        ModRenderHelper.enableAlphaBlend();
        GL11.glTranslated(x + 0.5F, y + 0.5F, z + 0.5F);
        GL11.glScalef(-1, -1, 1);
        if (rotation != 0) {
          GL11.glRotatef((90F * rotation), 0, 1, 0);
        }
        skin.onUsed();
        double distance = Minecraft.getMinecraft().thePlayer.getDistance(
                tileEntity.getPos().getX() + 0.5F,
                tileEntity.getPos().getY() + 0.5F,
                tileEntity.getPos().getZ() + 0.5F);
        for (int i = 0; i < skin.getParts().size(); i++) {
            SkinPart skinPart = skin.getParts().get(i);
            SkinPartRenderer.INSTANCE.renderPart(skinPart, 0.0625F, tileEntity.getSkinPointer().getSkinDye(), null, distance);
        }
        if (rotation != 0) {
            //GL11.glRotatef((90F * -rotation), 0, 1, 0);
          }
        ModRenderHelper.disableAlphaBlend();
        //GL11.glScalef(-1, -1, 1);
        //GL11.glTranslated(-x - 0.5F, -y - 0.5F, -z - 0.5F);
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
            EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
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

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage) {
        renderTileEntityAt((TileEntitySkinnable) te, x, y, z, partialTicks);
        //renderList.add(new RenderLast(te, x, y, z));
    }
}
