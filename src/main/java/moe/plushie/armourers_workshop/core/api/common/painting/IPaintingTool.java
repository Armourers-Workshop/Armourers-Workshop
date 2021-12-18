package moe.plushie.armourers_workshop.core.api.common.painting;

import moe.plushie.armourers_workshop.core.api.ISkinPaintType;
import net.minecraft.item.ItemStack;

public interface IPaintingTool {
    
    @Deprecated
    public boolean getToolHasColour(ItemStack stack);
    
    public int getToolColour(ItemStack stack);
    
    public void setToolColour(ItemStack stack, int colour);
    
    public void setToolPaintType(ItemStack stack, ISkinPaintType paintType);
    
    public ISkinPaintType getToolPaintType(ItemStack stack);
}
