package moe.plushie.armourers_workshop.client.render.tileEntity;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.render.ModRenderHelper;
import moe.plushie.armourers_workshop.client.render.SkinRenderHelper;
import moe.plushie.armourers_workshop.client.texture.PlayerTexture;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.data.SkinTexture;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityArmourer;
import moe.plushie.armourers_workshop.proxies.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockArmourer extends TileEntitySpecialRenderer<TileEntityArmourer> {
    
    
    @Override
    public void render(TileEntityArmourer te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.profiler.startSection("armourersArmourer");
        float scale = 0.0625F;
        
        ISkinType skinType = te.getSkinType();
        
        mc.profiler.startSection("textureBuild");
        if (te.skinTexture == null) {
            te.skinTexture = new SkinTexture();
        }
        
        PlayerTexture playerTexture = ClientProxy.playerTextureDownloader.getPlayerTexture(te.getTexture());
        if (!playerTexture.isDownloaded()) {
            playerTexture = ClientProxy.playerTextureDownloader.getPlayerTexture(te.getTextureOld());
        }
        
        te.skinTexture.updateForResourceLocation(playerTexture.getResourceLocation());
        te.skinTexture.updatePaintData(te.getPaintData());
        mc.profiler.endSection();
        
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glColor3f(0.77F, 0.77F, 0.77F);
        ModRenderHelper.disableLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glTranslated(x, y, z);
        
        if (te.getDirection() != null) {
            switch (te.getDirection()) {
            case EAST:
                GL11.glRotatef(270, 0, 1, 0);
                break;
            case SOUTH:
                GL11.glRotatef(180, 0, 1, 0);
                break; 
            case WEST:
                GL11.glRotatef(90, 0, 1, 0);
                break;
            default:
                break;
            }
        }
        
        GL11.glTranslated(0, te.getHeightOffset(), 0);
        
        GL11.glScalef(-1, -1, 1);
        GL11.glScalef(16, 16, 16);
        
        if (skinType != null) {
            mc.profiler.startSection("modelRender");
            boolean hidden = false;
            if (skinType.getVanillaArmourSlotId() != -1) {
                if (SkinProperties.PROP_ARMOUR_OVERRIDE.getValue(te.getSkinProps())) {
                    hidden = true;
                }
            }
            GL11.glPolygonOffset(3F, 3F);
            GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
            if (!hidden) {
                long time = System.currentTimeMillis();
                int fadeTime = 1000;
                int fade = (int) (time - playerTexture.getDownloadTime());
                if (playerTexture.isDownloaded() & fade < fadeTime) {
                    PlayerTexture oldTexture = te.getTextureOld();
                    oldTexture = ClientProxy.playerTextureDownloader.getPlayerTexture(oldTexture);
                    bindTexture(oldTexture.getResourceLocation());
                    SkinRenderHelper.renderBuildingGuide(skinType, scale, te.isShowOverlay(), te.isShowHelper());
                    te.skinTexture.bindTexture();
                    ModRenderHelper.enableAlphaBlend();
                    GL11.glColor4f(1, 1, 1, fade / 1000F);
                    GL11.glPolygonOffset(-6F, -6F);
                    SkinRenderHelper.renderBuildingGuide(skinType, scale, te.isShowOverlay(), te.isShowHelper());
                    GL11.glPolygonOffset(3F, 3F);
                    ModRenderHelper.disableAlphaBlend();
                    GL11.glColor4f(1, 1, 1, 1);
                } else {
                    te.skinTexture.bindTexture();
                    SkinRenderHelper.renderBuildingGuide(skinType, scale, te.isShowOverlay(), te.isShowHelper());
                }
            }
            
            GL11.glPolygonOffset(-3F, -3F);
            mc.profiler.endSection();
            mc.profiler.startSection("renderGuideGrid");
            SkinRenderHelper.renderBuildingGrid(skinType, scale, te.isShowGuides(), hidden, SkinProperties.PROP_BLOCK_MULTIBLOCK.getValue(te.getSkinProps()));
            mc.profiler.endSection();
            GL11.glPolygonOffset(0F, 0F);
            GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
        }
        
        GL11.glPopAttrib();
        GL11.glPopMatrix();
        GL11.glColor3f(1F, 1F, 1F);
        
        ModRenderHelper.enableLighting();
        GL11.glEnable(GL11.GL_LIGHTING);
        mc.profiler.endSection();
    }
}
