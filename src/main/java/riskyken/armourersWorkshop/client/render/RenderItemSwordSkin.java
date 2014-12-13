package riskyken.armourersWorkshop.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentType;
import riskyken.armourersWorkshop.api.common.lib.LibCommonTags;
import riskyken.armourersWorkshop.client.equipment.ClientEquipmentModelCache;

public class RenderItemSwordSkin implements IItemRenderer {

    private final RenderItem renderItem;
    private final Minecraft mc;
    
    public RenderItemSwordSkin() {
        renderItem = (RenderItem) RenderManager.instance.entityRenderMap.get(EntityItem.class);
        mc = Minecraft.getMinecraft();
    }
    
    @Override
    public boolean handleRenderType(ItemStack stack, ItemRenderType type) {
        if (type == ItemRenderType.INVENTORY) {
            return false;
        }
        return canRenderModel(stack);
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return type == ItemRenderType.ENTITY;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
        if (canRenderModel(stack)) {
            if (type != ItemRenderType.ENTITY) {
                GL11.glPopMatrix();
                GL11.glPopMatrix(); 
                
                GL11.glRotatef(-135, 0, 1, 0);
                GL11.glRotatef(-10, 0, 0, 1);
            }

            GL11.glPushMatrix();
            
            GL11.glScalef(-1F, -1F, 1F);
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
                
                GL11.glTranslatef(-2F * scale, -1F * scale, 0F);
                if (isBlocking) {
                    GL11.glTranslatef(-0F * scale, 2F * scale, 1F * scale);
                }
                GL11.glRotatef(-90F, 0F, 1F, 0F);
                break;
            case ENTITY:
                GL11.glTranslatef(0F, -10F * scale, 0F);
                break;
            case EQUIPPED_FIRST_PERSON:
                GL11.glRotatef(-90F, 0F, 1F, 0F);
                break;
            default:
                break;
            }
            EquipmentItemRenderCache.renderItemAsArmourModel(stack, EnumEquipmentType.SWORD);
            
            GL11.glPopMatrix();
            
            if (type != ItemRenderType.ENTITY) {
                GL11.glPushMatrix();
                GL11.glPushMatrix();
            }

        } else {
            renderNomalIcon(stack);
        }
    }
    
    private boolean canRenderModel(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return false;
        }
        NBTTagCompound armourNBT = stack.getTagCompound().getCompoundTag(LibCommonTags.TAG_ARMOUR_DATA);
        if (armourNBT == null) { return false; }
        if (!armourNBT.hasKey(LibCommonTags.TAG_EQUIPMENT_ID)) { return false; }
        int equipmentId = armourNBT.getInteger(LibCommonTags.TAG_EQUIPMENT_ID);
        if (ClientEquipmentModelCache.INSTANCE.isEquipmentInCache(equipmentId)) {
            return true;
        } else {
            ClientEquipmentModelCache.INSTANCE.requestEquipmentDataFromServer(equipmentId);
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
