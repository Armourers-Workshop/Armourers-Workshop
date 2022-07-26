package moe.plushie.armourers_workshop.library.blockentity;

import moe.plushie.armourers_workshop.core.blockentity.AbstractBlockEntity;
import moe.plushie.armourers_workshop.init.ModBlockEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

public class GlobalSkinLibraryBlockEntity extends AbstractBlockEntity {

    public GlobalSkinLibraryBlockEntity() {
        super(ModBlockEntities.SKIN_LIBRARY_GLOBAL.get());
    }

    @Override
    public void readFromNBT(CompoundTag nbt) {
    }

    @Override
    public void writeToNBT(CompoundTag nbt) {
    }
}
