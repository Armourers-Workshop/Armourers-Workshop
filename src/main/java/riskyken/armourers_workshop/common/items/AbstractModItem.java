package riskyken.armourers_workshop.common.items;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourers_workshop.ArmourersWorkshop;
import riskyken.armourers_workshop.common.creativetab.ISortOrder;
import riskyken.armourers_workshop.common.lib.LibModInfo;

public abstract class AbstractModItem extends Item implements ISortOrder {

    private int sortPriority = 0;
    
    public AbstractModItem(String name) {
        this(name, true);
    }
    
    public AbstractModItem(String name, boolean addCreativeTab) {
        if (addCreativeTab) {
            setCreativeTab(ArmourersWorkshop.tabArmorersWorkshop);
        }
        setUnlocalizedName(name);
        setHasSubtypes(false);
        setMaxStackSize(1);
        setNoRepair();
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        String unlocalized;
        String localized;

        unlocalized = stack.getUnlocalizedName() + ".flavour";
        localized = I18n.format(unlocalized);
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
    
    public AbstractModItem setSortPriority(int sortPriority) {
        this.sortPriority = sortPriority;
        return this;
    }
    
    @Override
    public int getSortPriority() {
        return sortPriority;
    }
}
