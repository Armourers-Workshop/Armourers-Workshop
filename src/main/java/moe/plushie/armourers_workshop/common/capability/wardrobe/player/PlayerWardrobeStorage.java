package moe.plushie.armourers_workshop.common.capability.wardrobe.player;

import moe.plushie.armourers_workshop.common.capability.wardrobe.IWardrobeCap;
import moe.plushie.armourers_workshop.common.capability.wardrobe.WardrobeCap;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class PlayerWardrobeStorage implements IStorage<IPlayerWardrobeCap> {

    private static final String TAG_ARMOUR_OVERRIDE = "armourOverride";
    /*
    private static final String TAG_SLOTS_UNLOCKED = "slotsUnlocked";
    private static final String TAG_SLOT_KEY = "slotKey";
    private static final String TAG_SLOT_VALUE = "slotValue";
    */
    
    @Override
    public NBTBase writeNBT(Capability<IPlayerWardrobeCap> capability, IPlayerWardrobeCap instance, EnumFacing side) {
        IStorage<IWardrobeCap> storage = WardrobeCap.WARDROBE_CAP.getStorage();
        NBTTagCompound compound = (NBTTagCompound) storage.writeNBT(WardrobeCap.WARDROBE_CAP, instance, side);
        for (int i = 0; i < 4; i++) {
            compound.setBoolean(TAG_ARMOUR_OVERRIDE + i, instance.getArmourOverride(EntityEquipmentSlot.values()[i + 2]));
        }
        return compound;
    }

    @Override
    public void readNBT(Capability<IPlayerWardrobeCap> capability, IPlayerWardrobeCap instance, EnumFacing side, NBTBase nbt) {
        NBTTagCompound compound = (NBTTagCompound) nbt;
        IStorage<IWardrobeCap> storage = WardrobeCap.WARDROBE_CAP.getStorage();
        storage.readNBT(WardrobeCap.WARDROBE_CAP, instance, side, compound);
        for (int i = 0; i < 4; i++) {
            instance.setArmourOverride(EntityEquipmentSlot.values()[i + 2], compound.getBoolean(TAG_ARMOUR_OVERRIDE + i));
        }
    }
}
