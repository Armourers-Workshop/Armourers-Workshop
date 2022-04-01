package moe.plushie.armourers_workshop.library.container;

import moe.plushie.armourers_workshop.core.item.BottleItem;
import moe.plushie.armourers_workshop.init.common.AWCore;
import moe.plushie.armourers_workshop.init.common.ModBlocks;
import moe.plushie.armourers_workshop.init.common.ModContainerTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;

@SuppressWarnings("NullableProblems")
public class GlobalSkinLibraryContainer extends Container {

    private final IWorldPosCallable access;

    public GlobalSkinLibraryContainer(int containerId, PlayerInventory playerInventory, IWorldPosCallable access) {
        super(ModContainerTypes.SKIN_LIBRARY_GLOBAL, containerId);
        this.access = access;
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return stillValid(this.access, player, ModBlocks.SKIN_LIBRARY_GLOBAL);
    }
}