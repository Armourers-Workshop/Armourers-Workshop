package moe.plushie.armourers_workshop.core.holiday;

import moe.plushie.armourers_workshop.core.holiday.Holiday;
import moe.plushie.armourers_workshop.core.item.MannequinItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class HalloweenSeasonHandler implements Holiday.IHandler {

    @Override
    public int getBackgroundColor() {
        return 0xe05900;
    }

    @Override
    public int getForegroundColor() {
        return 0xeeeeee;
    }

    @Override
    public ItemStack getGift(Player player) {
        return MannequinItem.of(player, 2.0f);
    }
}
