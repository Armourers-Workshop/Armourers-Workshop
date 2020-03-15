package moe.plushie.armourers_workshop.client.render.item;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.client.model.block.ModelBlockSkinnable;
import moe.plushie.armourers_workshop.client.render.ModRenderHelper;
import moe.plushie.armourers_workshop.client.render.SkinItemRenderHelper;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;

public class RenderItemEquipmentSkin extends TileEntityItemStackRenderer {
    
    private static final ModelBlockSkinnable loadingModel = new ModelBlockSkinnable();
    
    @Override
    public void renderByItem(ItemStack itemStackIn) {
        ISkinDescriptor descriptor = SkinNBTHelper.getSkinDescriptorFromStack(itemStackIn);
        if (canRenderModel(descriptor)) {
            Minecraft mc = Minecraft.getMinecraft();
            GlStateManager.pushMatrix();
            GlStateManager.pushAttrib();
            GlStateManager.scale(-1F, -1F, 1F);
            GlStateManager.rotate(180, 0, 1, 0);
            mc.profiler.startSection("armourersItemSkin");
            ModRenderHelper.enableAlphaBlend();
            GL11.glEnable(GL11.GL_CULL_FACE);
            
            GlStateManager.disableNormalize();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableDepth();
            
            GlStateManager.enableNormalize();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableDepth();
            
            GlStateManager.translate(8 * 0.0625F, -8 * 0.0625F, 0);
            GlStateManager.scale(0.6F, 0.6F, 0.6F);
            GlStateManager.rotate(30, 1, 0, 0);
            GlStateManager.rotate(45, 0, 1, 0);
            SkinItemRenderHelper.renderSkinAsItem(itemStackIn, true, 16, 16);
            mc.profiler.endSection();
            GlStateManager.popAttrib();
            GlStateManager.popMatrix();
        } else {
            GL11.glPushMatrix();
            GlStateManager.translate(8 * 0.0625F, 8 * 0.0625F, 0);
            renderLoadingIcon(descriptor);
            GL11.glPopMatrix();
        }
    }
    
    private boolean canRenderModel(ISkinDescriptor descriptor) {
        if (descriptor != null) {
            if (ClientSkinCache.INSTANCE.isSkinInCache(descriptor)) {
                return true;
            } else {
                ClientSkinCache.INSTANCE.requestSkinFromServer(descriptor);
                return false;
            }
        }
        return false;
    }
    
    public static void renderLoadingIcon(ISkinDescriptor descriptor) {
        renderLoadingIcon(descriptor, 0.0625F);
    }
    
    public static void renderLoadingIcon(ISkinDescriptor descriptor, float scale) {
        if (descriptor == null) {
            return;
        }
        Minecraft.getMinecraft().renderEngine.bindTexture(descriptor.getIdentifier().getSkinType().getIcon());
        float angle = (float) (System.currentTimeMillis() / 5 % 360D);
        
        GlStateManager.pushMatrix();
        
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.translate(8, 8, 0);
        
        GlStateManager.translate(-8, 0, 0);
        GlStateManager.rotate(angle, 0, 1, 0);
        GlStateManager.translate(8, 0, 0);
        
        GlStateManager.disableCull();
        GlStateManager.disableLighting();
        GlStateManager.scale(-1, -1, 1);
        Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 16, 16, 16, 16);
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }
    
/*
    protected final RenderItem renderItem;
    protected final Minecraft mc;

    public RenderItemEquipmentSkin() {
        renderItem = (RenderItem) RenderManager.instance.entityRenderMap.get(EntityItem.class);
        mc = Minecraft.getMinecraft();
    }

    @Override
    public boolean handleRenderType(ItemStack stack, ItemRenderType type) {
        return canRenderModel(stack);
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack stack, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {

    }
    

    
    private void renderNomalIcon(ItemStack stack) {
        IIcon icon = stack.getItem().getIcon(stack, 0);
        renderItem.renderIcon(0, 0, icon, icon.getIconWidth(), icon.getIconHeight());
        if (stack.getItem().getRenderPasses(stack.getItemDamage()) > 1) {
            icon = stack.getItem().getIcon(stack, 1);
            renderItem.renderIcon(0, 0, icon, icon.getIconWidth(), icon.getIconHeight());
        }

    }*/
}
