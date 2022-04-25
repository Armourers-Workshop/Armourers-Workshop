package moe.plushie.armourers_workshop.library.container;

import moe.plushie.armourers_workshop.init.common.ModBlocks;
import moe.plushie.armourers_workshop.init.common.ModContainerTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.IWorldPosCallable;

@SuppressWarnings("NullableProblems")
public class CreativeSkinLibraryContainer extends SkinLibraryContainer {

    public CreativeSkinLibraryContainer(int containerId, PlayerInventory playerInventory, IWorldPosCallable access) {
        super(containerId, ModContainerTypes.SKIN_LIBRARY_CREATIVE, ModBlocks.SKIN_LIBRARY_CREATIVE, playerInventory, access);
    }

    @Override
    public boolean shouldLoadStack() {
        return getOutputStack().isEmpty();
    }
}