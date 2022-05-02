package moe.plushie.armourers_workshop.core.holiday.handler;

import moe.plushie.armourers_workshop.core.holiday.Holiday;
import moe.plushie.armourers_workshop.core.item.MannequinItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class ChristmasSeasonHandler implements Holiday.IHandler {

    @Override
    public int getBackgroundColor() {
        return 0x990000;
    }

    @Override
    public int getForegroundColor() {
        return 0x267f00;
    }

    @Override
    public ItemStack getGift(PlayerEntity player) {
        return MannequinItem.of(player, 0.5f);
    }
}
