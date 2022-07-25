package moe.plushie.armourers_workshop.core.holiday.handler;

import moe.plushie.armourers_workshop.core.holiday.Holiday;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ValentinesHandler implements Holiday.IHandler {

    @Override
    public int getBackgroundColor() {
        return 0xe5a2e5;
    }

    @Override
    public int getForegroundColor() {
        return 0x961596;
    }

    @Override
    public ItemStack getGift(Player player) {
        return new ItemStack(Items.CAKE);
    }
}
