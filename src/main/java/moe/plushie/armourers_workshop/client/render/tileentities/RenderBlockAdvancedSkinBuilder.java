package moe.plushie.armourers_workshop.client.render.tileentities;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.client.render.SkinItemRenderHelper;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityAdvancedSkinBuilder;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityAdvancedSkinBuilder.SkinPartSettings;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
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
        
        for (int i = 0; i < te.getSizeInventory(); i++) {
            ItemStack itemStack = te.getStackInSlot(i);
            ISkinDescriptor descriptor = SkinNBTHelper.getSkinDescriptorFromStack(itemStack);
            if (descriptor == null) {
                continue;
            }
            SkinPartSettings ps = te.getPartSettings(i);
            
            
            GlStateManager.pushAttrib();
            GlStateManager.resetColor();
            GlStateManager.color(1, 1, 1, 1);
            GlStateManager.enableBlend();
            GlStateManager.disableTexture2D();
            GlStateManager.glLineWidth(1F);
            RenderGlobal.drawBoundingBox(
                    -scale / 4F, scale / 1.5F, -scale / 4F,
                    scale / 4F, scale / 1.5F + scale / 2F, scale / 4F, 0.1F, 1F, 0.1F, 0.5F);
            GlStateManager.enableTexture2D();
            GlStateManager.popAttrib();
            
            GlStateManager.pushMatrix();
            GlStateManager.translate(ps.posOffset.x * scale, -ps.posOffset.y * scale, ps.posOffset.z * scale);
            SkinItemRenderHelper.renderSkinWithoutHelper(descriptor, false);
            GlStateManager.popMatrix();
        }
        
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
