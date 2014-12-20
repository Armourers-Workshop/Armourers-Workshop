package riskyken.armourersWorkshop.common.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;

import org.lwjgl.input.Keyboard;

import riskyken.armourersWorkshop.api.common.lib.LibCommonTags;
import riskyken.armourersWorkshop.client.settings.Keybindings;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemEquipmentSkin extends AbstractModItem {

    public ItemEquipmentSkin() {
        super(LibItemNames.EQUIPMENT_SKIN, false);
        setHasSubtypes(true);
    }
    
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < 7; i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }
    
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_) {
        String cGreen = EnumChatFormatting.GREEN.toString();
        String cGray = EnumChatFormatting.GRAY.toString();
        String cRed = EnumChatFormatting.RED.toString();
        String cGold = EnumChatFormatting.GOLD.toString();
        if (stack.hasTagCompound()) {
            
            NBTTagCompound itemData = stack.getTagCompound();
            if (itemData.hasKey(LibCommonTags.TAG_ARMOUR_DATA)) {
                NBTTagCompound armourData = itemData.getCompoundTag(LibCommonTags.TAG_ARMOUR_DATA);
                if (armourData.hasKey(LibCommonTags.TAG_CUSTOM_NAME)) {
                    if (!armourData.getString(LibCommonTags.TAG_CUSTOM_NAME).trim().isEmpty()) {
                        list.add(cGold + "Name: " + cGray + armourData.getString(LibCommonTags.TAG_CUSTOM_NAME));
                    }
                    
                }
                if (armourData.hasKey(LibCommonTags.TAG_AUTHOR_NAME)) {
                    list.add(cGold + "Author: " + cGray + armourData.getString(LibCommonTags.TAG_AUTHOR_NAME));
                }
                if (armourData.hasKey(LibCommonTags.TAG_EQUIPMENT_ID)) {
                    list.add(cGold + "Equipment Id: " + cGray + armourData.getInteger(LibCommonTags.TAG_EQUIPMENT_ID));
                }
                
            }
            String keyName = Keyboard.getKeyName(Keybindings.openCustomArmourGui.getKeyCode());

            keyName = cGreen + keyName + cGray;
            list.add("Press the " + keyName + " key to open the " + cGreen + "Equipment Wardrobe");
        } else {
            list.add(cRed + "ERROR: Invalid equipment skin.");
            list.add(cRed + "Please delete.");
        }
        

        super.addInformation(stack, player, list, p_77624_4_);
    }
    
    @Override
    public int getRenderPasses(int metadata) {
        return 2;
    }
    
    @Override
    public boolean requiresMultipleRenderPasses() {
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    private IIcon[] icons;
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        icons = new IIcon[8];
        icons[0] = register.registerIcon(LibModInfo.ID + ":" + "templateHead");
        icons[1] = register.registerIcon(LibModInfo.ID + ":" + "templateChest");
        icons[2] = register.registerIcon(LibModInfo.ID + ":" + "templateLegs");
        icons[3] = register.registerIcon(LibModInfo.ID + ":" + "templateSkirt");
        icons[4] = register.registerIcon(LibModInfo.ID + ":" + "templateFeet");
        icons[5] = register.registerIcon(LibModInfo.ID + ":" + "templateWeapon");
        icons[6] = register.registerIcon(LibModInfo.ID + ":" + "templateBow");
        icons[7] = register.registerIcon(LibModInfo.ID + ":" + "templateLoading");
    }
    
    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        int damage = stack.getItemDamage();
        
        if (pass == 1) {
            return icons[7];
        }
        
        if (damage < 7 & damage >= 0) {
            return icons[damage];
        }
        return super.getIcon(stack, pass);
    }
}
