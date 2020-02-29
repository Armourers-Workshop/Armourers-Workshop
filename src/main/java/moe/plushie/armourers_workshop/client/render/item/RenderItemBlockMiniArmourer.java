package moe.plushie.armourers_workshop.client.render.item;

import moe.plushie.armourers_workshop.client.model.block.ModelBlockArmourer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;

public class RenderItemBlockMiniArmourer extends TileEntityItemStackRenderer {

    private static final ModelBlockArmourer modelArmourer = new ModelBlockArmourer();

    @Override
    public void renderByItem(ItemStack itemStackIn, float partialTicks) {
        float scale = 0.0625F;
        GlStateManager.pushMatrix();
        GlStateManager.scale(1.1F, 1.1F, 1.1F);
        GlStateManager.rotate(180F, 1F, 0F, 0F);
        GlStateManager.translate(0F, scale * 7F, 0F);
        modelArmourer.render(null, 0, scale);
        GlStateManager.popMatrix();
    }
    
/*implements IItemRenderer*/ 
/*
    private static final ModelBlockArmourer modelArmourer = new ModelBlockArmourer();
    
    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        float scale = 0.0625F;
        GL11.glPushMatrix();
        GL11.glRotatef(180, 1, 0, 0);
        if (type == ItemRenderType.INVENTORY) {
            GL11.glTranslatef(0F, -0.1F, 0F);
        }
        if (type == ItemRenderType.EQUIPPED_FIRST_PERSON | type == ItemRenderType.EQUIPPED) {
            GL11.glTranslatef(0.5F, -0.8F, -0.5F);
        }
        modelArmourer.render(null, 0, scale);
        GL11.glPopMatrix();
    }*/
}
