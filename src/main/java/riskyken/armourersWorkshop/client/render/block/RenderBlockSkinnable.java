package riskyken.armourersWorkshop.client.render.block;

import java.util.ArrayList;
import java.util.Collections;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.client.model.ClientModelCache;
import riskyken.armourersWorkshop.client.model.block.ModelBlockSkinnable;
import riskyken.armourersWorkshop.client.render.EquipmentPartRenderer;
import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.common.tileentities.TileEntitySkinnable;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
        GL11.glPushMatrix();
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
        GL11.glPopMatrix();
    }
    
    public void renderTileEntityAt(TileEntitySkinnable tileEntity, double x, double y, double z, float partialTickTime) {
        ModRenderHelper.setLightingForBlock(tileEntity.getWorldObj(), tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
        SkinPointer skinPointer = tileEntity.getSkinPointer();
        if (skinPointer != null) {
            Skin skin = ClientModelCache.INSTANCE.getEquipmentItemData(skinPointer.getSkinId());
            if (skin != null) {
                renderSkin(tileEntity, x, y, z, skin);
            } else {
                ClientModelCache.INSTANCE.requestEquipmentDataFromServer(skinPointer.getSkinId());
                GL11.glPushMatrix();
                GL11.glTranslated(x + 0.5F, y + 0.5F, z + 0.5F);
                loadingModel.render(tileEntity, partialTickTime, 0.0625F);
                GL11.glPopMatrix();
            }
        }
    }
    
    private void renderSkin(TileEntitySkinnable tileEntity, double x, double y, double z, Skin skin) {
        GL11.glPushMatrix();
        int rotation = tileEntity.getBlockMetadata();
        GL11.glTranslated(x + 0.5F, y + 0.5F, z + 0.5F);
        GL11.glScalef(-1, -1, 1);
        GL11.glRotatef((90F * rotation), 0, 1, 0);
        if (skin.getCustomName().isEmpty()) {
            Minecraft.getMinecraft().mcProfiler.startSection("unnamedSkin");
        } else {
            Minecraft.getMinecraft().mcProfiler.startSection(skin.getCustomName().replace(" ", ""));
        }
        skin.onUsed();
        for (int i = 0; i < skin.getParts().size(); i++) {
            SkinPart skinPart = skin.getParts().get(i);
            EquipmentPartRenderer.INSTANCE.renderPart(skinPart, 0.0625F);
        }
        GL11.glPopMatrix();
        Minecraft.getMinecraft().mcProfiler.endSection();
    }
    
    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTickTime) {
        renderList.add(new RenderLast(tileEntity, x, y, z));
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
            double dist = getDistanceFrom(player.posX, player.posY, player.posZ);
            double otherDist = o.getDistanceFrom(player.posX, player.posY, player.posZ);
            return (int) ((otherDist - dist) * 16);
        }
        
        public double getDistanceFrom(double x, double y, double z) {
            double d3 = (double)tileEntity.xCoord + 0.5D - x;
            double d4 = (double)tileEntity.yCoord + 0.5D - y;
            double d5 = (double)tileEntity.zCoord + 0.5D - z;
            return d3 * d3 + d4 * d4 + d5 * d5;
        }
    }
}
