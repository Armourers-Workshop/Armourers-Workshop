package moe.plushie.armourers_workshop.common.inventory;

import java.util.HashMap;
import java.util.Set;

import moe.plushie.armourers_workshop.api.common.ISkinInventoryContainer;
import moe.plushie.armourers_workshop.api.common.skin.entity.ISkinnableEntity;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.inventory.ModInventory.IInventoryCallback;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SkinInventoryContainer implements ISkinInventoryContainer {
    
    private static final String TAG_WARDROBE_CONTAINER = "wardrobeContainer";
    
    private final HashMap<ISkinType, WardrobeInventory> skinInventorys;
    
    public SkinInventoryContainer(IInventoryCallback callback, ISkinType[] validSkins, int slotCount) {
        skinInventorys = new HashMap<ISkinType, WardrobeInventory>();
        for (int i = 0; i < validSkins.length; i++) {
            skinInventorys.put(validSkins[i], new WardrobeInventory(callback, validSkins[i], slotCount));
        }
    }
    
    public SkinInventoryContainer(IInventoryCallback callback, ISkinType[] validSkins, ISkinnableEntity skinnableEntity) {
        skinInventorys = new HashMap<ISkinType, WardrobeInventory>();
        for (int i = 0; i < validSkins.length; i++) {
            skinInventorys.put(validSkins[i], new WardrobeInventory(callback, validSkins[i], skinnableEntity.getSlotsForSkinType(validSkins[i])));
        }
    }
    
    public WardrobeInventory getSkinTypeInv(ISkinType skinType) {
        return skinInventorys.get(skinType);
    }
    
    @Override
    public void writeToNBT(NBTTagCompound compound) {
        NBTTagCompound containerCompound = new NBTTagCompound();
        Set skinTypes = skinInventorys.keySet();
        for (int i = 0; i < skinInventorys.size(); i++) {
            ISkinType skinType = (ISkinType) skinInventorys.keySet().toArray()[i];
            skinInventorys.get(skinType).writeItemsToNBT(containerCompound);
        }
        compound.setTag(TAG_WARDROBE_CONTAINER, containerCompound);
    }
    
    @Override
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
    
    @Override
    public void dropItems(World world, Vec3d pos) {
        Set skinTypes = skinInventorys.keySet();
        for (int i = 0; i < skinInventorys.size(); i++) {
            ISkinType skinType = (ISkinType) skinInventorys.keySet().toArray()[i];
            skinInventorys.get(skinType).dropItems(world, pos);
        }
    }

    @Override
    public void clear() {
        for (int i = 0; i < skinInventorys.size(); i++) {
            ISkinType skinType = (ISkinType) skinInventorys.keySet().toArray()[i];
            skinInventorys.get(skinType).clear();
        }
    }
}
