package riskyken.armourersWorkshop.common.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemArmourTemplate extends AbstractModItem {
    
    private static final String TAG_ARMOUR_DATA = "armourData";
    
    public ItemArmourTemplate() {
        super(LibItemNames.ARMOUR_TEMPLATE);
        setHasSubtypes(true);
    }
    
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < 5; i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }
    
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_) {
        if (stack.hasTagCompound()) {
            if (stack.getTagCompound().hasKey(TAG_ARMOUR_DATA)) {
                list.add("Has armour data");
            }
        } else {
            list.add("No data");
        }
        super.addInformation(stack, player, list, p_77624_4_);
    }
    
    @SideOnly(Side.CLIENT)
    private IIcon[] icons;
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        icons = new IIcon[5];
        icons[0] = register.registerIcon(LibModInfo.ID + ":" + "templateHead");
        icons[1] = register.registerIcon(LibModInfo.ID + ":" + "templateChest");
        icons[2] = register.registerIcon(LibModInfo.ID + ":" + "templateLegs");
        icons[3] = register.registerIcon(LibModInfo.ID + ":" + "templateSkirt");
        icons[4] = register.registerIcon(LibModInfo.ID + ":" + "templateFeet");
    }
    
    public int getArmourTypeNomal(ItemStack stack) {
        if (stack.getItemDamage() > 2) {
            return stack.getItemDamage() - 1;
        } else {
            return stack.getItemDamage();
        }
    }
    
    public int getArmourType(ItemStack stack) {
        return stack.getItemDamage();
    }
    
    @Override
    public IIcon getIconFromDamage(int damage) {
        if (damage < 5 & damage >= 0) {
            return icons[damage];
        }
        return super.getIconFromDamage(damage);
    }
}
