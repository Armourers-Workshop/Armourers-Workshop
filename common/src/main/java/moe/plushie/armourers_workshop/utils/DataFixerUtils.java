package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.init.ModLog;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class DataFixerUtils {

    public static void move(Container inventory, int src, int dest, int size, String reason) {
        int changes = 0;
        for (int i = size - 1; i >= 0; --i) {
            ItemStack itemStack = inventory.getItem(src + i);
            if (!itemStack.isEmpty()) {
                inventory.setItem(src + i, ItemStack.EMPTY);
                inventory.setItem(dest + i, itemStack);
                changes += 1;
            }
        }
        if (changes != 0) {
            ModLog.info("move {} items from {} - {}, to {} - {}, reason: {}", changes, src, src + size, dest, dest + size, reason);
        }
    }
}
