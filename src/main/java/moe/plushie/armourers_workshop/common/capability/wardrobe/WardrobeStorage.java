package moe.plushie.armourers_workshop.common.capability.wardrobe;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class WardrobeStorage implements IStorage<IWardrobeCapability> {

    private static final String TAG_SKIN_COLOUR = "skinColour";
    private static final String TAG_HAIR_COLOUR = "hairColour";
    private static final String TAG_ARMOUR_OVERRIDE = "armourOverride";
    private static final String TAG_HEAD_OVERLAY = "headOverlay";
    private static final String TAG_LIMIT_LIMBS = "limitLimbs";
    private static final String TAG_SLOTS_UNLOCKED = "slotsUnlocked";
    private static final String TAG_SLOT_KEY = "slotKey";
    private static final String TAG_SLOT_VALUE = "slotValue";
    
    @Override
    public NBTBase writeNBT(Capability<IWardrobeCapability> capability, IWardrobeCapability instance, EnumFacing side) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void readNBT(Capability<IWardrobeCapability> capability, IWardrobeCapability instance, EnumFacing side, NBTBase nbt) {
        // TODO Auto-generated method stub
        
    }
}
