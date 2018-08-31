package riskyken.armourers_workshop.common.items;

import java.util.List;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourers_workshop.ArmourersWorkshop;
import riskyken.armourers_workshop.client.model.skin.AbstractModelSkin;
import riskyken.armourers_workshop.client.render.SkinModelRenderer;
import riskyken.armourers_workshop.common.creativetab.ISortOrder;
import riskyken.armourers_workshop.common.lib.LibModInfo;
import riskyken.armourers_workshop.common.skin.data.Skin;
import riskyken.armourers_workshop.common.skin.data.SkinPointer;
import riskyken.armourers_workshop.utils.SkinNBTHelper;

public class AbstractModItemArmour extends ItemArmor implements ISortOrder {

    private int sortPriority = 0;
    
    public AbstractModItemArmour(String name, ArmorMaterial armorMaterial, EntityEquipmentSlot armorType, boolean addCreativeTab) {
        super(armorMaterial, 2, armorType);
        if (addCreativeTab) {
            setCreativeTab(ArmourersWorkshop.tabArmorersWorkshop);
        }
        setUnlocalizedName(name);
        setHasSubtypes(false);
        setMaxStackSize(1);
        ModItems.ITEM_LIST.add(this);
    }
    
    @Override
    public Item setUnlocalizedName(String unlocalizedName) {
        super.setUnlocalizedName(unlocalizedName);
        setRegistryName(new ResourceLocation(LibModInfo.ID, "item." + unlocalizedName));
        return this;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        String unlocalized = stack.getUnlocalizedName() + ".flavour";
        String localized = I18n.format(unlocalized);
        if (!unlocalized.equals(localized)) {
            if (localized.contains("%n")) {
                String[] split = localized.split("%n");
                for (int i = 0; i < split.length; i++) {
                    tooltip.add(split[i]);
                }
            } else {
                tooltip.add(localized);
            }
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
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
    
    @SideOnly(Side.CLIENT)
    @Override
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
        if (!SkinNBTHelper.stackHasSkinData(itemStack)) {
            return null;
        }
        
        SkinPointer skinData = SkinNBTHelper.getSkinPointerFromStack(itemStack);
        
        AbstractModelSkin targetModel = null;
        SkinModelRenderer emr = SkinModelRenderer.INSTANCE;
        Skin data = emr.getCustomArmourItemData(skinData);
        if (data == null) {
            return null;
        }
        
        targetModel = emr.getModelForEquipmentType(data.getSkinType());
        
        if (targetModel == null) {
            return null;
        }
        
        targetModel.npcSkinData = data;
        targetModel.npcDyeData = skinData.getSkinDye();
        return targetModel;
    }
    
    public AbstractModItemArmour setSortPriority(int sortPriority) {
        this.sortPriority = sortPriority;
        return this;
    }

    @Override
    public int getSortPriority() {
        return sortPriority;
    }
}
