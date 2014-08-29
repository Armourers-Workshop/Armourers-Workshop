package riskyken.armourersWorkshop.common.items;

import net.minecraft.item.ItemStack;

public interface IColourTool {
    
    public boolean getToolHasColour(ItemStack stack);
    
    public int getToolColour(ItemStack stack);
    
    public void setToolColour(ItemStack stack, int colour);
}
