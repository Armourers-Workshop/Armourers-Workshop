package moe.plushie.armourers_workshop.library.container;

import moe.plushie.armourers_workshop.init.common.ModBlocks;
import moe.plushie.armourers_workshop.init.common.ModContainerTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.IWorldPosCallable;

@SuppressWarnings("NullableProblems")
public class CreativeSkinLibraryContainer extends SkinLibraryContainer {

    public CreativeSkinLibraryContainer(int containerId, PlayerInventory playerInventory, IWorldPosCallable access) {
        super(ModContainerTypes.SKIN_LIBRARY_CREATIVE, containerId, playerInventory, access);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return stillValid(this.access, player, ModBlocks.SKIN_LIBRARY_CREATIVE);
    }
}