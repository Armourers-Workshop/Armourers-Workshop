package riskyken.armourersWorkshop.common.items;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import riskyken.armourersWorkshop.client.lib.LibItemResources;


public class ItemArmourContainer extends AbstractModItemArmour {

    public ItemArmourContainer(String name, int armourType) {
        super(name, ArmorMaterial.IRON, armourType, false);
        setCreativeTab(null);
    }
    
    @SideOnly(Side.CLIENT)
    private IIcon iconChest;
    @SideOnly(Side.CLIENT)
    private IIcon iconLegs;
    @SideOnly(Side.CLIENT)
    private IIcon iconFeet;
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister register) {
        itemIcon = register.registerIcon(LibItemResources.ARMOUR_CONTAINER_HEAD);
        iconChest = register.registerIcon(LibItemResources.ARMOUR_CONTAINER_CHEST);
        iconLegs = register.registerIcon(LibItemResources.ARMOUR_CONTAINER_LEGS);
        iconFeet = register.registerIcon(LibItemResources.ARMOUR_CONTAINER_FEET);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconIndex(ItemStack stack) {
        if (armorType == 1) {
            return iconChest;
        }
        if (armorType == 2) {
            return iconLegs;
        }
        if (armorType == 3) {
            return iconFeet;
        }
        return itemIcon;
    }
}
