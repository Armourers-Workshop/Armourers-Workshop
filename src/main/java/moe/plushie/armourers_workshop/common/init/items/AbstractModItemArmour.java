package moe.plushie.armourers_workshop.common.init.items;

import java.util.List;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.client.config.ConfigHandlerClient;
import moe.plushie.armourers_workshop.client.model.ICustomModel;
import moe.plushie.armourers_workshop.client.model.skin.ModelTypeHelper;
import moe.plushie.armourers_workshop.client.render.SkinModelRenderHelper;
import moe.plushie.armourers_workshop.client.render.SkinModelRenderHelper.ModelType;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.creativetab.ISortOrder;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AbstractModItemArmour extends ItemArmor implements ISortOrder, ICustomModel {

    private int sortPriority = 0;
    
    public AbstractModItemArmour(String name, ArmorMaterial armorMaterial, EntityEquipmentSlot armorType, boolean addCreativeTab) {
        super(armorMaterial, 2, armorType);
        if (addCreativeTab) {
            setCreativeTab(ArmourersWorkshop.TAB_MAIN);
        }
        setTranslationKey(name);
        setHasSubtypes(false);
        setMaxStackSize(1);
        ModItems.ITEM_LIST.add(this);
    }
    
    @Override
    public Item setTranslationKey(String unlocalizedName) {
        super.setTranslationKey(unlocalizedName);
        setRegistryName(new ResourceLocation(LibModInfo.ID, "item." + unlocalizedName));
        return this;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        String unlocalized = stack.getTranslationKey() + ".flavour";
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
        if (ConfigHandlerClient.showSortOrderToolTip) {
            tooltip.add("sortPriority" + String.valueOf(sortPriority));
        }
    }
    
    @Override
    public String getTranslationKey(ItemStack stack) {
        return getModdedUnlocalizedName(super.getTranslationKey(stack), stack);
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
        
        SkinDescriptor skinData = SkinNBTHelper.getSkinDescriptorFromStack(itemStack);
        
        ModelTypeHelper targetModel = null;
        SkinModelRenderHelper emr = SkinModelRenderHelper.INSTANCE;
        Skin data = ClientSkinCache.INSTANCE.getSkin(skinData);
        if (data == null) {
            return null;
        }
        
        targetModel = emr.getTypeHelperForModel(ModelType.MODEL_BIPED, data.getSkinType());
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
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerModels() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(new ResourceLocation(LibModInfo.ID, getTranslationKey()), "inventory"));
    }
}
