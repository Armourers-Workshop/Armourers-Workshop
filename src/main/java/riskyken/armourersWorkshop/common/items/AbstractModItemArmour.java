package riskyken.armourersWorkshop.common.items;

import java.util.List;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.client.model.equipmet.AbstractModelCustomEquipment;
import riskyken.armourersWorkshop.client.render.EquipmentModelRenderer;
import riskyken.armourersWorkshop.common.equipment.data.CustomEquipmentItemData;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper.SkinNBTData;
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
        
        if (EquipmentNBTHelper.stackHasSkinData(stack)) {
            SkinNBTData skinData = EquipmentNBTHelper.getSkinNBTDataFromStack(stack);
            list.add(cGold + "Equipment Id: " + cGray + skinData.skinId);
        } else {
            
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
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack stack, int armorSlot) {
        
        if (!EquipmentNBTHelper.stackHasSkinData(stack)) {
            return null;
        }
        
        SkinNBTData skinData = EquipmentNBTHelper.getSkinNBTDataFromStack(stack);
        
        AbstractModelCustomEquipment targetModel = null;
        EquipmentModelRenderer emr = EquipmentModelRenderer.INSTANCE;
        CustomEquipmentItemData data = emr.getCustomArmourItemData(skinData.skinId);
        if (data == null) {
            return null;
        }
        
        targetModel = emr.getModelForEquipmentType(data.getSkinType());
        
        if (targetModel == null) {
            return null;
        }
        
        targetModel.npcEquipmentData = data;
        return targetModel;
    }
}
