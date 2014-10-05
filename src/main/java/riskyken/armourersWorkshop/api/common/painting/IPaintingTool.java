package riskyken.armourersWorkshop.api.common.painting;

import net.minecraft.item.ItemStack;

public interface IPaintingTool {
    
    public boolean getToolHasColour(ItemStack stack);
    
    public int getToolColour(ItemStack stack);
    
    public void setToolColour(ItemStack stack, int colour);
}
