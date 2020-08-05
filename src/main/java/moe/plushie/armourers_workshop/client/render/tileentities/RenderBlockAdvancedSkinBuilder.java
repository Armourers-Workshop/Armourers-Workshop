package moe.plushie.armourers_workshop.client.render.tileentities;

import moe.plushie.armourers_workshop.common.tileentities.TileEntityAdvancedSkinBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockAdvancedSkinBuilder extends TileEntitySpecialRenderer<TileEntityAdvancedSkinBuilder> {

    @Override
    public void render(TileEntityAdvancedSkinBuilder te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

        float scale = 0.0625F;
        
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        
        GlStateManager.enableBlend();
        GlStateManager.enableRescaleNormal();
        //GlStateManager.disableLighting();
        
        GlStateManager.translate(x + 0.5F, y + 2.5F, z + 0.5F);
        GlStateManager.scale(-1F, -1F, 1F);
        //GlStateManager.scale(16F, 16F, 16F);
        

        // TODO render here
        
        //GlStateManager.enableLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
    }
    
    @Override
    public boolean isGlobalRenderer(TileEntityAdvancedSkinBuilder te) {
        return true;
    }
}
