package moe.plushie.armourers_workshop.common.init.items.block;

import java.util.List;

import moe.plushie.armourers_workshop.client.config.ConfigHandlerClient;
import moe.plushie.armourers_workshop.common.creativetab.ISortOrder;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModItemBlockWithMetadata extends ItemBlock implements ISortOrder {

    public ModItemBlockWithMetadata(Block block) {
        super(block);
        setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedNameInefficiently(ItemStack par1ItemStack) {
        return super.getUnlocalizedNameInefficiently(par1ItemStack);
    }
    
    @Override
    public String getTranslationKey(ItemStack stack) {
        return getModdedUnlocalizedName(super.getTranslationKey(stack), stack);
    }
    
    protected String getModdedUnlocalizedName(String unlocalizedName, ItemStack stack) {
        String name = unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
        if (hasSubtypes) {
            return "tile." + LibModInfo.ID.toLowerCase() + ":" + name + "." + stack.getItemDamage();
        } else {
            return "tile." + LibModInfo.ID.toLowerCase() + ":" + name;
        }
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        String unlocalized;
        String localized;

        unlocalized = stack.getTranslationKey() + ".flavour";
        localized = I18n.format(unlocalized);
        if (!unlocalized.equals(localized)) {
            if (localized.contains("\r\n")) {
                String[] split = localized.split("\r\n");
                for (int i = 0; i < split.length; i++) {
                    tooltip.add(split[i]);
                }
            } else {
                tooltip.add(localized);
            }
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (block instanceof ISortOrder & ConfigHandlerClient.showSortOrderToolTip) {
            tooltip.add("sortPriority" + String.valueOf(getSortPriority()));
        }
    }
    
    @Override
    public int getSortPriority() {
        if (block instanceof ISortOrder) {
            return ((ISortOrder)block).getSortPriority();
        }
        return 100;
    }
}
