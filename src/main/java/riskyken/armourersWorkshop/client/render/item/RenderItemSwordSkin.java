package riskyken.armourersWorkshop.client.render.item;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import riskyken.armourersWorkshop.client.model.ClientModelCache;
import riskyken.armourersWorkshop.client.render.ItemStackRenderHelper;
import riskyken.armourersWorkshop.common.addons.Addons;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper;
import riskyken.armourersWorkshop.utils.EventState;

public class RenderItemSwordSkin implements IItemRenderer {

    private final RenderItem renderItem;
    private final Minecraft mc;
    
    public RenderItemSwordSkin() {
        renderItem = (RenderItem) RenderManager.instance.entityRenderMap.get(EntityItem.class);
        mc = Minecraft.getMinecraft();
    }
    
    @Override
    public boolean handleRenderType(ItemStack stack, ItemRenderType type) {
        IItemRenderer render = Addons.getItemRenderer(stack, type);
        if (canRenderModel(stack)) {
            if (type == ItemRenderType.INVENTORY) {
                if (render != null) {
                    return render.handleRenderType(stack, type);
                } else {
                    return false;
                }
            } else {
                return true;
            }
        } else {
            if (render != null) {
                return render.handleRenderType(stack, type);
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack stack, ItemRendererHelper helper) {
        IItemRenderer render = Addons.getItemRenderer(stack, type);
        if (canRenderModel(stack)) {
            if (type == ItemRenderType.INVENTORY) {
                if (render != null) {
                    return render.shouldUseRenderHelper(type, stack, helper);
                } else {
                    return false;
                }
            } else {
                return type == ItemRenderType.ENTITY;
            }
        } else {
            if (render != null) {
                return render.shouldUseRenderHelper(type, stack, helper);
            } else {
                return false;
            }
        }
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
        if (canRenderModel(stack) & type != ItemRenderType.INVENTORY) {
            if (type != ItemRenderType.ENTITY) {
                GL11.glPopMatrix();
                GL11.glRotatef(-135, 0, 1, 0);
                GL11.glRotatef(-10, 0, 0, 1);
            }

            GL11.glPushMatrix();
            
            GL11.glScalef(1F, -1F, 1F);
            GL11.glScalef(1.6F, 1.6F, 1.6F);

            boolean isBlocking = false;
            
            if (data.length >= 2) {
                if (data[1] instanceof AbstractClientPlayer & data[0] instanceof RenderBlocks) {
                    RenderBlocks renderBlocks = (RenderBlocks) data[0];
                    AbstractClientPlayer player = (AbstractClientPlayer) data[1];
                    isBlocking = player.isBlocking();
                }
            }
            
            float scale = 0.0625F;
            
            switch (type) {
            case EQUIPPED:
                
                GL11.glTranslatef(2F * scale, -1F * scale, 0F * scale);
                if (isBlocking) {
                    GL11.glTranslatef(-0F * scale, 2F * scale, 1F * scale);
                }
                GL11.glRotatef(90F, 0F, 1F, 0F);
                break;
            case ENTITY:
                GL11.glScalef(-1F, 1F, 1F);
                GL11.glTranslatef(0F, -10F * scale, 0F);
                break;
            case EQUIPPED_FIRST_PERSON:
                GL11.glScalef(-1F, 1F, 1F);
                GL11.glRotatef(-90F, 0F, 1F, 0F);
                break;
            default:
                break;
            }
            GL11.glEnable(GL11.GL_CULL_FACE);
            Addons.onWeaponRender(type, EventState.PRE);
            ItemStackRenderHelper.renderItemAsArmourModel(stack);
            Addons.onWeaponRender(type, EventState.POST);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glPopMatrix();
            
            if (type != ItemRenderType.ENTITY) {
                GL11.glPushMatrix();
            }

        } else {
            IItemRenderer render = Addons.getItemRenderer(stack, type);
            if (render != null) {
                render.renderItem(type, stack, data);
            } else {
                renderNomalIcon(stack);
            }
        }
    }
    
    private boolean canRenderModel(ItemStack stack) {
        if (EquipmentNBTHelper.stackHasSkinData(stack)) {
            SkinPointer skinData = EquipmentNBTHelper.getSkinPointerFromStack(stack);
            if (ClientModelCache.INSTANCE.isEquipmentInCache(skinData.skinId)) {
                return true;
            } else {
                ClientModelCache.INSTANCE.requestEquipmentDataFromServer(skinData.skinId);
                return false;
            }
        } else {
            return false;
        }
    }
    
    private void renderNomalIcon(ItemStack stack) {
        IIcon icon = stack.getItem().getIcon(stack, 0);
        renderItem.renderIcon(0, 0, icon, icon.getIconWidth(), icon.getIconHeight());
        icon = stack.getItem().getIcon(stack, 1);
        renderItem.renderIcon(0, 0, icon, icon.getIconWidth(), icon.getIconHeight());
    }
}
