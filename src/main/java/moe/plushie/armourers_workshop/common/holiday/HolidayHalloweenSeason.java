package moe.plushie.armourers_workshop.common.holiday;

import moe.plushie.armourers_workshop.common.init.items.ItemGiftSack;
import moe.plushie.armourers_workshop.common.init.items.ItemMannequin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class HolidayHalloweenSeason extends Holiday {

    public HolidayHalloweenSeason(String name, int dayOfMonth, int month, int lengthInDays, int lengthInHours) {
        super(name, dayOfMonth, month, lengthInDays, lengthInHours);
    }
    
    @Override
    public ItemStack getGiftSack() {
        return ItemGiftSack.createStack(0xE05900, 0xEEEEEE, this);
    }
    
    @Override
    public ItemStack getGift(EntityPlayer player) {
        return ItemMannequin.create(player, 2F);
    }
}
