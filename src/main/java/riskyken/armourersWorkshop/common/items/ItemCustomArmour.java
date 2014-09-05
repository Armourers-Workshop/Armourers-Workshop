package riskyken.armourersWorkshop.common.items;

import java.util.List;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import riskyken.armourersWorkshop.common.customarmor.ArmourType;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.proxies.ClientProxy;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCustomArmour extends AbstractModItemArmor {
    
    private static final String TAG_ARMOUR_DATA = "armourData";
    private static final String TAG_CUSTOM_NAME = "customName";
    
    private final ArmourType type;
    
    public ItemCustomArmour(ArmorMaterial armorMaterial, ArmourType armorType) {
        super(LibItemNames.CUSTOM_ARMOUR + "." + armorMaterial.name().toLowerCase() + "." + armorType.name().toLowerCase(),
                armorMaterial, armorType.getSlotId());
        this.type = armorType;
    }
    
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_) {
        if (stack.hasTagCompound()) {
            NBTTagCompound itemData = stack.getTagCompound();
            if (itemData.hasKey(TAG_CUSTOM_NAME)) {
                list.add(itemData.getString(TAG_CUSTOM_NAME)); 
            }
            if (itemData.hasKey(TAG_ARMOUR_DATA)) {
                list.add("Has armour data");
            }
        } else {
            list.add("ERROR NO DATA");
        }
        super.addInformation(stack, player, list, p_77624_4_);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        switch (type) {
        case NONE:
            break;
        case HEAD:
            itemIcon = register.registerIcon(LibModInfo.ID + ":" + "custom-head");
            break;
        case CHEST:
            itemIcon = register.registerIcon(LibModInfo.ID + ":" + "custom-chest");
            break;
        case LEGS:
            itemIcon = register.registerIcon(LibModInfo.ID + ":" + "custom-legs");
            break;
        case SKIRT:
            itemIcon = register.registerIcon(LibModInfo.ID + ":" + "custom-skirt");
            break;
        case FEET:
            itemIcon = register.registerIcon(LibModInfo.ID + ":" + "custom-feet");
            break;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot) {
        ModelBiped armorModel = null;
        if (itemStack != null) {
            if (itemStack.getItem() instanceof ItemCustomArmour) {
                switch (type) {
                case NONE:
                    return null;
                case HEAD:
                    return ClientProxy.customHead;
                case CHEST:
                    return ClientProxy.customChest;
                case LEGS:
                    return ClientProxy.customLegs;
                case SKIRT:
                    return ClientProxy.customSkirt;
                case FEET:
                    return ClientProxy.customFeet;
                }
            }
        }
        return null;
    }
}
