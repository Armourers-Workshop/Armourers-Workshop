package riskyken.plushieWrapper.common.item;

import net.minecraft.item.ItemStack;

public class PlushieItemStack {

    private final ItemStack stack;
    
    public PlushieItemStack(ItemStack stack) {
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
