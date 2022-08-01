package moe.plushie.armourers_workshop.api.common;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;

public interface IEntityHandler {

    /**
     * Called when a user uses the creative pick block button on this entity.
     *
     * @param target The full target the player is looking at
     * @return A ItemStack to add to the player's inventory, empty ItemStack if nothing should be added.
     */
    default ItemStack getCustomPickResult(HitResult target) {
        return ItemStack.EMPTY;
    }
}
