package riskyken.armourersWorkshop.common.inventory;

import java.util.HashMap;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.utils.NBTHelper;

public class WardrobeInventoryContainer {
    
    private static final String TAG_WARDROBE_CONTAINER = "wardrobeContainer";
    private static final String TAG_LEGACY_ITEMS = "items";
    
    private final HashMap<ISkinType, WardrobeInventory> skinInventorys;
    
    public WardrobeInventoryContainer(IInventorySlotUpdate callback, ISkinType[] validSkins) {
        skinInventorys = new HashMap<ISkinType, WardrobeInventory>();
        for (int i = 0; i < validSkins.length; i++) {
            skinInventorys.put(validSkins[i], new WardrobeInventory(callback, validSkins[i]));
        }
    }
    
    public WardrobeInventory getInventoryForSkinType(ISkinType skinType) {
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
        } else {
            if (compound.hasKey(TAG_LEGACY_ITEMS)) {
                ItemStack[] legacyItems = new ItemStack[7];
                NBTHelper.readStackArrayFromNBT(compound, TAG_LEGACY_ITEMS, legacyItems);
                getInventoryForSkinType(SkinTypeRegistry.skinHead).setInventorySlotContents(0, legacyItems[0]);
                getInventoryForSkinType(SkinTypeRegistry.skinChest).setInventorySlotContents(0, legacyItems[1]);
                getInventoryForSkinType(SkinTypeRegistry.skinLegs).setInventorySlotContents(0, legacyItems[2]);
                getInventoryForSkinType(SkinTypeRegistry.skinFeet).setInventorySlotContents(0, legacyItems[3]);
                getInventoryForSkinType(SkinTypeRegistry.skinSword).setInventorySlotContents(0, legacyItems[4]);
                getInventoryForSkinType(SkinTypeRegistry.skinBow).setInventorySlotContents(0, legacyItems[5]);
                getInventoryForSkinType(SkinTypeRegistry.skinArrow).setInventorySlotContents(0, legacyItems[6]);
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
