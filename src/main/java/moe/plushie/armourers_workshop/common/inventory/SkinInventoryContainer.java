package moe.plushie.armourers_workshop.common.inventory;

import java.util.HashMap;
import java.util.Set;

import moe.plushie.armourers_workshop.api.common.skin.entity.ISkinnableEntity;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class SkinInventoryContainer {
    
    private static final String TAG_WARDROBE_CONTAINER = "wardrobeContainer";
    
    private final HashMap<ISkinType, WardrobeInventory> skinInventorys;
    
    public SkinInventoryContainer(IInventorySlotUpdate callback, ISkinType[] validSkins, ISkinnableEntity skinnableEntity) {
        skinInventorys = new HashMap<ISkinType, WardrobeInventory>();
        for (int i = 0; i < validSkins.length; i++) {
            skinInventorys.put(validSkins[i], new WardrobeInventory(callback, validSkins[i], skinnableEntity.getSlotsForSkinType(validSkins[i])));
        }
    }
    
    public WardrobeInventory getSkinTypeInv(ISkinType skinType) {
        return skinInventorys.get(skinType);
    }
    
    public void writeToNBT(NBTTagCompound compound) {
        NBTTagCompound containerCompound = new NBTTagCompound();
        Set skinTypes = skinInventorys.keySet();
        for (int i = 0; i < skinInventorys.size(); i++) {
            ISkinType skinType = (ISkinType) skinInventorys.keySet().toArray()[i];
            skinInventorys.get(skinType).writeItemsToNBT(containerCompound);
        }
        compound.setTag(TAG_WARDROBE_CONTAINER, containerCompound);
    }
    
    public void readFromNBT(NBTTagCompound compound) {
        if (compound.hasKey(TAG_WARDROBE_CONTAINER, 10)) {
            NBTTagCompound containerCompound = compound.getCompoundTag(TAG_WARDROBE_CONTAINER);
            Set skinTypes = skinInventorys.keySet();
            for (int i = 0; i < skinInventorys.size(); i++) {
                ISkinType skinType = (ISkinType) skinInventorys.keySet().toArray()[i];
                skinInventorys.get(skinType).readItemsFromNBT(containerCompound);
            }
        }
    }
    
    public void dropItems(EntityPlayer player) {
        Set skinTypes = skinInventorys.keySet();
        for (int i = 0; i < skinInventorys.size(); i++) {
            ISkinType skinType = (ISkinType) skinInventorys.keySet().toArray()[i];
            skinInventorys.get(skinType).dropItems(player);
        }
    }
}
