package moe.plushie.armourers_workshop.api.client.render;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkin;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.item.ItemStack;

public interface ISkinRenderHandler {

    public boolean renderSkinWithHelper(ItemStack stack);
    
    public boolean renderSkinWithHelper(ItemStack stack, ModelBiped modelBiped);
    
    public boolean renderSkinWithHelper(ItemStack stack, float limb1, float limb2, float limb3, float headY, float headX);
    
    public boolean renderSkinWithHelper(ISkinDescriptor skinPointer);
    
    public boolean renderSkinWithHelper(ISkinDescriptor skinPointer, ModelBiped modelBiped);
    
    public boolean renderSkinWithHelper(ISkinDescriptor skinPointer, float limb1, float limb2, float limb3, float headY, float headX);
    
    public boolean renderSkin(ItemStack stack);
    
    public boolean renderSkin(ISkinDescriptor skinPointer);
    
    public boolean renderSkinPart(ISkinDescriptor skinPointer, ISkinPartType skinPartType);
    
    /**
     * Checks if the client has a skin in it's cache.
     * @return Returns true if the skin is in the cache.
     */
    public boolean isSkinInModelCache(ItemStack stack);
    
    /**
     * Checks if the client has a skin in it's cache.
     * @return Returns true if the skin is in the cache.
     */
    public boolean isSkinInModelCache(ISkinDescriptor skinPointer);
    
    public void requestSkinModelFromSever(ItemStack stack);
    
    public void requestSkinModelFromSever(ISkinDescriptor skinPointer);
    
    public ModelBase getArmourerHandModel();
    
    public ISkin getSkinFromModelCache(ISkinDescriptor skinPointer);
}
