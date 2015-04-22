package riskyken.armourersWorkshop.api.client.render;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;

public interface ISkinRenderHandler {

    public boolean renderSkinFromStack(ItemStack stack);
    
    public boolean renderSkinFromStack(ItemStack stack, ModelBiped modelBiped);
    
    public boolean renderSkinFromStack(ItemStack stack, float limb1, float limb2, float limb3, float headY, float headX);
    
    public boolean renderSkinFromPointer(ISkinPointer skinPointer);
    
    public boolean isSkinInModelCache(ItemStack stack);
    
    public boolean isSkinInModelCache(ISkinPointer skinPointer);
    
    public void requestSkinModelFromSever(ItemStack stack);
    
    public void requestSkinModelFromSever(ISkinPointer skinPointer);
}
