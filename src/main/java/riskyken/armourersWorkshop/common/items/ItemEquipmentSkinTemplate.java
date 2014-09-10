package riskyken.armourersWorkshop.common.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemEquipmentSkinTemplate extends AbstractModItem {
    
    public ItemEquipmentSkinTemplate() {
        super(LibItemNames.EQUIPMENT_SKIN_TEMPLATE);
        setMaxStackSize(16);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        itemIcon = register.registerIcon(LibModInfo.ID + ":" + "templateBlank");
    }
}
