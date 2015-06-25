package riskyken.minecraftWrapper.common.item;

import net.minecraft.item.ItemStack;

public class ItemStackPointer {

    private final ItemStack stack;
    
    public ItemStackPointer(ItemStack stack) {
        this.stack = stack;
    }

    public int getItemDamage() {
        return stack.getItemDamage();
    }
    
    public String getUnlocalizedName() {
        return stack.getUnlocalizedName();
    }
    
    public ItemStack getMinecraftStack() {
        return stack;
    }
}
