package moe.plushie.armourers_workshop.core.holiday;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ValentinesHandler implements Holiday.IHandler {

    @Override
    public int backgroundColor() {
        return 0xe5a2e5;
    }

    @Override
    public int foregroundColor() {
        return 0x961596;
    }

    @Override
    public ItemStack getGift(Player player) {
        return new ItemStack(Items.CAKE);
    }
}
