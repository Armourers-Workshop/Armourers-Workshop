package riskyken.armourersWorkshop.common.creativetab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;

public class CreativeTabArmourersWorkshop extends CreativeTabs {

    public CreativeTabArmourersWorkshop(int id, String label) {
        super(id, label);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public Item getTabIconItem() {
        return Item.getItemFromBlock(ModBlocks.armourerBrain);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void displayAllReleventItems(List list) {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        super.displayAllReleventItems(items);
        Collections.sort(items, new ItemComparator());
        list.addAll(items);
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
