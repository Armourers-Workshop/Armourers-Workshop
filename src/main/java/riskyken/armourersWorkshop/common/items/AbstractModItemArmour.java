package riskyken.armourersWorkshop.common.items;

import java.util.List;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.api.common.lib.LibCommonTags;
import riskyken.armourersWorkshop.client.model.equipmet.AbstractModelCustomEquipment;
import riskyken.armourersWorkshop.client.render.EquipmentModelRenderer;
import riskyken.armourersWorkshop.common.equipment.data.CustomEquipmentItemData;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AbstractModItemArmour extends ItemArmor {

    public AbstractModItemArmour(String name, ArmorMaterial armorMaterial, int armorType, boolean addCreativeTab) {
        super(armorMaterial, 2, armorType);
        if (addCreativeTab) {
            setCreativeTab(ArmourersWorkshop.tabArmorersWorkshop);
        }
        setUnlocalizedName(name);
        setHasSubtypes(false);
        setMaxStackSize(1);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        String cGreen = EnumChatFormatting.GREEN.toString();
        String cGray = EnumChatFormatting.GRAY.toString();
        String cRed = EnumChatFormatting.RED.toString();
        String cGold = EnumChatFormatting.GOLD.toString();
        String unlocalized;
        String localized;
        
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
        }
        
        unlocalized = stack.getUnlocalizedName() + ".flavour";
        localized = StatCollector.translateToLocal(unlocalized);
        if (!unlocalized.equals(localized)) {
            if (localized.contains("%n")) {
                String[] split = localized.split("%n");
                for (int i = 0; i < split.length; i++) {
                    list.add(split[i]);
                }
            } else {
                list.add(localized);
            }
        }
        
        super.addInformation(stack, player, list, par4);
    }

    @Override
    public Item setUnlocalizedName(String name) {
        GameRegistry.registerItem(this, name);
        return super.setUnlocalizedName(name);
    }

    @Override
    public String getUnlocalizedName() {
        return getModdedUnlocalizedName(super.getUnlocalizedName());
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        return getModdedUnlocalizedName(super.getUnlocalizedName(itemStack), itemStack);
    }

    protected String getModdedUnlocalizedName(String unlocalizedName) {
        String name = unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
        if (hasSubtypes) {
            return "item." + LibModInfo.ID.toLowerCase() + ":" + name + ".0";
        } else {
            return "item." + LibModInfo.ID.toLowerCase() + ":" + name;
        }
    }
    
    protected String getModdedUnlocalizedName(String unlocalizedName, ItemStack stack) {
        String name = unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
        if (hasSubtypes) {
            return "item." + LibModInfo.ID.toLowerCase() + ":" + name + "." + stack.getItemDamage();
        } else {
            return "item." + LibModInfo.ID.toLowerCase() + ":" + name;
        }
    }
    
    @Override
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack stack, int armorSlot) {
        if (!stack.hasTagCompound()) {
            return null;
        }
        NBTTagCompound itemData = stack.getTagCompound();
        
        if (!itemData.hasKey(LibCommonTags.TAG_ARMOUR_DATA)) {
            return null;
        }
        NBTTagCompound armourData = itemData.getCompoundTag(LibCommonTags.TAG_ARMOUR_DATA);
        if (!armourData.hasKey(LibCommonTags.TAG_CUSTOM_NAME)) {
            return null;
        }
        
        if (!armourData.hasKey(LibCommonTags.TAG_EQUIPMENT_ID)) {
            return null;
        }
        int equipmentId = armourData.getInteger(LibCommonTags.TAG_EQUIPMENT_ID);
        
        AbstractModelCustomEquipment targetModel = null;
        EquipmentModelRenderer emr = EquipmentModelRenderer.INSTANCE;
        CustomEquipmentItemData data = emr.getCustomArmourItemData(equipmentId);
        if (data == null) {
            return null;
        }
        
        switch (data.getType()) {
        case HEAD:
            targetModel = emr.customHead;
            break;
        case CHEST:
            targetModel = emr.customChest;
            break;
        case LEGS:
            targetModel = emr.customLegs;
            break;
        case SKIRT:
            targetModel = emr.customSkirt;
            break;
        case FEET:
            targetModel = emr.customFeet;
            break;
        default:
            break;
        }
        
        if (targetModel == null) {
            return null;
        }
        
        targetModel.npcEquipmentData = data;
        return targetModel;
    }
}
