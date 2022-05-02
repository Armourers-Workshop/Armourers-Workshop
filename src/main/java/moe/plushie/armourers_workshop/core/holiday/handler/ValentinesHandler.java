package moe.plushie.armourers_workshop.core.holiday.handler;

import moe.plushie.armourers_workshop.core.holiday.Holiday;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

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
    public ItemStack getGift(PlayerEntity player) {
        return new ItemStack(Items.CAKE);
    }
}
