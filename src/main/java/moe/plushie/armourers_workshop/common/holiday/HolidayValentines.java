package moe.plushie.armourers_workshop.common.holiday;

import moe.plushie.armourers_workshop.common.items.ItemGiftSack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class HolidayValentines extends Holiday {

    public HolidayValentines(String name, int dayOfMonth, int month, int lengthInDays, int lengthInHours) {
        super(name, dayOfMonth, month, lengthInDays, lengthInHours);
    }

    @Override
    public boolean hasGift() {
        return true;
    }
    
    @Override
    public ItemStack getGift(EntityPlayer player) {
        return ItemGiftSack.createStack(0xE5A2E5, 0x961596, new ItemStack(Items.CAKE));
    }
}
