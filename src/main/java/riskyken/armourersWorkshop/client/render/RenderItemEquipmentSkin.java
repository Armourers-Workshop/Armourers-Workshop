package riskyken.armourersWorkshop.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.common.lib.LibCommonTags;

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
            switch (type) {
            case EQUIPPED:
                GL11.glRotatef(180, 0, 1, 0);
                GL11.glTranslatef(-0.5F, -0.8F, 0.5F);
                break;
            case ENTITY:
                GL11.glTranslatef(0F, -0.3F, 0F);
                break;
            case EQUIPPED_FIRST_PERSON:
                GL11.glScalef(0.8F, 0.8F, 0.8F);
                GL11.glRotatef(90, 0, 1, 0);
                GL11.glTranslatef(0.3F, -0.9F, 0.3F);
                break;
            default:
                break;
            }
            EquipmentItemRenderCache.renderItemAsArmourModel(stack);
            GL11.glPopMatrix();
            
        } else {
            renderNomalIcon(stack);
        }
    }
    
    private boolean canRenderModel(ItemStack stack) {
        NBTTagCompound armourNBT = stack.getTagCompound().getCompoundTag(LibCommonTags.TAG_ARMOUR_DATA);
        if (armourNBT == null) { return false; }
        if (!armourNBT.hasKey(LibCommonTags.TAG_EQUPMENT_ID)) { return false; }
        int equipmentId = armourNBT.getInteger(LibCommonTags.TAG_EQUPMENT_ID);
        if (EquipmentItemRenderCache.isEquipmentInCache(equipmentId)) {
            return true;
        } else {
            EquipmentItemRenderCache.requestEquipmentDataFromServer(equipmentId);
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
