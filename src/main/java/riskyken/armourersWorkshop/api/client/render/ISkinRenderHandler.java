package riskyken.armourersWorkshop.api.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.api.common.skin.data.ISkin;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;

public interface ISkinRenderHandler {

    public boolean renderSkinWithHelper(ItemStack stack);
    
    public boolean renderSkinWithHelper(ItemStack stack, ModelBiped modelBiped);
    
    public boolean renderSkinWithHelper(ItemStack stack, float limb1, float limb2, float limb3, float headY, float headX);
    
    public boolean renderSkinWithHelper(ISkinPointer skinPointer);
    
    public boolean renderSkinWithHelper(ISkinPointer skinPointer, ModelBiped modelBiped);
    
    public boolean renderSkinWithHelper(ISkinPointer skinPointer, float limb1, float limb2, float limb3, float headY, float headX);
    
    public boolean renderSkin(ItemStack stack);
    
    public boolean renderSkin(ISkinPointer skinPointer);
    
    public boolean renderSkinPart(ISkinPointer skinPointer, ISkinPartType skinPartType);
    
    public boolean isSkinInModelCache(ItemStack stack);
    
    public boolean isSkinInModelCache(ISkinPointer skinPointer);
    
    public void requestSkinModelFromSever(ItemStack stack);
    
    public void requestSkinModelFromSever(ISkinPointer skinPointer);
    
    public ModelBase getArmourerHandModel();
    
    public ISkin getSkinFromModelCache(ISkinPointer skinPointer);
}
