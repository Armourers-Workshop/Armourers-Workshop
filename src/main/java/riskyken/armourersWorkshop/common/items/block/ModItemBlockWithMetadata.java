package riskyken.armourersWorkshop.common.items.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ModItemBlockWithMetadata extends ItemBlock {

    public ModItemBlockWithMetadata(Block block) {
        super(block);
    }
    
    @Override
    public int getMetadata(int damage) {
        return damage;
    }
    
    @Override
    public String getUnlocalizedNameInefficiently(ItemStack par1ItemStack) {
        return super.getUnlocalizedNameInefficiently(par1ItemStack);
    }
    
    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return block.getUnlocalizedName() + stack.getItemDamage();
    }
    
    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
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
        super.addInformation(stack, playerIn, tooltip, advanced);
    }
    
    
    /*
     * 

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
        String unlocalized;
        String localized;

        unlocalized = itemStack.getUnlocalizedName() + ".flavour";
        localized = I18n.format(unlocalized);
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
        
        super.addInformation(itemStack, player, list, par4);
    }*/
}
