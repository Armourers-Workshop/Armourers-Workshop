package extensions.net.minecraft.world.entity.player.Inventory;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

@Available("[1.18, )")
@Extension
public class CarriedProvider {

    public static void setCarried(@This Inventory inventory, ItemStack itemStack) {
        inventory.setPickedItem(itemStack);
    }
}
