package riskyken.armourersWorkshop.api.common.painting;

import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.painting.PaintType;

public interface IPaintingTool {
    
    public boolean getToolHasColour(ItemStack stack);
    
    public int getToolColour(ItemStack stack);
    
    public void setToolColour(ItemStack stack, int colour);
    
    public void setToolPaintType(ItemStack stack, PaintType paintType);
    
    public PaintType getToolPaintType(ItemStack stack);
}
