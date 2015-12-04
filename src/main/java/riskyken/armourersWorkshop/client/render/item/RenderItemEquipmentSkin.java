package riskyken.armourersWorkshop.client.render.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.render.ItemStackRenderHelper;
import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import riskyken.armourersWorkshop.client.skin.ClientSkinCache;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;

public class RenderItemEquipmentSkin implements IItemRenderer {

    private final RenderItem renderItem;
    private final Minecraft mc;

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
        if (canRenderModel(stack)) {
            GL11.glPushMatrix();
            GL11.glScalef(-1F, -1F, 1F);
            float scale = 1.2F;
            GL11.glScalef(scale, scale, scale);
            GL11.glRotatef(180, 0, 1, 0);
            
            ISkinType skinType = SkinNBTHelper.getSkinTypeFromStack(stack);
            if (skinType == SkinTypeRegistry.skinSword) {
                GL11.glScalef(0.7F, 0.7F, 0.7F);
            }
            if (skinType == SkinTypeRegistry.skinArrow) {
                GL11.glTranslatef(0F, 0F, -0.4F * scale);
            }
            if (skinType == SkinTypeRegistry.skinBlock) {
                GL11.glScalef(0.9F, 0.9F, 0.9F);
            }
            switch (type) {
            case EQUIPPED:
                GL11.glTranslatef(0.6F, -0.5F, -0.5F);
                GL11.glRotatef(180, 0, 1, 0);
                break;
            case ENTITY:
                GL11.glTranslatef(0F, -0.3F, 0F);
                break;
            case EQUIPPED_FIRST_PERSON:
                GL11.glTranslatef(0.5F, -0.7F, -0.5F);
                GL11.glRotatef(90, 0, 1, 0);
                break;
            default:
                break;
            }
            mc.mcProfiler.startSection("armourersItemSkin");
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
            ModRenderHelper.enableAlphaBlend();
            GL11.glEnable(GL11.GL_CULL_FACE);
            ItemStackRenderHelper.renderItemAsArmourModel(stack, true);
            GL11.glPopAttrib();
            mc.mcProfiler.endSection();
            GL11.glPopMatrix();
        } else {
            renderNomalIcon(stack);
        }
    }
    
    private boolean canRenderModel(ItemStack stack) {
        if (SkinNBTHelper.stackHasSkinData(stack)) {
            SkinPointer skinData = SkinNBTHelper.getSkinPointerFromStack(stack);
            if (ClientSkinCache.INSTANCE.isSkinInCache(skinData.skinId)) {
                return true;
            } else {
                ClientSkinCache.INSTANCE.requestSkinFromServer(skinData.skinId);
                return false;
            }
        } else {
            return false;
        }
    }
    
    private void renderNomalIcon(ItemStack stack) {
        IIcon icon = stack.getItem().getIcon(stack, 0);
        renderItem.renderIcon(0, 0, icon, icon.getIconWidth(), icon.getIconHeight());
        if (stack.getItem().getRenderPasses(stack.getItemDamage()) > 1) {
            icon = stack.getItem().getIcon(stack, 1);
            renderItem.renderIcon(0, 0, icon, icon.getIconWidth(), icon.getIconHeight());
        }

    }
}
