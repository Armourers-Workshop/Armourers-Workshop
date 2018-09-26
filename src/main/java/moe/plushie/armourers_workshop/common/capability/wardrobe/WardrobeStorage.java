package moe.plushie.armourers_workshop.common.capability.wardrobe;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class WardrobeStorage implements IStorage<IWardrobeCapability> {

    private static final String TAG_SKIN_COLOUR = "skinColour";
    private static final String TAG_HAIR_COLOUR = "hairColour";
    private static final String TAG_ARMOUR_OVERRIDE = "armourOverride";
    /*
    private static final String TAG_SLOTS_UNLOCKED = "slotsUnlocked";
    private static final String TAG_SLOT_KEY = "slotKey";
    private static final String TAG_SLOT_VALUE = "slotValue";
    */
    
    @Override
    public NBTBase writeNBT(Capability<IWardrobeCapability> capability, IWardrobeCapability instance, EnumFacing side) {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger(TAG_SKIN_COLOUR, instance.getSkinColour());
        compound.setInteger(TAG_HAIR_COLOUR, instance.getHairColour());
        for (int i = 0; i < 4; i++) {
            compound.setBoolean(TAG_ARMOUR_OVERRIDE + i, instance.getArmourOverride().get(i));
        }
        return compound;
    }

    @Override
    public void readNBT(Capability<IWardrobeCapability> capability, IWardrobeCapability instance, EnumFacing side, NBTBase nbt) {
        NBTTagCompound compound = (NBTTagCompound) nbt;
        if (compound.hasKey(TAG_SKIN_COLOUR)) {
            instance.setSkinColour(compound.getInteger(TAG_SKIN_COLOUR));
        }
        if (compound.hasKey(TAG_HAIR_COLOUR)) {
            instance.setHairColour(compound.getInteger(TAG_HAIR_COLOUR));
        }
        for (int i = 0; i < 4; i++) {
            instance.getArmourOverride().set(i, compound.getBoolean(TAG_ARMOUR_OVERRIDE + i));
        }
    }
}
