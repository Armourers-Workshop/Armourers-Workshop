package moe.plushie.armourers_workshop.client.render.tileentities;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.render.ArmourerRenderHelper;
import moe.plushie.armourers_workshop.client.render.ModRenderHelper;
import moe.plushie.armourers_workshop.client.texture.PlayerTexture;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.data.SkinTexture;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityArmourer;
import moe.plushie.armourers_workshop.proxies.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
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

        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        GlStateManager.enableBlend();
        GlStateManager.enableRescaleNormal();
        GlStateManager.disableLighting();

        ModRenderHelper.enableAlphaBlend();

        // ModRenderHelper.disableLighting();
        GlStateManager.translate(x, y, z);

        if (te.getDirection() != null) {
            switch (te.getDirection()) {
            case EAST:
                GlStateManager.rotate(270F, 0F, 1F, 0F);
                break;
            case SOUTH:
                GlStateManager.rotate(180F, 0F, 1F, 0F);
                break;
            case WEST:
                GlStateManager.rotate(90F, 0F, 1F, 0F);
                break;
            default:
                break;
            }
        }

        GlStateManager.translate(0F, te.getHeightOffset(), 0F);

        GlStateManager.scale(-1F, -1F, 1F);
        GlStateManager.scale(16F, 16F, 16F);

        if (skinType != null) {
            mc.profiler.startSection("modelRender");
            GlStateManager.enablePolygonOffset();
            GlStateManager.doPolygonOffset(3F, 3F);
            long time = System.currentTimeMillis();
            int fadeTime = 1000;
            int fade = (int) (time - playerTexture.getDownloadTime());
            if (playerTexture.isDownloaded() & fade < fadeTime) {
                PlayerTexture oldTexture = te.getTextureOld();
                oldTexture = ClientProxy.playerTextureDownloader.getPlayerTexture(oldTexture);
                bindTexture(oldTexture.getResourceLocation());
                ArmourerRenderHelper.renderBuildingGuide(skinType, scale, te.getSkinProps(), te.isShowHelper());
                te.skinTexture.bindTexture();
                ModRenderHelper.enableAlphaBlend();
                GlStateManager.color(1, 1, 1, fade / 1000F);
                GlStateManager.doPolygonOffset(-6F, -6F);
                ArmourerRenderHelper.renderBuildingGuide(skinType, scale, te.getSkinProps(), te.isShowHelper());
                GlStateManager.doPolygonOffset(3F, 3F);
                ModRenderHelper.disableAlphaBlend();
                GlStateManager.color(1, 1, 1, 1);
            } else {
                te.skinTexture.bindTexture();
                ArmourerRenderHelper.renderBuildingGuide(skinType, scale, te.getSkinProps(), te.isShowHelper());
            }
            GlStateManager.doPolygonOffset(-3F, -3F);
            mc.profiler.endSection();
            mc.profiler.startSection("renderGuideGrid");
            ArmourerRenderHelper.renderBuildingGrid(skinType, scale, te.isShowGuides(), te.getSkinProps(), SkinProperties.PROP_BLOCK_MULTIBLOCK.getValue(te.getSkinProps()));
            mc.profiler.endSection();
            GlStateManager.doPolygonOffset(0F, 0F);
            GlStateManager.disablePolygonOffset();
        }
        // GlStateManager.color(1F, 1F, 1F, 1F);
        // ModRenderHelper.enableLighting();

        ModRenderHelper.disableAlphaBlend();

        GlStateManager.enableLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();

        mc.profiler.endSection();
    }

    @Override
    public boolean isGlobalRenderer(TileEntityArmourer te) {
        return true;
    }
}
