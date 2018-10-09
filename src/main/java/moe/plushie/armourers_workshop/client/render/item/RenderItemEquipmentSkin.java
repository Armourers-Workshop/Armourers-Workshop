package moe.plushie.armourers_workshop.client.render.item;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.client.render.ModRenderHelper;
import moe.plushie.armourers_workshop.client.render.SkinItemRenderHelper;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;

public class RenderItemEquipmentSkin extends TileEntityItemStackRenderer {
    
    
    @Override
    public void renderByItem(ItemStack itemStackIn) {
        //ModLogger.log("render");
        if (canRenderModel(itemStackIn)) {
            Minecraft mc = Minecraft.getMinecraft();
            GL11.glPushMatrix();
            GL11.glScalef(-1F, -1F, 1F);
            GL11.glRotatef(180, 0, 1, 0);
            /*
            switch (type) {
            case EQUIPPED:
                GL11.glTranslatef(0.6F, -0.5F, -0.5F);
                GL11.glRotatef(180, 0, 1, 0);
                break;
            case ENTITY:
                GL11.glTranslatef(0F, -0.4F, 0F);
                break;
            case EQUIPPED_FIRST_PERSON:
                GL11.glTranslatef(0.5F, -0.7F, -0.5F);
                GL11.glRotatef(90, 0, 1, 0);
                break;
            case INVENTORY:
                //float rotation = (float)((double)System.currentTimeMillis() / 10 % 360);
                //GL11.glRotatef(rotation, 0F, 1F, 0F);
                break;
            default:
                break;
            }
            */
            mc.profiler.startSection("armourersItemSkin");
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
            ModRenderHelper.enableAlphaBlend();
            GL11.glEnable(GL11.GL_CULL_FACE);
            SkinItemRenderHelper.renderSkinAsItem(itemStackIn, true, 16, 16);
            //ItemStackRenderHelper.renderItemAsArmourModel(stack, true);
            GL11.glPopAttrib();
            mc.profiler.endSection();
            GL11.glPopMatrix();
        } else {
            //renderNomalIcon(stack);
        }
    }
    
    private boolean canRenderModel(ItemStack stack) {
        if (SkinNBTHelper.stackHasSkinData(stack)) {
            SkinDescriptor skinData = SkinNBTHelper.getSkinDescriptorFromStack(stack);
            if (ClientSkinCache.INSTANCE.isSkinInCache(skinData)) {
                return true;
            } else {
                ClientSkinCache.INSTANCE.requestSkinFromServer(skinData);
                return false;
            }
        } else {
            return false;
        }
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
