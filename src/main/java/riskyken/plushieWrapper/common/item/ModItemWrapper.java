package riskyken.plushieWrapper.common.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import riskyken.plushieWrapper.common.entity.PlushieEntityPlayer;

public class ModItemWrapper extends Item {

    private final PlushieItem item;
    
    public ModItemWrapper(PlushieItem item) {
        this.item = item;
        this.setUnlocalizedName(item.getName());
    }
    
    
    @Override
    public String getUnlocalizedName() {
        return getModdedUnlocalizedName(super.getUnlocalizedName());
    }
    
    @Override
    public CreativeTabs getCreativeTab() {
        if (item.getCreativeTab() != null) {
            return item.getCreativeTab().getMinecraftCreativeTab();
        }
        return null;
    }
    
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advancedTooltips) {
        item.addInformation(new PlushieItemStack(stack), new PlushieEntityPlayer(player), list, advancedTooltips);
        super.addInformation(stack, player, list, advancedTooltips);
    }
    
    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return getModdedUnlocalizedName(super.getUnlocalizedName(stack), new PlushieItemStack(stack));
    }
    
    private String getModdedUnlocalizedName(String unlocalizedName) {
        String name = unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
        if (hasSubtypes) {
            return "item." + item.getModId().toLowerCase() + ":" + name + ".0";
        } else {
            return "item." + item.getModId().toLowerCase() + ":" + name;
        }
    }
    
    private String getModdedUnlocalizedName(String unlocalizedName, PlushieItemStack stack) {
        String name = unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
        if (hasSubtypes) {
            return "item." + item.getModId().toLowerCase() + ":" + name + "." + stack.getItemDamage();
        } else {
            return "item." + item.getModId().toLowerCase() + ":" + name;
        }
    }
}
