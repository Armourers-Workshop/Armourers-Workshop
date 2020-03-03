package moe.plushie.armourers_workshop.common.creativetab;

import java.util.Collections;
import java.util.Comparator;

import moe.plushie.armourers_workshop.common.init.items.ModItems;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.painting.PaintTypeRegistry;
import moe.plushie.armourers_workshop.common.painting.PaintingHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabPaintingTools extends CreativeTabs {
    
    public CreativeTabPaintingTools() {
        super(CreativeTabs.getNextID(), LibModInfo.ID + "_painting_tools");
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public ItemStack createIcon() {
        ItemStack itemStack = new ItemStack(ModItems.PAINT_BRUSH);
        PaintingHelper.setToolPaint(itemStack, PaintTypeRegistry.PAINT_TYPE_RAINBOW);
        return itemStack;
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
