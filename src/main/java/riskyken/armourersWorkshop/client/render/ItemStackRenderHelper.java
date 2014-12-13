
package riskyken.armourersWorkshop.client.render;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentType;
import riskyken.armourersWorkshop.api.common.lib.LibCommonTags;
import riskyken.armourersWorkshop.client.equipment.ClientEquipmentModelCache;
import riskyken.armourersWorkshop.client.model.equipmet.IEquipmentModel;
import riskyken.armourersWorkshop.common.equipment.data.CustomEquipmentItemData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Helps render item stacks.
 * 
 * @author RiskyKen
 *
 */

@SideOnly(Side.CLIENT)
public final class ItemStackRenderHelper {

    public static void renderItemAsArmourModel(ItemStack stack) {
        renderItemAsArmourModel(stack, EnumEquipmentType.getOrdinal(stack.getItemDamage() + 1));
    }
    
    public static void renderItemAsArmourModel(ItemStack stack, EnumEquipmentType type) {
        NBTTagCompound armourNBT = stack.getTagCompound().getCompoundTag(LibCommonTags.TAG_ARMOUR_DATA);
        int equipmentId = armourNBT.getInteger(LibCommonTags.TAG_EQUIPMENT_ID);
        
        renderItemModelFromId(equipmentId, type);
    }
    
    public static void renderItemModelFromId(int equipmentId, EnumEquipmentType type) {
        IEquipmentModel targetModel = EquipmentModelRenderer.INSTANCE.getModelForEquipmentType(type);
        if (targetModel == null) {
            return;
        }
        
        CustomEquipmentItemData data = ClientEquipmentModelCache.INSTANCE.getEquipmentItemData(equipmentId);
        if (data == null) {
            return;
        }
        
        switch (type) {
        case NONE:
            break;
        case HEAD:
            GL11.glTranslatef(0F, 0.2F, 0F);
            targetModel.render(null, null, data);
            break;
        case CHEST:
            GL11.glTranslatef(0F, -0.35F, 0F);
            targetModel.render(null, null, data);
            break;
        case LEGS:
            GL11.glTranslatef(0F, -1.2F, 0F);
            targetModel.render(null, null, data);
            break;
        case SKIRT:
            GL11.glTranslatef(0F, -1.0F, 0F);
            targetModel.render(null, null, data);
            break;
        case FEET:
            GL11.glTranslatef(0F, -1.2F, 0F);
            targetModel.render(null, null, data);
            break;
        case SWORD:
            targetModel.render(null, null, data);
            break;
        case BOW:
            targetModel.render(null, null, data);;
            break;
        }
    }
}
