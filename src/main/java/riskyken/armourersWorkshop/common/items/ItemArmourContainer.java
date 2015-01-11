package riskyken.armourersWorkshop.common.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class ItemArmourContainer extends AbstractModItemArmour {

    public ItemArmourContainer(String name, int armourType) {
        super(name, ArmorMaterial.IRON, armourType, false);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        itemIcon = register.registerIcon(LibItemResources.ARMOUR_CONTAINER);
    }
}
