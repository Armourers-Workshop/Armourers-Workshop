package moe.plushie.armourers_workshop.core.wardrobe;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class SkinWardrobeStorage implements Capability.IStorage<SkinWardrobe> {

    @Nullable
    @Override
    public INBT writeNBT(Capability<SkinWardrobe> capability, SkinWardrobe instance, Direction side) {
        return null;
    }

    @Override
    public void readNBT(Capability<SkinWardrobe> capability, SkinWardrobe instance, Direction side, INBT nbt) {
    }
}
