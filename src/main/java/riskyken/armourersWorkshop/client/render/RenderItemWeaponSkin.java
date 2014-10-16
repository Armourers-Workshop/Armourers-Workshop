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

import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentType;
import riskyken.armourersWorkshop.api.common.lib.LibCommonTags;

public class RenderItemWeaponSkin implements IItemRenderer {

    private final RenderItem renderItem;
    private final Minecraft mc;
    
    public RenderItemWeaponSkin() {
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
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
        if (canRenderModel(stack)) {
            GL11.glPushMatrix();
            GL11.glScalef(-1F, -1F, 1F);
            float scale = 2.7F;
            GL11.glScalef(scale, scale, scale);
            
            switch (type) {
            case EQUIPPED:
                GL11.glRotatef(70, 1, 0, 1);
                GL11.glRotatef(315, 0, 1, 0);
                GL11.glTranslatef(0.0F, -0.334F, 0.38F);
                break;
            case ENTITY:
                GL11.glScalef(0.5F, 0.5F, 0.5F);
                GL11.glTranslatef(0F, -1.8F, 0F);
                break;
            case EQUIPPED_FIRST_PERSON:
                GL11.glScalef(0.4F, 0.4F, 0.4F);
                GL11.glRotatef(40, 0, 1, 0);
                GL11.glTranslatef(-0.1F, -1.5F, 0.8F);
                break;
            case INVENTORY:
                GL11.glRotatef(90, 0, 1, 0);
                GL11.glTranslatef(0F, -0.22F, 0F);
                GL11.glScalef(0.3F, 0.3F, 0.3F);
                break;
            default:
                break;
            }
            //ModLogger.log("render weapon model");
            GL11.glTranslatef(0F, 0.7F, 0F);
            GL11.glScalef(1.6F, 1.6F, 1.6F);
            GL11.glTranslatef(0F, 0.06F, 0F);
            EquipmentItemRenderCache.renderItemAsArmourModel(stack, EnumEquipmentType.WEAPON);
            GL11.glPopMatrix();
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
