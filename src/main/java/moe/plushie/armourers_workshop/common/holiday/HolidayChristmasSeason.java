package moe.plushie.armourers_workshop.common.holiday;

import moe.plushie.armourers_workshop.common.blocks.ModBlocks;
import moe.plushie.armourers_workshop.common.items.ItemGiftSack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class HolidayChristmasSeason extends Holiday {

    public HolidayChristmasSeason(String name, int dayOfMonth, int month, int lengthInDays, int lengthInHours) {
        super(name, dayOfMonth, month, lengthInDays, lengthInHours);
    }
    
    @Override
    public boolean hasGift() {
        return true;
    }
    
    @Override
    public ItemStack getGift(EntityPlayer player) {
        return ItemGiftSack.createStack(0x990000, 0x267F00, new ItemStack(ModBlocks.doll));
    }
}
