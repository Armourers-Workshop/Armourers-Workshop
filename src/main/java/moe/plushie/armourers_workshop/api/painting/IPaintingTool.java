package moe.plushie.armourers_workshop.api.painting;

import java.util.function.Consumer;

public interface IPaintingTool {

    void createToolProperties(Consumer<IPaintingToolProperty<?>> builder);
    
//    @Deprecated
//    public boolean getToolHasColour(ItemStack stack);
//
//    int getToolColour(ItemStack stack);
//
//    void setToolColour(ItemStack stack, int colour);
//
//    void setToolPaintType(ItemStack stack, ISkinPaintType paintType);
//
//    ISkinPaintType getToolPaintType(ItemStack stack);
}
