package moe.plushie.armourers_workshop.common.capability.entityskin;

import moe.plushie.armourers_workshop.common.inventory.ModInventory;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.util.Constants.NBT;

public class EntitySkinStorage implements IStorage<IEntitySkinCapability> {

    private static final String TAG_INVENTORY_OUTFIT = "inventory-outfit";
    
    @Override
    public NBTBase writeNBT(Capability<IEntitySkinCapability> capability, IEntitySkinCapability instance, EnumFacing side) {
        NBTTagCompound compound = new NBTTagCompound();
        instance.getSkinInventoryContainer().writeToNBT(compound);
        NBTTagCompound compoundOutfit = new NBTTagCompound();
        ((ModInventory)instance.getInventoryOutfits()).saveItemsToNBT(compoundOutfit);
        compound.setTag(TAG_INVENTORY_OUTFIT, compoundOutfit);
        return compound;
    }

    @Override
    public void readNBT(Capability<IEntitySkinCapability> capability, IEntitySkinCapability instance, EnumFacing side, NBTBase nbt) {
        NBTTagCompound compound = (NBTTagCompound) nbt;
        instance.getSkinInventoryContainer().readFromNBT(compound);
        if (compound.hasKey(TAG_INVENTORY_OUTFIT, NBT.TAG_COMPOUND)) {
            NBTTagCompound compoundOutfit = compound.getCompoundTag(TAG_INVENTORY_OUTFIT);
            ((ModInventory)instance.getInventoryOutfits()).loadItemsFromNBT(compoundOutfit);
        }
    }
}
