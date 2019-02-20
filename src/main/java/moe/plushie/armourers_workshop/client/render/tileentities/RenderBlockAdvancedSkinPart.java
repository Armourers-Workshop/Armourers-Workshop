package moe.plushie.armourers_workshop.client.render.tileentities;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.client.render.SkinItemRenderHelper;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityAdvancedSkinPart;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockAdvancedSkinPart extends TileEntitySpecialRenderer<TileEntityAdvancedSkinPart> {

    @Override
    public void render(TileEntityAdvancedSkinPart te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        ItemStack itemStack = te.getStackInSlot(0);
        ISkinDescriptor descriptor = SkinNBTHelper.getSkinDescriptorFromStack(itemStack);
        if (descriptor == null) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        
        GlStateManager.enableBlend();
        GlStateManager.enableRescaleNormal();
        //GlStateManager.disableLighting();
        
        GlStateManager.translate(x + 0.5F, y + 1.5F, z + 0.5F);
        GlStateManager.scale(-1F, -1F, 1F);
        GlStateManager.scale(16F, 16F, 16F);
        
        SkinItemRenderHelper.renderSkinWithoutHelper(descriptor, false);
        
        //GlStateManager.enableLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
    }
    
    @Override
    public boolean isGlobalRenderer(TileEntityAdvancedSkinPart te) {
        return true;
    }
}
