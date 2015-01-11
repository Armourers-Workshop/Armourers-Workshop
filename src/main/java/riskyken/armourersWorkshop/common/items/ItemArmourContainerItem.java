package riskyken.armourersWorkshop.common.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.common.equipment.ISkinHolder;
import riskyken.armourersWorkshop.common.equipment.data.CustomEquipmentItemData;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemArmourContainerItem extends AbstractModItem implements ISkinHolder {

    public ItemArmourContainerItem() {
        super(LibItemNames.ARMOUR_CONTAINER);
        setMaxStackSize(64);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        itemIcon = register.registerIcon(LibItemResources.ARMOUR_CONTAINER);
    }

    @Override
    public ItemStack makeStackForEquipment(CustomEquipmentItemData armourItemData) {
        if (armourItemData.getType().ordinal() < 6) {
            return EquipmentNBTHelper.makeStackForEquipment(armourItemData, true);
        }
        return null;
    }
}
