package riskyken.armourersWorkshop.common.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import riskyken.armourersWorkshop.common.lib.LibCommonTags;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemEquipmentSkin extends AbstractModItem {

    public ItemEquipmentSkin() {
        super(LibItemNames.EQUIPMENT_SKIN);
        setHasSubtypes(true);
    }
    
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
    }
    
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_) {
        if (stack.hasTagCompound()) {
            NBTTagCompound itemData = stack.getTagCompound();
            if (itemData.hasKey(LibCommonTags.TAG_ARMOUR_DATA)) {
                NBTTagCompound armourData = itemData.getCompoundTag(LibCommonTags.TAG_ARMOUR_DATA);
                if (armourData.hasKey(LibCommonTags.TAG_CUSTOM_NAME)) {
                    list.add("Name: " + armourData.getString(LibCommonTags.TAG_CUSTOM_NAME));
                }
                if (armourData.hasKey(LibCommonTags.TAG_AUTHOR_NAME)) {
                    list.add("Author: " + armourData.getString(LibCommonTags.TAG_AUTHOR_NAME));
                }
                if (armourData.hasKey(LibCommonTags.TAG_EQUPMENT_ID)) {
                    list.add("Equpment Id: " + armourData.getInteger(LibCommonTags.TAG_EQUPMENT_ID));
                }
                
            }
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
    
    @Override
    public IIcon getIconFromDamage(int damage) {
        if (damage < 5 & damage >= 0) {
            return icons[damage];
        }
        return super.getIconFromDamage(damage);
    }
}
