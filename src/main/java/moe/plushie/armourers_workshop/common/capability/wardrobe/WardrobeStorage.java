package moe.plushie.armourers_workshop.common.capability.wardrobe;

import moe.plushie.armourers_workshop.api.common.IExtraColours.ExtraColourType;
import moe.plushie.armourers_workshop.api.common.capability.IWardrobeCap;
import moe.plushie.armourers_workshop.common.skin.data.SkinDye;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.util.Constants.NBT;

public class WardrobeStorage implements IStorage<IWardrobeCap> {

    private static final String TAG_EXTRA_COLOUR = "extra-colour-";
    private static final String TAG_DYE = "dye";
    
    @Override
    public NBTBase writeNBT(Capability<IWardrobeCap> capability, IWardrobeCap instance, EnumFacing side) {
        NBTTagCompound compound = new NBTTagCompound();
        for (int i = 0; i < ExtraColourType.values().length; i++) {
            ExtraColourType type = ExtraColourType.values()[i];
            compound.setInteger(TAG_EXTRA_COLOUR + type.toString().toLowerCase(), instance.getExtraColours().getColour(type));
        }

        NBTTagCompound dyeCompund = new NBTTagCompound();
        ((SkinDye)instance.getDye()).writeToCompound(dyeCompund);
        compound.setTag(TAG_DYE, dyeCompund);
        return compound;
    }

    @Override
    public void readNBT(Capability<IWardrobeCap> capability, IWardrobeCap instance, EnumFacing side, NBTBase nbt) {
        NBTTagCompound compound = (NBTTagCompound) nbt;
        for (int i = 0; i < ExtraColourType.values().length; i++) {
            ExtraColourType type = ExtraColourType.values()[i];
            if (compound.hasKey(TAG_EXTRA_COLOUR + type.toString().toLowerCase(), NBT.TAG_INT)) {
                instance.getExtraColours().setColour(type, compound.getInteger(TAG_EXTRA_COLOUR + type.toString().toLowerCase()));
            }
        }

        if (compound.hasKey(TAG_DYE, NBT.TAG_COMPOUND)) {
            NBTTagCompound dyeCompund = compound.getCompoundTag(TAG_DYE);
            ((SkinDye)instance.getDye()).readFromCompound(dyeCompund);
        }
    }
}
