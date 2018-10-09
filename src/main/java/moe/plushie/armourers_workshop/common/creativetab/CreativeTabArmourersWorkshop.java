package moe.plushie.armourers_workshop.common.creativetab;

import java.util.Collections;
import java.util.Comparator;

import moe.plushie.armourers_workshop.common.blocks.ModBlocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabArmourersWorkshop extends CreativeTabs {

    public CreativeTabArmourersWorkshop(int id, String label) {
        super(id, label);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModBlocks.armourerBrain);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void displayAllRelevantItems(NonNullList<ItemStack> itemStackList) {
        NonNullList<ItemStack> items = NonNullList.<ItemStack>create();
        super.displayAllRelevantItems(items);
        Collections.sort(items, new ItemComparator());
        itemStackList.addAll(items);
    }
    
    private static class ItemComparator implements Comparator<ItemStack> {
        
        @Override
        public int compare(ItemStack stack1, ItemStack stack2) {
            if (stack1.getItem() instanceof ISortOrder && stack2.getItem() instanceof ISortOrder) {
                ISortOrder sort1 = (ISortOrder) stack1.getItem();
                ISortOrder sort2 = (ISortOrder) stack2.getItem();
                return sort2.getSortPriority() - sort1.getSortPriority();
            }
            return 0;
        }
    }
}
